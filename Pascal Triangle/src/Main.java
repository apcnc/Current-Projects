import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiFunction;

import com.sun.tools.javac.code.Attribute.Array;

import de.tilman_neumann.jml.factor.CombinedFactorAlgorithm;
import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.pollardRho.PollardRho_ProductGcd;



public class Main {


	static BigInteger start = new BigInteger("270000000000000000000042424200000000000000000000000000000000000000000000002312412400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001");
											//1cbwuqbusupo98ryy50io4buz1a1f83rk9kyd5hn9x909qcsghpfg80rl5rlh8iuwdyttpiq8aus9euglwjk
											//1lxHwSuBWRmdofCHslVMceNfYOf5i7RcdswYXY1YjB3Jiq1sNHJK0zbOTK5lq0DRbPS3kgcqW
	static int amount = 10;
	static IOManager collatz = new IOManager("F://DB/Collatz");
	static AKS aks = new AKS(new BigInteger("2"));

	static int maxLength = 200;
	static BigInteger maxSteps = BigInteger.valueOf(25);
	
	static LocalFactorDB factorDB = new LocalFactorDB();
	public static void main(String[] args) {
		//connectSQL();
		//factorDB.disableLogging();
		//homeCollatz();
		//System.out.println(factorDB.factorise(start));
		//calcNumbers(start, start.add(BigInteger.valueOf(100)), true);
		//pushFactorial();
		//System.out.println(getMinNumberCollatz(start, maxSteps));
		
		//System.out.println(start.toString().length());
		//System.out.println(collatz(start));
		calcCollatz(start, start.add(BigInteger.valueOf(amount)));
		/*
		BigInteger minNumber = getMinNumberCollatz(start, maxSteps);
		int steps = 0;
		while(steps<5000) {
			minNumber = getMinNumberCollatz(minNumber, maxSteps);
			System.out.println(minNumber+" min number");
			steps = collatz(minNumber).get(0).intValue();
		}
		System.out.println(maxSteps+":"+minNumber+":"+collatz(minNumber));*/
		//trialDivUnkown();
	}

	public static void trialDivUnkown() {
		try {
			for(int i=10;i<20;i++) {
				String out = new Scanner(new URL("http://factordb.com/listtype.php?t=2&mindig=101000&perpage=1&start="+i+"&download=1").openStream(), "UTF-8").useDelimiter("\\A").next().replace("]", "").replace("[", "").replace("\"", "").replace("}", "");
				out = out.strip();
				BigInteger test = new BigInteger(out);
				trialDiv(test);
				System.out.println("trialDiv end:"+i);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static BigInteger getMinNumberCollatz(BigInteger start, BigInteger maxSteps) {
		System.out.println(start+":"+maxSteps);
		collatzQ = new LinkedList<BigInteger>();
		collatzQ.add(start);
		BigInteger steps = BigInteger.ONE;
		steps = reverseCollatzBuildup(collatzQ.poll());
		maxSteps = steps.add(maxSteps);
		BigInteger minNumber = start.pow(20);
		while(!collatzQ.isEmpty()&&steps.compareTo(maxSteps)<0) {
			BigInteger number = collatzQ.poll();
			steps = reverseCollatzBuildup(number);
			if(steps.add(BigInteger.ONE).compareTo(maxSteps)==0) {
				System.out.println("reached max");
				if(number.compareTo(minNumber)<0) {
					minNumber = number;
					System.out.println("should show once"+minNumber);
				}
			}
		}
		System.out.println(collatzQ);
		if(minNumber.equals(start.pow(20))) {
		//	throw new IllegalArgumentException("maxLenght reached");
		}
		return minNumber;
	}
	
	private static void trialDiv(BigInteger n) {
		ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
		for(BigInteger i =BigInteger.TWO;i.compareTo(BigInteger.valueOf(100000))<0;i=i.nextProbablePrime()) {
			
			if(n.mod(i).equals(BigInteger.ZERO)) {
				//System.out.println(i);
				factors.add(i);
				
			}
			
			//SiteOperations.pushFactor(n, factors, true);
		}
		if(factors.size()>0) {
			System.out.println(factors);
		}
		
	}
	
	static Set<BigInteger> seen = new HashSet<BigInteger>();
	static Queue<BigInteger> collatzQ;
	private static BigInteger reverseCollatzBuildup(BigInteger number) {
		BigInteger steps = BigInteger.ZERO;
		//seen.add(number);
		if(!number.equals(BigInteger.ONE)) {
			System.out.println(number);
			steps = collatz(number).get(0);
			if(number.toString().length()<maxLength) {
				BigInteger newNumber = number.multiply(BigInteger.TWO);
				if(!seen.contains(newNumber)) {
					collatzQ.add(number.multiply(BigInteger.TWO));
				}
				if(number.subtract(BigInteger.ONE).mod(BigInteger.valueOf(3)).equals(BigInteger.ZERO)&&number.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
					newNumber = number.subtract(BigInteger.ONE).divide(BigInteger.valueOf(3));
					//System.out.println(newNumber+" should be new");
					if(!seen.contains(newNumber)) {
						collatzQ.add(newNumber);
					}
				}
			}
		}else {
			System.out.println("something went wrong");
		}
		return steps;
	}
	
	private static void homeCollatz() {
		BigInteger test = new BigInteger("31");
		
		//sql.initDBConnection();
		//collatz.initDBConnection();
		Timer timer = new Timer();
		timer.start();
		//System.out.println(Base62.encode(start));
		//calcNumbers(start, start.add(BigInteger.valueOf(amount)),true);
		//fillTable(new BigInteger("1"));
		//pushFactorial();
		//calcCollatz(start.pow(5000), start.pow(5000).add(BigInteger.ONE));
		BigInteger newInt = start;
		//homePrimeBase10(start, true, 80);
		BigInteger max = start;

		//SiteOperations.getFactors(start.toString());
		int maxSteps = 0;
		for(int i=0;i<amount;i++) {
			start = start.add(BigInteger.ONE);
			//newInt = start.pow(i).subtract(BigInteger.ONE);
			//start = start.pow(102).subtract(BigInteger.ONE);
			//newInt = newInt.nextProbablePrime();
			//calcCollatz(newInt, newInt.add(BigInteger.ONE));
			int steps = homePrimeBase10(start, true, 70);
			if(steps>maxSteps) {
				maxSteps = steps;
				max = start;
			}
			//newInt = newInt.add(BigInteger.TWO);
			//start = start.pow(102).subtract(BigInteger.ONE);
			//calcCollatz(newInt, newInt.add(BigInteger.valueOf(amount)));
		}
		System.out.println(maxSteps+":"+max);
		//calcCollatz(start, start.add(BigInteger.valueOf(amount)));
		timer.end();
		timer.printTime("pushing 10k");
		//System.out.println(usedPascal+":"+usedFermat);
		
		//System.out.println(sql.getRow(test));
		//System.out.println(factorize(test));
		//System.out.println(test+":"+factorize(test));
	}
	
	private static void calcCollatz(BigInteger start, BigInteger end) {
		BigInteger max = BigInteger.ZERO;
		BigInteger maxnum = null;
		for(;start.compareTo(end)<0;start=start.add(BigInteger.ONE)) {
			ArrayList<BigInteger> col = collatz(start);
			System.out.println(start+":"+col);
			if(max.compareTo(col.get(0))<0) {
				max=col.get(0);
				maxnum = start;
			}
		}
		System.out.println("max:"+max+" at:"+maxnum);
	}
	
	private static ArrayList<BigInteger> collatzsql(BigInteger n) {
		ArrayList<BigInteger> col = collatz.getRow(n);
		if(col.size()>0) {
			//System.out.println("works");
			return col;
		}
		if(n.equals(BigInteger.ONE)) {
			col.add(BigInteger.ONE);
			return col;
		}
		if(n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
			col = collatz(n.divide(BigInteger.TWO));
			
		}else {
			col = collatz(n.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE));
		}
		col.set(0, col.get(0).add(BigInteger.ONE));
		
		System.out.println("new calc");
		collatz.addRow(n, col);
		return col;
	}
	
	private static ArrayList<BigInteger> collatz(BigInteger n) {
		ArrayList<BigInteger> col = new ArrayList<BigInteger>();
		if(n.equals(BigInteger.ONE)) {
			col.add(BigInteger.ONE);
			return col;
		}
		if(n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
			col = collatz(n.divide(BigInteger.TWO));
			
		}else {
			col = collatz(n.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE));
		}
		col.set(0, col.get(0).add(BigInteger.ONE));
		
		//System.out.println("new calc");
		//collatz.addRow(n, col);
		return col;
	}
	
	private static int homePrimeBase10(BigInteger number, boolean push,int maxDigits) {
	
		BigInteger numberold = number;
		int steps = 0;
		while(!factorPrimeTest(number)&&number.toString().length()<maxDigits) {
			ArrayList<BigInteger> factors = factorDB.factorise(number);
				//System.out.println(i+":"+factorize(i));
			//System.out.println(number+":"+factors);
				
				String f = "";
				for(BigInteger factor:factors) {
					f+=factor;
				}
				number = new BigInteger(f);
				steps++;
		}
		if(factorPrimeTest(number)) {
			System.out.println(numberold+" finishes after "+steps+" at:"+number);
		}else {
			System.out.println(numberold+" doesn't finish after "+steps+" at:"+number);
		}
		return steps;
	}
	
	
	
	private static void calcNumbers(BigInteger start,BigInteger end,boolean push) {
		ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
		for(BigInteger i = start;i.compareTo(end)<0;i=i.add(BigInteger.ONE)) {
			if(i.mod(BigInteger.valueOf(100)).equals(BigInteger.ZERO)) {
			System.out.println(i+":"+factorDB.factorise(i));
			}else {
				System.out.println(i+":"+factorDB.factorise(i));
				//factors = factorDB.factorise(i);
				
			}
		}
	}
	
	private static void fillTable(BigInteger table) {
		amount = 10000;
		start = table.multiply(BigInteger.valueOf(amount));
		if(table.equals(BigInteger.ZERO)) {
			start = BigInteger.TWO;
		}
		calcNumbers(start, start.add(BigInteger.valueOf(amount)),false);
	}
	
	private static void pushFactorial() {
		BigInteger n = BigInteger.ONE;
		for(int i=0;i<100;i++) {
		n=n.multiply(BigInteger.valueOf(i).add(BigInteger.ONE));
		if(n.toString().length()>5) {
			System.out.println(i+":"+n+":"+factorDB.factorise(n));
			}
		}
	}
	

	

	
	private static boolean factorPrimeTest(BigInteger n) {
		return aks.checkIsPrime(n);
	}
	

	

	

	

	

		
	
	

	

	
	private static BigInteger biggestFactorEuklid(BigInteger a,BigInteger b) {
		if(b.equals(BigInteger.ZERO))return a;
		return biggestFactorEuklid(b, a.mod(b));
	}
	
}
