

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.io.IOException;
public class IOManager {
	
	private int addedNumbers =0;
	
	private Statement dbStatement;
	private Connection dbConnection; 
	private boolean logging = true;
	private final String DB_PATH; 
	ResultSet result;
	private String sqlCommand;
	private String strSelect;
	private String lastOpenDB;
	private BigInteger tableSize = BigInteger.valueOf(10000);
	
	public IOManager(String path) {
		 DB_PATH = path;
	}


	 private void initDBConnection() { 
	      initDBConnection(DB_PATH);
	    } 
	
	 private void initDBConnection(String addToPath) { 
		 String DB_PATH = this.DB_PATH+addToPath;
		 //System.out.println(DB_PATH);
		 try {
			Files.createDirectories(Paths.get(DB_PATH));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 DB_PATH+="/number.db";
	        try { 
	            /*if (dbConnection != null) 
	                return; */
	            //System.out.println("Creating Connection to Database..."); 
	            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH); 
	            if (!dbConnection.isClosed()) {
	                //System.out.println("...Connection established"); 
	                dbStatement = dbConnection.createStatement();
	             
	               /* sqlCommand = "CREATE TABLE triangle(Row Text, Content Text);";
	    			dbStatement.execute(sqlCommand);*/
	                lastOpenDB = addToPath;
	            }
	            
	        } catch (SQLException e) { 
	            throw new RuntimeException(e); 
	        } 
	        
	      
	    } 
	
	 public void setLogging(boolean loggingstate) {
		 logging = loggingstate;
	 }
	 
	 public void closeDBConnection() {
		 try {
			 if(dbConnection!=null)
			dbConnection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	 public String getPath(BigInteger number) {
		 String path = "";
		 int i=0;
		 BigInteger oldNumber = number;
		 while(number.compareTo(tableSize)>0) {
			 path +="/"+oldNumber.toString().charAt(i);
			 i++;
			 number = number.divide(BigInteger.TEN);
			 //System.out.println(number);
		 }
		 return path;
	 }
	 
	public void addRow(BigInteger row,ArrayList<BigInteger> numbers){
		String path = getPath(row);
		if(!path.equals(lastOpenDB)) {
			closeDBConnection();
			initDBConnection(path);
		}
		
		try{
			
			sqlCommand = "INSERT INTO `"+row.mod(BigInteger.TEN)+"`(`Row`, `Content`) VALUES ('"+row.mod(tableSize)+"','"+numbers.toString().replace("[", "").replace("]", "")+"');";
			
		
			//System.out.println(addedNumbers+":"+sqlCommand);
			dbStatement.executeUpdate(sqlCommand);
			
			
			
		}catch(SQLException e){
			if(logging)
			System.out.println(e+" adding row:"+row);
			if(e.toString().contains("no such table")||e.toString().contains("doesn't exist")) {
				if(logging)
				System.out.println("new table");
				sqlCommand = "CREATE TABLE `"+row.mod(BigInteger.TEN)+"`(`Row` TEXT NOT NULL, `Content` TEXT NOT NULL) ;";
				try {
					dbStatement.executeUpdate(sqlCommand);
					addRow(row, numbers);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			//System.out.println(sqlCommand);
		}finally {
			
		}
	}
	
	public ArrayList<BigInteger> getRow(BigInteger rownumber){
		ArrayList<BigInteger> row = new ArrayList<BigInteger>();
		String path = getPath(rownumber);
		if(!path.equals(lastOpenDB)) {
			closeDBConnection();
			initDBConnection(path);
		}
		try{
			strSelect = "select * from `"+rownumber.mod(BigInteger.TEN)+"` where Row=\""+rownumber.mod(tableSize)+"\" limit 1";
			//System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
			result = dbStatement.executeQuery(strSelect);
			//System.out.println(rownumber);
			if(result.next()) {
				String rowS = result.getString(2);
				//System.out.println(rowS);
				rowS = rowS.replace("[", "").replace("]", "");
				String[] numbers = rowS.split(",");
				for(String number:numbers) {
					row.add(new BigInteger(number.strip()));
				}
			}
		}catch(SQLException e){
			//e.printStackTrace();
			if(logging)
			System.out.println(e+" getting row");
			//System.out.println(strSelect);
		}
		return row;
	}
	
	


	
	 
	

}


