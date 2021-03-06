package constants;

import java.math.BigInteger;

public class Constants {

    // toggle to show print statements/steps
    public static final boolean DEBUG_EUCLIDEAN_ALGORITHM = false;
    public static final boolean DEBUG_QUADRATIC_SIEVE = true;

    // commonly used constants
    public static final BigInteger ZERO = BigInteger.ZERO;
    public static final BigInteger ONE = BigInteger.ONE;

    // Quadratic Sieve constants
    public static final int SIEVING_INTERVAL_B_FACTOR = 1; // sets sieving interval of size 2*factor*B
}
