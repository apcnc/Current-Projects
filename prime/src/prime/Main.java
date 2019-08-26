package prime;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.BigIntegerMath; 
public class Main {
	
	static IOManager sql;
	static ArrayList<BigInteger> primes;
	static int currentPrimes = 0;
	static BufferedWriter out;
	static long numbersToBeTested;
	static Timer timer;
	static AKS primeTester;
	public static void main(String[] args) {
		int repeats = 100;
	
		primeTester = new AKS(BigInteger.ONE);
		for(int i=0;i<repeats;i++) {
				//calcPrimes();
		}
		timer = new Timer();
		timer.start();
		
		timer.end();
		timer.printTime("testing primes");
	}
	
	private static void testNextPrimes(BigInteger number,long range) {
		BigInteger max = number.add(BigInteger.valueOf(range));
		sql = new IOManager();
		sql.loadValues();
		while(number.compareTo(max)==-1) {
			boolean prime = testPrime(number);
			if(prime) {
				System.out.println(number);
				System.out.println(number.multiply(number.nextProbablePrime()));
				sql.setValue(number, true);
				sql.setValue(number.multiply(number.nextProbablePrime()), true);
				
			}
			number = number.add(BigInteger.ONE);
		}
	}
	
	private static void calcPrimes() {
	timer = new Timer();
		
	
		
		BigInteger testForPrime = BigInteger.valueOf(2);
		BigInteger startTestForPrime = testForPrime;
		numbersToBeTested = 50000;
		primes = new ArrayList<BigInteger>();
		sql = new IOManager();
		
		timer.start();
		primes = sql.loadValues();
		timer.end();
		timer.printTime("loading primes");
		timer.start();
		//System.out.println(sql.getLastValue());
		timer.end();
		timer.printTime("last prime");
		
		
		
		//System.out.println(primes);
		if(primes.size()>0){
			testForPrime = primes.get(primes.size()-1);
			startTestForPrime = testForPrime;
			currentPrimes = primes.size();
			System.out.println(primes.size()+"size");
			primes.clear();
		}
	
		
		BigInteger numberToBeTested = startTestForPrime.add(BigInteger.valueOf(numbersToBeTested));
		try{
			timer.start();
			while(testForPrime.compareTo(numberToBeTested)==-1){
				if(testPrime(testForPrime)){
					//System.out.println(testForPrime);
					//primes.add(testForPrime);
					currentPrimes++;
					
					Timer local = new Timer();
					local.start();
					sql.setValue(testForPrime,false);
					local.end();
					//local.printTime("set value");
					
				}
				testForPrime = testForPrime.add(BigInteger.ONE);
			}
			sql.setValue(BigInteger.ZERO, true);
			timer.end();
			timer.printTime("calculating primes");
		}catch(Exception e){
			System.out.println(e);
			
		}finally{
		}
		//System.out.println(primes);
	}
	
	static int primesCalculated=0;
	private static boolean testPrime(BigInteger testForPrime){
		boolean prime = false;
		Timer local = new Timer();
		local.start();
		if(testForPrime.isProbablePrime(100)) {
			Timer veryLocal = new Timer();
			veryLocal.start();
			//System.out.println("testing: "+testForPrime);
			//prime = primeTester.checkIsPrime(testForPrime);
			//prime = factorPrime(testForPrime);
			prime = true;
			if(prime==false) {
				System.out.println(testForPrime+" is false");
			}
			veryLocal.end();
			//veryLocal.printTime("aks");
			
		}
		local.end();
		if(local.getPassedTime()!=0) {
			//local.printTime("one prime");
			//System.out.println(testForPrime+":"+prime);
		}
		return prime;
	}
	
	private static boolean factorPrime(BigInteger prime) {
	 BigInteger root = BigIntegerMath.sqrt(prime, RoundingMode.CEILING);
	 BigInteger counter = BigInteger.valueOf(2);
	 
	 while(counter.compareTo(root)==-1) {
		 if(BigInteger.ZERO.compareTo(prime.mod(counter))==0) {
			 return false;
		 }
		 counter = counter.add(BigInteger.ONE);
	 }
	 return true;
	}

}
