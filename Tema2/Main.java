package com.company;

import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;
import java.util.Random;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        /**
         * Generator BBS
         */
        /** Pas 1 generam numarul N = P * Q **/
        Random random = new Random();
        BigInteger P = new BigInteger(String.valueOf(BigInteger.probablePrime(512,random)));
        while(!P.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)))
        {
            P = new BigInteger(String.valueOf(BigInteger.probablePrime(512,random)));
        }
        System.out.println("P = " + P);
        System.out.println("P modullo 4 este " + P.mod(BigInteger.valueOf(4)) + ", dimensiunea lui P este de " + P.bitLength());
        BigInteger Q = new BigInteger(String.valueOf(BigInteger.probablePrime(512,random)));
        while(!Q.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)))
        {
            Q = new BigInteger(String.valueOf(BigInteger.probablePrime(512,random)));
        }
        System.out.println("Q = " + Q);
        System.out.println("Q modullo 4 este " + Q.mod(BigInteger.valueOf(4)) + ", dimensiunea lui Q este de " + Q.bitLength());
        BigInteger N = new BigInteger(String.valueOf(P.multiply(Q)));
        System.out.println("N = " + N);
        System.out.println("Dimensiunea lui N este de " + N.bitLength());
        /** Pas 2 : generam seedul x0 **/
        BigInteger X = new BigInteger(String.valueOf(BigInteger.valueOf((System.nanoTime()))));
        X = X.pow(2);
        X = X.mod(N);
        System.out.println("Seedul este " + X);
        /** Pas 3 : generatorul propriu-zis **/
        BigInteger index = new BigInteger(String.valueOf(BigInteger.ZERO));
        BigInteger indexBit1 = new BigInteger(String.valueOf(BigInteger.ZERO));
        PrintWriter out = new PrintWriter("outputBBS.txt");
        /** generam un milion de biti **/
        while(index.equals(BigInteger.valueOf(1000000)) == false)
        {
            /**afisam paritatea seedului**/
            out.print(X.mod(BigInteger.TWO));
            /** contorizam numarul de aparitii al bit-ului 1 pentru testarea elementara **/
            if(X.mod(BigInteger.valueOf(2)).equals(BigInteger.ONE))
            {
                indexBit1 = indexBit1.add(BigInteger.ONE);
            }
            /** ridicam seedul la patrat, apoi modullo N **/
            X = X.pow(2);
            X = X.mod(N);
            index = index.add(BigInteger.ONE);
        }
        out.close();
        double numarDe1 = indexBit1.doubleValue();
        numarDe1 = numarDe1*100/1000000;
        double numarDe0 = 100 - numarDe1;
        System.out.println("\n Testare elementara : " + numarDe0 + "% biti de 0, " + numarDe1 + "% biti de 1");
        System.out.println("**************************************************************************************");
        /** Pentru generatorul Jacobi folosim N-ul si seedul calculate anterior **/
        X = new BigInteger(String.valueOf(BigInteger.valueOf((System.nanoTime()))));
        System.out.println("N = " + N);
        System.out.println("Seed = " + X);
        PrintWriter out1 = new PrintWriter("outputJacobi.txt");
        indexBit1 = BigInteger.ZERO;
        for(int i=0; i<1000000; i++) /** afisam un milion de biti **/
        {
            BigInteger X1 = X.add(BigInteger.valueOf(i));
            int semn = jacobiSimbol(X1,N); /** afisam semnul jacobi pentru fiecare Xi / N, daca semnul este -1 il transformam in 0**/
            out1.print(semn);
            if(semn==1)
            {
                indexBit1 = indexBit1.add(BigInteger.ONE);
            }
        }
        numarDe1 = indexBit1.doubleValue();
        numarDe1 = numarDe1*100/1000000;
        numarDe0 = 100 - numarDe1;
        System.out.println("\n Testare elementara : " + numarDe0 + "% biti de 0, " + numarDe1 + "% biti de 1");

    }

    /** functia pentru calculul simbolului jacobi **/
    private static int jacobiSimbol(BigInteger A, BigInteger N) {
        BigInteger b = A.mod(N);
        BigInteger c = N;
        int s = 1;
        while(b.compareTo(BigInteger.TWO) >= 0)
        {
            while(b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(0)))
            {
                b = b.divide(BigInteger.valueOf(4));
            }
            if(b.mod(BigInteger.TWO).equals(BigInteger.valueOf(0)))
            {
                if(c.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(3)) || c.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(5)))
                {
                    s = -s;
                }
                b = b.divide(BigInteger.TWO);
            }
            if(b.equals(BigInteger.valueOf(1)))
            {
                break;
            }
            if(b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)) && c.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)))
            {
                s = -s;
            }
            BigInteger copie = b;
            b = c.mod(b);
            c = copie;
        }
        if(b.multiply(BigInteger.valueOf(s)).equals(BigInteger.valueOf(-1)))
        {
            return b.multiply(BigInteger.valueOf(s)).add(BigInteger.ONE).intValue();
        }
        return b.multiply(BigInteger.valueOf(s)).intValue();

    }
}
