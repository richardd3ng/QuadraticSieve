package algorithms;

import java.math.BigInteger;
import java.util.List;

import static constants.Constants.*;

public class Gaussian {

    //private variables of the Gaussian class
    private boolean[][] matrix;
    private int numBsmooth;
    private int numFactors;
    private boolean[] marked;
    private BigInteger n;

    private List<BigInteger> BSmoothNumbers;
    private List<Integer> factorBase;

    public Gaussian(List<BigInteger> BSmoothNumbers, List<Integer> factorBase, BigInteger n) {
        //set private variables
        this.n = n;
        this.numBsmooth = BSmoothNumbers.size();
        this.numFactors = factorBase.size();
        this.matrix = new boolean[numBsmooth][numFactors];
        this.factorBase = factorBase;
        this.BSmoothNumbers = BSmoothNumbers;
        this.marked = new boolean[numBsmooth];

        //create the GF(2) matrix and then gaussian eliminate it
        createMatrix();
        //printResult();
        fastGauss();
        //printResult();
    }

    /**
     * takes an integer x and returns x^2 (mod n). If the modulo is close to n, it returns the negative counterpart
     *
     * @param x
     * @return the square modulo
     */
    private BigInteger getModulo(BigInteger x) {
        BigInteger result = x.pow(2).mod(n);
        //if the result is greater than n/2 return n minus the result
        if (result.compareTo(n.divide(BigInteger.valueOf(2))) > 0) {
            result = n.subtract(result).abs();
        }
        return result;
    }

    /**
     * Creates the GF(2) matrix based on the list of B smooth numbers and factor base.
     * Every row corresponds to a b smooth number and every column corresponds to a prime factor
     * a_ij is true if the exponent of the jth factor in the prime factorization of the ith b smooth number is odd.
     * It's false if it is even
     */
    private void createMatrix() {
        int row = 0;
        int col;
        boolean parity;
        for (BigInteger bSmooth : BSmoothNumbers) {
            //for every b smooth number, check the square modulo
            //if it is negative set the first element of the matrix to true
            col = 0;
            BigInteger modulo = bSmooth.pow(2).mod(n);
            if (modulo.compareTo(n.divide(BigInteger.valueOf(2))) > 0) {
                modulo = n.subtract(modulo).abs();
                matrix[row][0] = true;
            }
            //for every factor, find the corresponding exponent in the prime factorization
            //store it modulo 2 as boolean value
            for (Integer factor : factorBase) {
                if (factor > 0) {
                    BigInteger bigFactor = BigInteger.valueOf(factor);
                    parity = false;
                    for (BigInteger e = bigFactor; modulo.mod(e).compareTo(ZERO) == 0; e = e.multiply(bigFactor)) {
                        parity = !parity;
                    }
                    matrix[row][col] = parity;
                }
                col++;
            }
            row++;
        }
    }

    /**
     * This function adds the column j to the column k in the GF(2) matrix
     *
     * @param j column j
     * @param k column k
     */
    private void addCol(int j, int k) {
        for (int row = 0; row < numBsmooth; row++) {
            matrix[row][k] = matrix[row][k] ^ matrix[row][j];
        }
    }

    private void fastGauss() {
        for (int col = 0; col < numFactors; col++) {
            int row = 0;
            while (row < numBsmooth && !this.matrix[row][col]) {
                row += 1;
            }
            if (row != numBsmooth) {
                marked[row] = true;
                for (int col2 = 0; col2 < numFactors; col2++) {
                    if (this.matrix[row][col2] && col != col2) {
                        addCol(col, col2);
                    }
                }
            }
        }

    }

    /**
     * Uses gaussian eliminated matrix to find solutions to the original problem
     *
     * @return array of solutions
     */
    public BigInteger[] findSolutions() {
        BigInteger x, y, temp, factorPossibility;
        //run over the marked vector to find a non-marked b smooth number
        for (int mark = 0; mark < numBsmooth; mark++) {
            if (!marked[mark]) {
                //if not marked, find the b smooth number and the corresponding square modulo
                x = BSmoothNumbers.get(mark);
                y = getModulo(x);
                //run through matrix and find other b smooth numbers that share the same prime factors as marked row
                for (int col = 0; col < numFactors; col++) {
                    if (matrix[mark][col]) {
                        for (int row = 0; row < numBsmooth; row++) {
                            if (matrix[row][col] && marked[row]) {
                                //multiply x and y to find the total product
                                temp = BSmoothNumbers.get(row);
                                x = x.multiply(temp);
                                y = y.multiply(getModulo(temp));
                            }
                        }
                    }
                }
                //if x^2=y^2 (mod n), check if gcd(x-y, n) is a nontrivial factor
                y = y.sqrt();
                factorPossibility = MathUtil.gcd(x.subtract(y), n);
                //see if the factor is not n or 1
                if (n.compareTo(factorPossibility) > 0 && factorPossibility.compareTo(ONE) != 0) {
                    BigInteger factor2 = n.divide(factorPossibility);
                    return new BigInteger[]{factorPossibility, factor2};
                }
            }
        }
        return new BigInteger[]{};
    }

    /**
     * prints matrix and marked vector for debugging
     */
    void printResult() {
        System.out.println("MARKED");
        for (int i = 0; i < numBsmooth; i++) {
            System.out.print(marked[i]);
            System.out.print(" ");
            System.out.println();
        }
        System.out.println("MATRIX");
        for (int i = 0; i < numBsmooth; i++) {
            for (int j = 0; j < numFactors; j++) {
                System.out.print(this.matrix[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println(BSmoothNumbers);
        System.out.println(factorBase);
    }
}
