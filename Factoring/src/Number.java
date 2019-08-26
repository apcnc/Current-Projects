

import java.math.BigInteger;
import java.util.ArrayList;

public class Number implements Comparable{
	
	
	public Number(BigInteger number, State status, String factors) {
		super();
		this.number = number;
		this.status = status;
		this.factors = factors;
		/*
		String[] factorString = factors.split(",");
		this.factors = new BigInteger[factorString.length];
		for(int i=0;i<factorString.length;i++) {
			this.factors[i]=new BigInteger(factorString[i].trim());
		}*/
		
	}
	private final BigInteger number;
	private final State status;
	//private BigInteger[] factors;
	private final String factors;
	public State getStatus() {
		return status;
	}

	public String getFactors() {
		String factors = this.factors;
		/*
		String factors = "";
		for(int i=0;i<this.factors.length;i++) {
			factors+=this.factors[i]+",";
		}

		factors = factors.substring(0,factors.length()-1);*/
		return factors;
	}
	public ArrayList<BigInteger> getFactorList() {
		ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
		String[] factorString = this.factors.split(",");
		for(int i=0;i<factorString.length;i++) {
			factors.add(new BigInteger(factorString[i].trim()));
		}
		
		return factors;
		
	}
	
	public BigInteger getNumber() {
		return number;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Number other = (Number) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}
	@Override
	public int compareTo(Object number) {
		 BigInteger otherNumber=((Number)number).getNumber();
	        /* For Ascending order*/
	        return this.number.compareTo(otherNumber);
	}
	
	
}
