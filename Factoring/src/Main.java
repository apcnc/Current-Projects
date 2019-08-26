import java.io.StreamTokenizer;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;


public class Main {
	static ArrayList<BigInteger> primes;
	static IOManager sql = new IOManager();
	static Timer timer = new Timer();
	static BigInteger highestSaveNumber;
	public static void main(String[] args) {
		int fieldsToCalculate=1;
		boolean calculateSite = true;
		/*System.out.println(sql.connectToMysql("h2828143.stratoserver.net", "apcncFactor", "apcnc", "E01wmrW%"));
		//printDuplicates();
		showAllNumbersAndPrimes();
		
		System.out.println("finish");
		
		//sql.addFields(10, 10000);
		sql.closeConnection();*/
		
		
		System.out.println(sql.connectToMysql("h2828143.stratoserver.net", "apcncFactor", "apcnc", "E01wmrW%"));
		sql.initDBConnection();
		showAllNumbersAndPrimes();
		Timer timer = new Timer();
		int fields = 0;
		timer.start();
		primes = sql.getPrimes();
		//showAllNumbersAndPrimes();
		timer.end();
		timer.printTime("get Numbers");
		sql.addFields(1, 10000);
		ArrayList<BigInteger> field = sql.getNextNumberField();
		Timer fieldTimer = new Timer();
		fieldTimer.start();
		while(field.size()>0) {
			Timer localTimer = new Timer();
			calcField(field);
			fields++;
			localTimer.start();
			sql.closeConnection();
			localTimer.end();
			localTimer.printTime("closing connection and inserting numbers");
			sql.connectToMysql("h2828143.stratoserver.net", "apcncFactor", "apcnc", "E01wmrW%");
			if(fields<fieldsToCalculate) {
				field = sql.getNextNumberField();
			}else {
				field = new ArrayList<BigInteger>();
			}
		}
		fieldTimer.end();
		fieldTimer.printTime("total time");
		sql.setDBMode(true);
		while(calculateSite) {
			sql.closeConnection();
			sql.connectToMysql("h2828143.stratoserver.net", "apcncFactor", "apcnc", "E01wmrW%");
			calcNumbers();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				calculateSite = false;
				e.printStackTrace();
			}
		}
		sql.addFields(50, 10000);
		sql.closeConnection();
		System.out.println("calculated "+fields+" fields");
	}
	private static void printDuplicates() {
		String command;
		command="SELECT Number, COUNT(Number) FROM FactorDB GROUP BY Number HAVING COUNT(Number) > '1';";
		ResultSet result = sql.excecuteQuerry(command);
		try {
			while(result.next()) {
				System.out.println(result.getString(1)+":"+result.getString(2)+":");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private static void calcField(ArrayList<BigInteger> fieldToCalculate) {
	
		Timer timer = new Timer();
		
		timer.start();
		
		highestSaveNumber = sql.highestSaveNumber();
		highestSaveNumber = highestSaveNumber.multiply(highestSaveNumber);
		BigInteger thousand = new BigInteger("5000");
		for(BigInteger number:fieldToCalculate) {
			Timer local = new Timer();
			
			if(number.mod(thousand).equals(BigInteger.ZERO)) {
				factorise(number,true,false);
				calcNumbers();
				Date time = new Date();
				
				System.out.println("got Numbers:"+ time);
			}else {
				factorise(number,true,false);
			}
			highestSaveNumber = highestSaveNumber.add(BigInteger.ONE);
			
		}
		sql.addToHighestSaveNumber(new BigInteger(""+(fieldToCalculate.size())));
		timer.end();
		timer.printTime("factor field");
		timer.start();
		//showAllNumbersAndPrimes();
		timer.end();
		//timer.printTime("show Primes");
		
	
		//System.out.println(sql.initialiseAndShowTable());
	}
	
	private static void calcNumbers() {
		ArrayList<BigInteger> numbers = new ArrayList<>();
		numbers = sql.getNumbersToCalculate();
		
		while(numbers.size()>0) {
			Collections.sort(numbers);
			for(int i=0;i<numbers.size();i++) {
				
				System.out.println(factorise(numbers.get(i),true,false)+":"+numbers.get(i));
			
			}
			System.out.println(numbers);
			numbers = sql.getNumbersToCalculate();
		}
	}
	
	private static void showAllNumbersAndPrimes() {
		ResultSet table = sql.initialiseAndShowTable();
		
		try {
			while(table.next()) {
				for(int i=1;i<4;i++) {
					try {
						System.out.print(table.getString(i)+":");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				System.out.println();
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static ArrayList<BigInteger> factorise(BigInteger number,Boolean show,Boolean assumeUnknownState) {
		ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
		Boolean addedFactor = false;
		//System.out.println("starting to get state");
		timer.start();
		State state;
		if(!assumeUnknownState) {
			state = sql.getState(number.toString());
		}else {
			state = State.U;
		}
		timer.end();
		//timer.printTime("getting state");
		//System.out.println(state);
		if(state!=State.P) {
			if(state!=State.FF) {
				//System.out.println(state+"shouldnt be P||FF "+number);
				if(primes.size()!=0) {
					boolean compareSize = primes.get(0).multiply(primes.get(0)).compareTo(number)!=1;
					//System.out.println(compareSize+":"+primes.get(0)+":"+primes.get(0).multiply(primes.get(0)).compareTo(number)+":"+number);
					for(int i=0;i<primes.size()&&(compareSize);i++) {
						compareSize = primes.get(i).multiply(primes.get(i)).compareTo(number)!=1;
						if(number.mod(primes.get(i)).equals(BigInteger.ZERO)) {
							factors.add(primes.get(i));
							factors.addAll(factorise(number.divide(primes.get(i)),show,false));
							addedFactor = true;
							//System.out.println(factors+"factors");
							break;
						}
					}
				}
				if(addedFactor) {
					if(state==State.U) {
						
						for(BigInteger factor:factors) {
							if(!primes.contains(factor)) {
								State localstate = sql.getState(factor.toString());
								if(localstate!=State.FF&&localstate!=State.P) {
									state = State.CF;
									System.out.println(number+":"+localstate+":"+factor);
								}
							}
						}
					
						
						if(state!=State.CF) {
							state = State.FF;
						}else {
						}
						//System.out.println(factors+":"+number);
						sql.addNumber(number, state, factors);
					}else {
						state = State.U;
						for(BigInteger factor:factors) {
							State localState = sql.getState(factor.toString());
							
							if(localState!=State.P&&localState!=State.FF) {
								state = State.CF;
								System.out.println(localState+"shouldnt be P||FF");
							}
						}
						if(state!=State.CF) {
							state = State.FF;
						}
						
						sql.updateNumber(number, state, factors);
					}
				}else {
					factors.add(number);
					//System.out.println(highestSaveNumber.multiply(highestSaveNumber));
					if(number.compareTo(highestSaveNumber.multiply(highestSaveNumber))!=1) {
						if(state==State.U) {
							state = State.P;
							if(!primes.contains(number)) {
								sql.addNumber(number, state, factors);
							}
						}else {
							state = State.P;
							sql.updateNumber(number, state, factors);
						}
						if(!show)
						highestSaveNumber = number;
						//System.out.println("new HighstSaveNumber: "+highestSaveNumber);
						primes.add(number);
						Collections.sort(primes);
					}else {
						if(state==State.U) {
							state = State.PP;
							sql.addNumber(number, state, factors);
						}else {
							state = State.PP;
							sql.updateNumber(number, state, factors);
						}
					}
				}
			}else {
				factors = sql.getFactors(number.toString());
				Collections.sort(factors);
			}
		}else {
			factors.add(number);
		}
		if(show)
		System.out.println(factors+":"+number+":"+state);
		return factors;
	}

}
