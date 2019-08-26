

import java.math.*;
import java.io.*;
/***************************************************************************
 * Team 
 **************
 * Arijit Banerjee
 * Suchit Maindola
 * Srikanth Manikarnike
 *
 **************
 * This is am implementation of Agrawal–Kayal–Saxena primality test in java. 
 *
 **************
 * The algorithm is -
 * 1. l <- log n
 * 2. for i<-2 to l
 *      a. if an is a power fo l
 *              return COMPOSITE
 * 3. r <- 2
 * 4. while r < n
 *      a. if gcd( r, n) != 1
 *              return COMPSITE
 *      b. if sieve marked n as PRIME
 *              q <- largest factor (r-1)
 *              o < - r-1 / q
 *              k <- 4*sqrt(r) * l
 *              if q > k and n <= r 
 *                      return PRIME
 *      c. x = 2
 *      d. for a <- 1 to k 
 *              if (x + a) ^n !=  x^n + mod (x^r - 1, n) 
 *                      return COMPOSITE
 *      e. return PRIME
 */

public class AKSBigInt
{
    private int log;
    private boolean sieveArray[];
    private int SIEVE_ERATOS_SIZE = 100000000;

    /* aks constructor */
    public AKSBigInt(BigInt input)
    {
        sieveEratos();

        boolean result = checkIsPrime(input);

        if( result) 
        {
            System.out.println("1");
        }
        else 
        {
            System.out.println("0");
        }
    }
    
    public AKSBigInt()
    {
        sieveEratos();

        
    }
    
    /* function to check if a given number is prime or not */
    public boolean checkIsPrime(BigInt n)
    {
        BigInt lowR, powOf, x, leftH, rightH, fm, aBigNum;
        int totR, quot, tm, aCounter, aLimit, divisor;
        log = (int) logBigNum(n);
        if( findPower(n,log) )
        {
        	System.out.println(log+":"+n);
            return false;
        }
        lowR = new BigInt("2");
        x = lowR.copy();
        totR = lowR.intValue();
        for( lowR = new BigInt("2"); 
                lowR.compareTo(n) < 0; 
                lowR.add(BigInt.ONE))
        {
            if( (lowR.gcd(n)).compareTo(BigInt.ONE) != 0 )
            {
            	System.out.println(lowR.gcd(n)+":"+n);
                return false;
            }
            totR = lowR.intValue();
            if( checkIsSievePrime(totR) )
            {
                quot = largestFactor(totR - 1);
                divisor = (int) (totR - 1) / quot;
                tm = (int) (4 * (Math.sqrt(totR)) * log);
                powOf = mPower(n, new BigInt("" + divisor), lowR);
                if( quot >= tm && (powOf.compareTo(BigInt.ONE)) != 0 ) 
                {
                    break;
                }
            }
        }
        BigInt mP = (mPower(x, lowR, n));
        mP.sub(BigInt.ONE);
        fm = mP.copy();
        aLimit = (int) (2 * Math.sqrt(totR) * log);
        for(aCounter = 1; aCounter < aLimit; aCounter++)
        {
            aBigNum = new BigInt("" + aCounter);
            mP = x.copy();
            mP.sub(aBigNum);
            mP = (mPower(mP, n, n));
            mP.mod(n);
            leftH = mP.copy();
            mP=mPower(x, n, n);
            mP.sub(aBigNum);
            mP.mod(n);
            rightH = mP.copy();
            if(leftH.compareTo(rightH) != 0) { 
            	System.out.println(leftH+":"+rightH);
            	return false;
            }
        }
        return true;
    }
    /* function that computes the log of a big number*/
    public double logBigNum(BigInt bNum)
    {
        String str;
        int len;
        double num1, num2;
        str = "." + bNum.toString();
        len = str.length() - 1;
       num1 = Double.parseDouble(str);
        num2 = Math.log10(num1) + len;
        return num2;
    }
    
    /*function that computes the log of a big number input in string format*/
    public double logBigNum(String str)
    {
        String s;
        int len;
        double num1, num2;
        len = str.length();
        s = "." + str;
        num1 = Double.parseDouble(s);
        num2 = Math.log10(num1) + len;
        return num2;
    }

    /* function to compute the largest factor of a number */
    public int largestFactor(int num)
    {
        int i;
        i = num;
        if(i == 1) return i;
        while(i > 1)
        {
            while( sieveArray[i] == true )
            { 
               i--;
            }
            if(num % i == 0) 
            {
                return i;
            }
            i--;
        }
        return num;
    }

    BigInt temp;
    /*function given a and b, computes if a is power of b */
    public boolean findPowerOf(BigInt bNum, int val)
    {
        int l;
        double len;
        BigInt low, high, mid, res;
        low = new BigInt("10");
        high = new BigInt("10");
        len = (bNum.toString().length()) / val;
        l = (int) Math.ceil(len);
        low = low.pow(l - 1);
        temp = high.pow(l);
        temp.sub(BigInt.ONE);
        high = temp.copy();
        while(low.compareTo(high) <= 0)
        {
        	temp = low.copy();
        	temp.add(high);
            mid = temp.copy();
            mid.div(new BigInt("2"));
            res = mid.pow(val);
            if(res.compareTo(bNum) < 0)
            {
            	temp = mid.copy();
            	temp.add(BigInt.ONE);
                low = temp.copy();
            }
            else if(res.compareTo(bNum) > 0)
            {
            	temp = mid.copy();
            	temp.sub(BigInt.ONE);
                high = temp.copy();
            }
            else if(res.compareTo(bNum) == 0)
            {
                return true;
            }
        }
        return false;
    }

    /* creates a sieve array that maintains a table for COMPOSITE-ness 
     * or possibly PRIME state for all values less than SIEVE_ERATOS_SIZE
     */
    public boolean checkIsSievePrime(int val)
    {
        if(sieveArray[val] == false)
        {
           return true;
        }
        else 
        {
           return false;
        }
    }

    long mPower(long x, long y, long n)
    {
        long m, p, z;
        m = y;
        p = 1;
        z = x;
        while(m > 0)
        {
            while(m % 2 == 0)
            {
                m = (long) m / 2;
                z = (z * z) % n;
            }
            m = m - 1;
            p = (p * z) % n;
        }
        return p;
    }

    /* function, given a and b computes if a is a power of b */
    boolean findPower(BigInt n, int l)
    {
        int i;
        for(i = 2; i < l; i++)
        {
            if(findPowerOf(n, i))
            {
                return true;
            }
        }
        return false;
    }

    BigInt mPower(BigInt x, BigInt y, BigInt n)
    {
        BigInt m, p, z, two;
        m = y.copy();
        p = BigInt.ONE.copy();
        z = x.copy();
        two = BigInt.TWO.copy();
        while(m.compareTo(BigInt.ZERO) > 0)
        {
            temp = m.copy();
            temp.mod(BigInt.TWO);
            while( ( temp.compareTo(BigInt.ZERO) ) == 0)
            {
                m.div(two);
                z.mul(z);
                z.mod(n);
                temp = m.copy();
                temp.mod(BigInt.TWO);
            }
            m.sub(BigInt.ONE);
            p.mul(z);
            p.mod(n);
            
           // System.out.println(m);
        }
        return p;
    }

    /* array to populate sieve array
     * the sieve array looks like this
     *  
     *  y index -> 0 1 2 3 4 5 6 ... n
     *  x index    1  
     *     |       2   T - T - T ...
     *     \/      3     T - - T ...
     *             4       T - - ...
     *             .         T - ...
     *             .           T ...
     *             n
     *             
     *
     *
     *
     */ 
    public void sieveEratos()
    {
        int i, j;
        sieveArray = new boolean[SIEVE_ERATOS_SIZE+1];
        sieveArray[1] = true;
        for(i = 2; i * i <= SIEVE_ERATOS_SIZE; i++)
        {
            if(sieveArray[i] != true)
            {
                for(j = i * i; j <= SIEVE_ERATOS_SIZE; j += i)
                {
                    sieveArray[j] = true;
                }
            }
        }
    }

}









