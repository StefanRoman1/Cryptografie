package com.company;

import java.util.BitSet;
import java.util.Random;

public class Main {
    /**Algoritmul de permutare al cheii**/
    public static Integer[] KSA(int n, int l, Integer[]key)
    {
        Integer[]S=new Integer[n];
        for(int i=0;i<=n-1;i++)
        {
            S[i]=i;
        }
        int j=0;
        for(int i=0;i<=n-1;i++)
        {
            j=(j+S[i]+key[i%l])%n;
            int aux=S[i];
            S[i]=S[j];
            S[j]=aux;
        }
        return (Integer[]) S;
    }

    /**Generatorul propriu-zis**/
    public static Integer PRGA(Integer[] S, int n)
    {
        int count=0;
        Integer M = 0;
        int i=0;
        int j=0;
        int x=0;
        while(x<16)
        {
            i=(i+1)%n;
            j=(j+S[i])%n;
            int aux=S[i];
            S[i]=S[j];
            S[j]=aux;
            M=S[(S[i]+S[j])%n];
            x++;
            /**Verificam daca pe byte-ul 2 avem valoarea 0**/
            if(M==0 && x==1)
            {
                count++;
            }
        }
        return count;
    }

    public static void RC(){
        int n=256;
        int l=16;
        Integer[] key = new Integer[l];
        for(int i = 0; i<l; i++)
        {
            Random r = new Random();
            key[i] =  r.nextInt(255);
        }
        /**Shiftam cheia**/
        key = KSA(n,l, key);
        int sum = 0;
        /**Adunam de cate ori avem valoarea 0 pe pozitia 128**/
        for(int i = 0; i<128; i++)
        {
            sum += PRGA(key, n);
        }
        double prob = 0;
        prob = (double) sum/128;
        System.out.println();
        System.out.println("Probabilitatea sa avem valoarea 0 pe byte-ul 2 este de " + prob);

    }


    public static boolean feedbackFunction(BitSet bitSet){
        /** Polinomul primitiv este : x^16 + x^5 + x^3 + x^2 + 1 **/
        BitSet bit15=new BitSet();
        bit15.set(0,bitSet.get(15));
        BitSet bit4=new BitSet();
        bit4.set(0,bitSet.get(4));
        BitSet bit2=new BitSet();
        bit2.set(0,bitSet.get(2));
        BitSet bit1=new BitSet();
        bit1.set(0,bitSet.get(1));
        bit1.xor(bit2);
        bit1.xor(bit4);
        bit1.xor(bit15);
        return bit1.get(0);
    }


    public static StringBuilder LSFR()
    {
        /**Functia generatorului LSFR**/
        StringBuilder rezultatBiti=new StringBuilder();
        BitSet bitSet= new BitSet(16);
        /** Starea initiala **/
        for(int i=0;i<16;i++)
        {
            Random rand = new Random();
            int bitRandom=rand.nextInt(2);
            if(bitRandom==1)
            {
                bitSet.set(i,true);
            }
            else
            {
                bitSet.set(i,false);
            }
        }
        /**adaugam ultimul bit la rezultat**/
        if(bitSet.get(15))
        {
            rezultatBiti.append(1);
        }
        else
        {
            rezultatBiti.append(0);
        }

        StringBuilder sir=new StringBuilder();
        for(int i=0;i<16;i++)
        {
            if(bitSet.get(i))
            {
                sir.append(1);
            }
            else
            {
                sir.append(0);
            }
        }
        StringBuilder initialBitSet=sir;

        System.out.println("Starea initiala este : " + sir);

        int var=0;
        /** generam exista lungimea perioadei de biti, mai exact 65536**/
        while(var<=256*256-1)
        {
            StringBuilder nextSirBiti=new StringBuilder();
            BitSet nextBitSet = new BitSet(16);
            /** setam primul bit din urmatorul sir**/
            nextBitSet.set(0, feedbackFunction(bitSet));
            /**populam urmatorul sir prin siftarea sirului actual**/
            for(int i=0;i<16;i++)
            {
                nextBitSet.set(i+1, bitSet.get(i) );
                if(!nextBitSet.get(i)){
                    nextSirBiti.append(0);
                }
                else
                {
                    nextSirBiti.append(1);
                }
            }
            /** adaugam ultimul bit la output **/
            if(nextBitSet.get(15)){
                rezultatBiti.append(1);
            }
            else
            {
                rezultatBiti.append(0);
            }
            /** verificam daca perioada e maxima**/
            if(nextSirBiti.compareTo(initialBitSet)==0)
            {
                System.out.println("Starea " +  (var + 2) + " este egala cu starea initiala!!!");
            }
            bitSet=nextBitSet;
            var++;
        }
        System.out.println("Output: "+ rezultatBiti);
        return rezultatBiti;
    }

    public static void main(String[] args) {
        double timpLSFRInceput = System.nanoTime();
        LSFR();
        double timpLSFRFinal = System.nanoTime();
        double  timpLSFRTotal = timpLSFRFinal - timpLSFRInceput;
        System.out.println("Lista timpi : ");
        System.out.println("LSFR : " + timpLSFRTotal / 1000000000);
        System.out.println("BBS : 0.5383013");
        System.out.println("Jacobi : 0.7211801");
        System.out.println("#################################################");
        RC();
    }
}
