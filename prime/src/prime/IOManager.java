package prime;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.Vector;
import java.sql.Date;
import java.util.ArrayList;
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
	
	private int addedNumbers =0;
	private int zeros, ones, twos;
	private Statement dbStatement;
	private static Connection dbConnection; 
	private static final String DB_PATH = "F://primedb.db"; 
	ResultSet result;
	private String sqlCommand;
	private String strSelect;
	public IOManager() {
	 
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
	             
	                /*sqlCommand = "CREATE TABLE numbers(Primes Text);";
	    			dbStatement.execute(sqlCommand);*/
	                
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
	
	private ResultSet initialiseAndShowTable(){
		initDBConnection();
	
		sqlCommand = "insert into numbers values";
		try{
			dbStatement = dbConnection.createStatement();
			result = getResultSet();
		}catch(SQLException e){
			System.out.println(e+"initialising table");
			
		}
		return result;
	}
	
	public void setValue(BigInteger value,boolean push){
		try{
			
			addedNumbers++;
			if(addedNumbers>1000||push){
				//System.out.println(value.multiply(new BigInteger("8872281231884183011160721772950318292886760917867460344743649806394755102795774584451561453695028741025264894481522470209280038556023129129810753582037332525538368720616245344052383606952699000123")));
				if(!push) {
					sqlCommand += "('"+value+"')";
					System.out.println(value);
				}else {
					sqlCommand += "('"+value+"')";
					System.out.println(value);
				}
				Timer timer = new Timer();
				timer.start();
				dbStatement.executeUpdate(sqlCommand);
				timer.end();
				timer.printTime("push Primes");
				//System.out.println(sqlCommand);
				sqlCommand = "insert into numbers values";
				
				addedNumbers = 0;
			}else{
				sqlCommand += "('"+value+"'),";
			}
			
		}catch(SQLException e){
			System.out.println(e+"seting table");
			System.out.println(sqlCommand);
		}
	}
	
	public ResultSet getResultSet(){
		try{
			
			strSelect = "select * from numbers";
			System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			result = dbStatement.executeQuery(strSelect);
		}catch(SQLException e){
			System.out.println(e+"reading table");
			
		}
		return result;
	}


	public ArrayList<BigInteger> loadValues(){
		ResultSet primes = initialiseAndShowTable();
		
		ArrayList<BigInteger> primeList = new ArrayList<BigInteger>();
		try {
			
			while(primes.next()) {
				primeList.add(new BigInteger(primes.getString(1)));
			}
			primes.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return primeList;
	}
	 
	public BigInteger getLastValue() {
		ResultSet primes = initialiseAndShowTable();
		BigInteger last = BigInteger.ONE;
		try {
			
			if(primes.last()) {
				last = new BigInteger(primes.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return last;
	
	}

}


