import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

import de.tilman_neumann.jml.factor.CombinedFactorAlgorithm;
import de.tilman_neumann.jml.factor.FactorAlgorithm;

public class LocalFactorDB {
	private IOManager sql = new IOManager("F://DB/factoring");
	private FactorAlgorithm factorAlg = new CombinedFactorAlgorithm(6, true);;
	private AKS aks = new AKS(new BigInteger("2"));
	private int minDigitsforOnlineCheck = 60;
	private boolean logging = true;
	
	public static void main(String[] args) {
		LocalFactorDB factor = new LocalFactorDB();
		factor.disableLogging();
		System.out.println(factor.factorise(new BigInteger("32323233113232321331132343242342342321232331231132412312412331132321132331131")));
	}
	public void disableLogging() {
		sql.setLogging(false);
		logging = false;
	}
	
	
	
	public void setMinDigitsforOnlineCheck(int digits) {
		minDigitsforOnlineCheck = digits;
	}
	private boolean factorPrimeTest(BigInteger n) {
		return aks.checkIsPrime(n);
	}
	public ArrayList<BigInteger> factorise(BigInteger n){
		boolean push = false;
		if(n.equals(BigInteger.ONE)) {
			return new ArrayList<BigInteger>();
		}
		ArrayList<BigInteger> factors = sql.getRow(n);
		
		if(factors.size()>0) {
			if(logging)
			System.out.println("no calc: "+n);
			return factors;
		}
		
		if(n.toString().length()>=minDigitsforOnlineCheck) {
			ArrayList<BigInteger> onlineFactors = SiteOperations.getFactors(n.toString());
			if(onlineFactors.size()>1) {
				for(BigInteger factor:onlineFactors) {
					factors.addAll(factorise(factor));
				}
				if(logging)
				System.out.println("no calc, found online: "+n);
				sql.addRow(n, factors);
				if(onlineFactors.size()!=factors.size()) {
					SiteOperations.pushFactor(n, factors,logging);
					
				}
				return factors;
			}
			push = true;
		}
		
		if(factorPrimeTest(n)) {
			factors.add(n);
			sql.addRow(n, factors);
			return factors;
		}
		
		BigInteger factor = factorAlg.findSingleFactor(n);
		factors.addAll(factorise(factor));
		factors.addAll(factorise(n.divide(factor)));
		Collections.sort(factors);
		sql.addRow(n, factors);
		if(push) {
			SiteOperations.pushFactor(n, factors,logging);
		}
		return factors;
	}
	
}
