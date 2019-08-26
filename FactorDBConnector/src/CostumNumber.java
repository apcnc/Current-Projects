

import java.math.BigInteger;
import java.util.ArrayList;

public class CostumNumber implements Comparable{
	
	
	public CostumNumber(BigInteger number, State status, ArrayList<BigInteger> factors) {
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
	private State status;
	//private BigInteger[] factors;
	private ArrayList<BigInteger> factors;
	public State getStatus() {
		return status;
	}

	public ArrayList<BigInteger> factorise(){
		
		if(status!=State.FullyFactored) {
		factors = new ArrayList<BigInteger>(SiteOperations.factorAlg.factor(number).toList());
		status = State.FullyFactored;
		}
		SiteOperations.pushFactor(this);
		return factors;
	}
	
	public ArrayList<BigInteger> getFactors() {
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
		CostumNumber other = (CostumNumber) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}
	@Override
	public int compareTo(Object number) {
		 BigInteger otherNumber=((CostumNumber)number).getNumber();
	        /* For Ascending order*/
	        return this.number.compareTo(otherNumber);
	}

	@Override
	public String toString() {
		return "Number [number=" + number + ", status=" + status + ", factors=" + factors + "]";
	}
	
	
}
