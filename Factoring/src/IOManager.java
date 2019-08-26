

import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;


import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
public class IOManager {
	private Connection mySQLConnection;
	private String sqlCommand;
	private String strSelect;
	private Statement statement;
	private Statement dbStatement;
	private int addedNumbers =0;
	private int zeros, ones, twos;
	private static Connection dbConnection; 
	private static final String DB_PATH = "F://testdb.db"; 
	private HashMap<String,State> states= new HashMap<>();
	private HashMap<String,int[]> factors= new HashMap<>();
	private boolean dbMode = false;
	//private ArrayList<Number> numbers = new ArrayList<Number>();
	ResultSet result;
	
	public IOManager() {
	 
	}
	 
	public void setDBMode(boolean mode) {
		dbMode = mode;
	}
	
	public boolean connectToMysql(String host, String database, String user, String password){
		try{
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionCommand = "jdbc:mysql://"+host+"/"+database+"?allowMultiQueries=true";
			mySQLConnection = DriverManager.getConnection(connectionCommand, user, password);
			statement = mySQLConnection.createStatement();
			//strSelect = "TRUNCATE TABLE FactorDB";
			//statement.execute(strSelect);
		//	System.out.println(statement.execute(strSelect));
			
			numbersToAdd = new ArrayList<>();
			return true;
		 
		}catch (Exception ex){
			System.out.println("false");
			ex.printStackTrace();
			return false;
		}
	}
	
	 public void initDBConnection() { 
	        try { 
	            if (dbConnection != null) 
	                return; 
	            System.out.println("Creating Connection to Database..."); 
	            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH); 
	            if (!dbConnection.isClosed()) {
	                System.out.println("...Connection established"); 
	                dbStatement = dbConnection.createStatement();
	              /* for(int i=0;i<1000;i++) {
		                strSelect = "DELETE FROM LocalFactorDB"+i;
		    			dbStatement.execute(strSelect);
	                }
	                strSelect = "DELETE FROM LocalFactorDBPrimes";
	    			dbStatement.execute(strSelect);*/
	                
	            }
	        } catch (SQLException e) { 
	            throw new RuntimeException(e); 
	        } 

	        Runtime.getRuntime().addShutdownHook(new Thread() { 
	            public void run() { 
	                try { 
	                    if (!dbConnection.isClosed() && dbConnection != null) { 
	                    	dbConnection.close(); 
	                        if (dbConnection.isClosed()) 
	                            System.out.println("Connection to Database closed"); 
	                    } 
	                } catch (SQLException e) { 
	                    e.printStackTrace(); 
	                } 
	            } 
	        }); 
	    } 
	
	public void closeConnection() {
		try {
			Timer timer = new Timer();
			if(numbersToAdd.size()>0) {
				strSelect = "";
				timer.start();
				String[] selectsmysql = new String[1000];
				String[] selectsdb = new String[1000];
				for(int i=0;i<1000;i++) {
					selectsmysql[i] = "INSERT INTO `FactorDB"+i+"`(`Number`, `Status`, `Factors`) VALUES ";
					selectsdb[i] = "INSERT INTO `localFactorDB"+i+"`(`Number`, `State`, `Factors`) VALUES ";
				}
				for(Number number:numbersToAdd) {
					BigInteger tableNumber = getTableNumber(number.getNumber());
					if(number.getStatus()==State.P) {
						strSelect+="INSERT INTO `FactorDBPrimes`(`Number`, `Status`, `Factors`) VALUES ('"+number.getNumber()+"','"+number.getStatus()+"','"+number.getFactors()+"');";
					}
					strSelect+="INSERT INTO `FactorDB"+getTableNumber(number.getNumber())+"`(`Number`, `Status`, `Factors`) VALUES ('"+number.getNumber()+"','"+number.getStatus()+"','"+number.getFactors()+"');";
				}
				timer.end();
				timer.printTime("construction Insert String");
				strSelect = strSelect.substring(0,strSelect.length()-1);
				//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
				System.out.println("closedConnection");
				timer.start();
				statement.executeUpdate(strSelect);
				timer.end();
				timer.printTime("sending Insert server");
				strSelect = strSelect.replace("FactorDB", "localFactorDB").replace("Status", "State");
				timer.start();
				dbStatement.executeUpdate(strSelect);
				timer.end();
				timer.printTime("sending Insert local");
			}
			timer.start();
			mySQLConnection.close();
			timer.end();
			timer.printTime("close connection");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet initialiseAndShowTable(){
		//connectToMysql("sql7.freemysqlhosting.net", "sql7283410", "sql7283410sql7283410", "GJDT7NWj7A");
	
		
			
			result = getResultSet();
		
		
		return result;
	}
	
	
	
	public ResultSet getResultSet(){
		try{
			
			strSelect = "select * from localFactorDBPrimes";
			//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			result = dbStatement.executeQuery(strSelect);
		}catch(SQLException e){
			System.out.println(e+"reading table");
			
		}
		return result;
	}
	public void excecute(String string) {
	try{
			
			strSelect = string;
			System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			System.out.println(statement.executeUpdate(strSelect));
			
		}catch(SQLException e){
			System.out.println(e+"excecuting");
			
		}
		//return result;
	}
	public ResultSet excecuteQuerry(String string) {
	try{
			
			strSelect = string;
			//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			result = statement.executeQuery(strSelect);
		}catch(SQLException e){
			System.out.println(e+"excecuting");
			
		}
		return result;
	}
	


	public ArrayList<BigInteger> getPrimes() {
		
		ArrayList<BigInteger> primes = new ArrayList<BigInteger>();
		try{
			
			strSelect = "SELECT * FROM `FactorDB`";
			//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			//result = statement.executeQuery(strSelect);
			strSelect = "SELECT * FROM `localFactorDBPrimes`";
			System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			ResultSet dbResult = dbStatement.executeQuery(strSelect);
			result = dbResult;
			//Number number;
			//numbers = new ArrayList<Number>(result.getFetchSize());
			System.out.println(result.toString());
			
			//PreparedStatement preparedDBStatement = dbConnection.prepareStatement("INSERT INTO localFactorDB VALUES (?,?,?);");
			while(result.next()) {
				//System.out.println(result.getString(1));
				String localnumber = result.getString(1);
				primes.add(new BigInteger(localnumber));
				System.out.println(localnumber);
				/*String[] factorArray = factors.split(",");
				int[] factorIntArray = new int[factorArray.length];
				Boolean addFactors = true;
				for(int i=0;i<factorArray.length;i++) {
					BigInteger integer = new BigInteger(factorArray[i].trim());
					if(integer.compareTo(BigInteger.valueOf(Integer.MAX_VALUE))==-1)
					factorIntArray[i] = Integer.parseInt(factorArray[i].trim());
				}
				for(State state:State.values()) {
					if(result.getString(2).equals(state.toString())) {
						numberState = state;
					}
				}
				if(addFactors) {
					this.factors.put(localnumber, factorIntArray);
				}
				states.put(localnumber,numberState);
				
				/*preparedDBStatement.setString(1, localnumber.toString());
				preparedDBStatement.setString(2, numberState.toString());
				preparedDBStatement.setString(3, factors);
				preparedDBStatement.addBatch();*/
				//number = new Number(localnumber, numberState, result.getString(3));
				//numbers.add(number);
				/*
				if(numberState==State.P) {
					primes.add(new BigInteger(localnumber));
				}*/
				//System.out.println(localnumber+":"+numberState);
				//numbers.put(result.getString(1), number);
				/*if(localnumber.mod(new BigInteger("10000")).equals(BigInteger.ZERO)) {
					System.out.println(localnumber+":"+numberState);
					/*dbConnection.setAutoCommit(false); 
		            preparedDBStatement.executeBatch(); 
		            dbConnection.setAutoCommit(true); 
				}*/
			}
		
			//System.out.println(numbers);
			//System.out.println(primes);
			//Collections.sort(numbers);
			Collections.sort(primes);
		}catch(SQLException e){
			System.out.println(e+"getting Primes");
			try {
				Thread.sleep(100000);
			} catch (InterruptedException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
		}
		return primes;
	}
	
	public BigInteger highestSaveNumber() {
		BigInteger highestSaveNumber = new BigInteger("3");
		strSelect = "SELECT * FROM `HighestSafeNumber`";
		System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
		try {
			result = statement.executeQuery(strSelect);
			
			if(result.next()) {
				highestSaveNumber = new BigInteger(result.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(highestSaveNumber+"highestSaveNumber");
		return highestSaveNumber;
	}
	
	public ArrayList<BigInteger> getNumbersToCalculate(){
		ArrayList<BigInteger> numbers = new ArrayList<BigInteger>();
		strSelect = "SELECT * FROM `NumbersToCalculate` WHERE NUMBER != ''";
		//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
		try {
			result = statement.executeQuery(strSelect);
			
			while(result.next()) {
				numbers.add(new BigInteger(result.getString(1)));
				System.out.println(result.getString(1));
			}
			strSelect = "DELETE FROM `NumbersToCalculate` WHERE 1";
			//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			statement.executeUpdate(strSelect);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numbers;
	}
	
	public void addToHighestSaveNumber(BigInteger number) {
		BigInteger highestSaveNumber = highestSaveNumber();
		System.out.println("highestNumber: "+highestSaveNumber+"and add: "+number);
		highestSaveNumber = highestSaveNumber.add(number);
		//highestSaveNumber = new BigInteger("2");
		try{
			
			strSelect = "UPDATE `HighestSafeNumber` SET `HighestNumber`='"+highestSaveNumber+"' WHERE 1";
			//strSelect = "INSERT INTO `HighestSafeNumber`(`HighestNumber`) VALUES ('2')";
			System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			statement.executeUpdate(strSelect);
		}catch(SQLException e){
			System.out.println(e+"update table");
			
		}
	}
	
	public ArrayList<BigInteger> getFactors(String number) {
		ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
		
		if(this.factors.containsKey(number)&&!dbMode) {
			int[] factorIntArray = this.factors.get(number);
			for(int i=0;i<factorIntArray.length;i++) {
				factors.add(BigInteger.valueOf(factorIntArray[i]));
			}
		}else {
		
			
			strSelect = "SELECT * FROM `localFactorDB"+getTableNumber(new BigInteger(number))+"` WHERE `Number`='"+number+"' LIMIT 1";
			System.out.println("Factors:The SQL query is: " + strSelect);  // Echo For debugging
			try {
				result = dbStatement.executeQuery(strSelect);
				if(result.next()) {
					Number localNumber = new Number(new BigInteger(number),State.U,result.getString(3)); 
					factors = localNumber.getFactorList();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*if(numbers.containsKey(number)) {
			factors = numbers.get(number).getFactorList();
			}*/
		}
		return factors;
	}
	
	public State getState(String number) {
		State state = State.U;
	
		if(states.containsKey(number)&&!dbMode) {
		state = states.get(number);
		}else {
			strSelect = "SELECT * FROM `localFactorDB"+getTableNumber(new BigInteger(number))+"` WHERE `Number`='"+number+"' LIMIT 1";
			System.out.println("States:The SQL query is: " + strSelect);  // Echo For debugging
			try {
				result = dbStatement.executeQuery(strSelect);
				if(result.next()) {
					String localState = result.getString(2);
					for(State states:State.values()) {
						if(localState.equals(states.toString())) {
							state=states;
						}
					}
					//System.out.println(localState+":"+state);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//getTableNumber(new BigInteger(number));
		return state;
	}
	//dead delete later
	/*public ArrayList<State> getStates(ArrayList<BigInteger> numbers){
		ArrayList<State> states = new ArrayList<>();
		HashSet<BigInteger> numberSet = new HashSet<>(numbers);
		//System.out.println(numberSet);
		for(BigInteger number:numberSet) {
			states.add(getState(number));
		}
		
		
		return states;
	}*/
	
	private BigInteger getTableNumber(BigInteger number) {
		BigInteger splitTablesAt = BigInteger.valueOf(1000);
		BigInteger tableNumber;
		tableNumber = number.mod(splitTablesAt);
		//System.out.println(number+" should be in table: "+tableNumber);
		return tableNumber;
	}
	
	public void updateNumber(BigInteger number,State state,ArrayList<BigInteger> factors) {
		try{
			
			strSelect = "UPDATE `FactorDB"+getTableNumber(number)+"` SET `Status`='"+state+"',`Factors`='"+factors.toString().replace("[", "").replace("]", "")+"' WHERE Number="+number;
			System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			statement.executeUpdate(strSelect);
			strSelect = "UPDATE `localFactorDB"+getTableNumber(number)+"` SET `State`='"+state+"',`Factors`='"+factors.toString().replace("[", "").replace("]", "")+"' WHERE Number="+number;
			System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			dbStatement.executeUpdate(strSelect);
			if(state == State.P) {
				strSelect = "INSERT INTO `FactorDBPrimes`(`Number`, `Status`, `Factors`) VALUES ('"+number+"','"+state+"','"+factors.toString().replace("[", "").replace("]", "")+"');";
				System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
				statement.executeUpdate(strSelect);
				strSelect = strSelect.replace("FactorDB", "localFactorDB").replace("Status", "State");
				dbStatement.executeUpdate(strSelect);
			}
			/*numbers.remove(number);
			
			numbers.put(number.toString(), new Number(number,state,factors.toString().replace("[", "").replace("]", "")));*/
			System.out.println("updated single Number");
		}catch(SQLException e){
			System.out.println(e+"update table");
			
		}
	}
	ArrayList<Number> numbersToAdd;
	public void addNumber(BigInteger number,State state,ArrayList<BigInteger> factors) {
		try{
			Number newNumber = new Number(number, state, factors.toString().replace("[", "").replace("]", ""));
			numbersToAdd.add(newNumber);
			//numbers.put(number.toString(),newNumber);
			
			//strSelect = "INSERT INTO `FactorDB`(`Number`, `Status`, `Factors`) VALUES ('"+number+"','"+state+"','"+factors.toString().replace("[", "").replace("]", "")+"')";
			//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			//statement.executeUpdate(strSelect);
		}catch(Exception e){
			System.out.println(e+"update table");
			
		}
	}


	public ArrayList<BigInteger> getNextNumberField() {
		ArrayList<BigInteger> numbers = new ArrayList<BigInteger>();
		strSelect = "SELECT * FROM `FieldToCalculate`";
		//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
		try {
			result = statement.executeQuery(strSelect);
			String field="";
			if(result.next()) {
				field = result.getString(1);
				String[] localField = field.split("-");
				BigInteger start = new BigInteger(localField[0]);
				BigInteger end = new BigInteger(localField[1]);
				while(!start.equals(end)) {
					numbers.add(start);
					start = start.add(BigInteger.ONE);
				}
				numbers.add(start);
			}
			strSelect = "DELETE FROM `FieldToCalculate` WHERE `Numbers` = '"+field+"'";
			System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			statement.executeUpdate(strSelect);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numbers;
	}
	
	public void addFields(int amount,int size) {
		BigInteger highestSafeNumber = highestSaveNumber();
		strSelect = "INSERT INTO `FieldToCalculate`(`Numbers`) VALUES ";
		for(int i=0;i<amount;i++) {
			strSelect+="('"+highestSafeNumber.add(BigInteger.ONE)+"-"+highestSafeNumber.add(new BigInteger(""+size))+"'),";
			
			highestSafeNumber=highestSafeNumber.add(new BigInteger(""+size));
		}
		strSelect = strSelect.substring(0,strSelect.length()-1);
		try {
			
			System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			statement.executeUpdate(strSelect);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}


