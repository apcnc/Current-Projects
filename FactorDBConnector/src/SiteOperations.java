import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.tilman_neumann.jml.factor.CombinedFactorAlgorithm;
import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.FactorizerTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver02_BlockLanczos;
import de.tilman_neumann.jml.factor.psiqs.PSIQS_U;
import de.tilman_neumann.jml.factor.siqs.powers.NoPowerFinder;

public class SiteOperations {
	static final String API = "http://factordb.com/index.php?query=%2810%5E77*28-1%29%2F9%2Bn&use=n&n=";
	//static final String API = "http://www.factordb.com/api?query=";
	static final String APIEND = "&VP=on&VC=on&EV=on&OD=on&PR=on&PRP=on&U=on&C=on&perpage=20&format=1&sent=Show";
	private static ArrayList<String> websiteLinks;
	static 	BigDecimal pi = BigDecimal.ONE;
	static String[] primes;
	static int threads = 0;
	static WebsiteOperations wo = new WebsiteOperations(null, null, "https://beatsaver.com/browse/newest");
	static FactorAlgorithm factorAlg = new CombinedFactorAlgorithm(5, true);;
    static int minDigits = 68;
    static  AKS primeTest = new AKS();
    static  AKSBigInt primeTestInt = new AKSBigInt();
    public static void main(String[] args) {
    	ArrayList<BigInteger> tests = new ArrayList<BigInteger>();
    	Timer timer = new Timer();
    	for(int i=0;i<100;i++) {
    		tests.add(new BigInteger(ranNthLenght(5)));
    	}
    	timer.start();
    	for(int i=0;i<100;i++) {
    		if(primeTest.checkIsPrime(tests.get(i))) {
        		//System.out.println("prime");
        	}else {
        	getFactorShor(tests.get(i),true);
        	}
    	}
    	timer.end();
    	timer.printTime("divide");
    	timer.start();
    	for(int i=0;i<100;i++) {
    		if(primeTest.checkIsPrime(tests.get(i))) {
        	//	System.out.println("prime");
        	}else {
        	getFactorShor(tests.get(i),false);
        	}
    	}
    	
    	timer.end();
    	timer.printTime("root");
    	
    	
    	
    	
    	//factorDb();
    }
    
    public static BigInteger getFactorShor(BigInteger number,boolean init) {
    	BigInteger factor = BigInteger.ONE;
    	BigInteger initialGuess;
    	if(init) {
    	initialGuess = number.divide(BigInteger.TWO);
    	} else {
    		initialGuess = number.sqrt();
    	}
    	if(!number.gcd(initialGuess).equals(BigInteger.ONE))return number.gcd(initialGuess);
    	BigInteger p = BigInteger.ONE;
    	//System.out.println(number+":"+initialGuess+":"+initialGuess.pow(p.intValue()).subtract(BigInteger.ONE));
    	BigInteger temp = new BigInteger(initialGuess.toString());
    	while(!temp.mod(number).equals(BigInteger.ONE)) {
    		p = p.add(BigInteger.ONE);
    		temp = temp.multiply(initialGuess);
    		//System.out.println(p);
    	}
    	factor = initialGuess.pow(p.intValue()/2).subtract(BigInteger.ONE).gcd(number);
    	
    	return factor;
    }
    
    public static String ranNthLenght(int maxLenght) {
    	String test = "";
    	while(test.length()<maxLenght) {
    		test+=Math.abs(ThreadLocalRandom.current().nextInt());
    	}
    	test = test.substring(0, maxLenght);
    	BigInteger num = new BigInteger(test);
    	return test;
    }
	
	
	
	
	public static void factorDb() {
		int amount = 0;
		
		
		while(true) {
			
			Document site = wo.getSite("http://factordb.com/listtype.php?t=3&mindig="+minDigits+"&perpage=100&start=0&download=1");
			String[] numbers = site.body().text().toString().split(" ");
			BigInteger number = new BigInteger(numbers[ThreadLocalRandom.current().nextInt(100)]);
			//System.out.println(number);
			/*System.out.println("start prime");
			primeTestInt.checkIsPrime(new BigInt(number.toString()));
			System.out.println("end prime");*/
			ArrayList<BigInteger> factors = new ArrayList<BigInteger>(factorAlg.factor(number).toList());
			//System.out.println(factorAlg.factor(number));
			pushFactor(number, factors,true);
			amount++;
			Date date = new Date();

			System.out.println(DateFormat.getTimeInstance().format(date).toString()+" A total of "+amount+" numbers have been factored");
		}
		
	}
	
	public static void pushFactor(CostumNumber number) {
		pushFactor(number.getNumber(),number.getFactors(),true);
	}
	
	public static void pushFactor(BigInteger number, ArrayList<BigInteger> factors, boolean logging){
		HashSet<BigInteger> reducedFactors = new HashSet<BigInteger>();
		reducedFactors.addAll(factors);
		String url = "http://factordb.com/report.php?report="+number+"%3D7%3D2";
		for(BigInteger f:reducedFactors) {
			url+=","+f;
		}
		Document site = wo.getSite(url);
		String[] lines = site.toString().split("\n");
		if(logging) {
			for(String line:lines) {
				if(line.contains("Found")) {
					System.out.println(number+":"+factors);
					System.out.println(line.replace("<td align=\"center\" colspan=\"3\" bgcolor=\"#DDDDDD\">", "").replace("</td> ", "").replace("<td align=\"center\" colspan=\"3\">",""));
				}
			}
		}
	}
	
	
	
	
	public static String test(String n) {
		final String API = "http://www.factordb.com/api?query=";
		String out = "";
		try {
			out = new Scanner(new URL(API+n).openStream(), "UTF-8").useDelimiter("\\A").next();

			System.out.println(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	
	public static String getState(String file) {
		
		return file.split(",")[1].split(":")[1].replace("\"", "");
	}
	
	public static CostumNumber getNumber(BigInteger number) {
		if(number.toString().length()>8100) {
			throw new IllegalArgumentException("number can't have more than 8100 digits");
		}
		ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
		State state = State.Unkown;
		final String API = "http://www.factordb.com/api?query=";
		try {
			String out = new Scanner(new URL(API+number).openStream(), "UTF-8").useDelimiter("\\A").next().replace("]", "").replace("[", "").replace("\"", "").replace("}", "");
			//System.out.println(out);
			String[] factor = out.split(":")[3].split(",");
			for(int i=0;i<factor.length;i+=2) {
				//System.out.println(factor[i]+":"+factor[i+1]);
				for(int amount=0;amount<Integer.valueOf(factor[i+1]);amount++) {
					factors.add(new BigInteger(factor[i]));
				}
			}
			switch(out.split(":")[2].split(",")[0]) {
			case "C":
				state = State.Composite;
				break;
			case "PRP":
				state = State.ProbablePrime;
				break;
			case "P":
				state = State.Prime;
				break;
			case "FF":
				state = State.FullyFactored;
				break;
			case "CF":
				state = State.PartialyFactored;
				break;
			case "U":
				state = State.Unkown;
				break;
				default:
					System.out.println("Unrecognised State: "+out.split(":")[2].split(",")[0]);
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(factors);
		return new CostumNumber(number,state, factors);
	}
	
	public static ArrayList<BigInteger> getFactors(String file){
		return getNumber(new BigInteger(file)).getFactors();
	}
	
	
	/*public static String readStringFromURL(String requestURL) throws IOException
	{
	    try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
	            StandardCharsets.UTF_8.toString()))
	    {
	        scanner.useDelimiter("\\A");
	        return scanner.hasNext() ? scanner.next() : "";
	    }
	}*/
	
	
	
	
}
