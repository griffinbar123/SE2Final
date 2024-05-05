
/*  Program: FinalProject
*  Author : Griffin Barnard
*  Date   : Fall 2024 (25 April 2024)
*  Course : CS375 Software Engineering II
*  Compile: mvn compile
*  Execute: 
*      Compression (LZW): java -cp target/classes SchubsL src/files/test1.txt [file2 file3 ...] or
*                         java -cp target/classes SchubsL src/files/*.txt
*      Compression (Huf): java -cp target/classes SchubsH src/files/test1.txt [file2 file3 ...] or
*                         java -cp target/classes SchubsH src/files/*.txt
* 
*      Uncompression    : java -cp target/classes Deschubs src/files/test1.txt.(ll or hh) [file2 file3 ...] or
*                         java -cp target/classes Deschubs src/files/*.txt.(ll or hh) 
* 
*      Tars             : java -cp target/classes SchubsArc src/files/archive.tar src/files/test1.txt [file2 file3 ...] or
*                         java -cp target/classes SchubsArc src/files/archive.tar src/files/*.txt
* 
*      Untars           : java -cp target/classes Deschubs src/files/archive.zl
* 
*  Note   : This program handles compressing, uncompressing, tarsing, and untarsing files. 
*           it can take many files, compress them and then tars them
*  Needs  : try/catch part for missing files.
*           command line arguments differ based of commands being ran. see execute examples listed above
*/

import java.io.InputStream;
import java.io.OutputStream;

public class LZW {
    private static final int R = 128;        // number of input chars
    private static final int L = 256;       // number of codewords = 2^W
    private static final int W = 8;         // codeword width

    public BinaryOut binaryStdOut;
    public BinaryIn binaryStdIn;
    public boolean log;

    public LZW(OutputStream outs, InputStream ins, boolean l) {
        binaryStdIn = new BinaryIn(ins);
        binaryStdOut = new BinaryOut(outs);
        log = l;
    }

    public LZW(OutputStream outs, BinaryIn ins, boolean l) {
        binaryStdIn = ins;
        binaryStdOut = new BinaryOut(outs);
        log = l;
    }

    public  void compress() { 
        String input = binaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        if(log)
            System.out.println("N1 [label=\"LZW R 128 L 256  W 8\"];");
        for (int i = 0; i < R; i++){
            st.put("" + (char) i, i);
            if(log)
                System.out.print(String.valueOf((char) i) +": " + Integer.toString(i) +" ");
        }
        if(log)
            System.out.println("");
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            binaryStdOut.write(st.get(s), W);      // Print s's encoding.
            if(log)
                System.out.println("out " + Integer.toString(st.get(s)) +  " " + s );
            int t = s.length();
            if (t < input.length() && code < L) {    // Add s to symbol table.
                if(log)
                    System.out.println("       " + input.substring(0, t + 1) +  " " + code );
                st.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);            // Scan past s in input.
        }
        binaryStdOut.write(R, W);
        binaryStdOut.close();
    } 


    public void expand() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = binaryStdIn.readInt(W);
        String val = st[codeword];
        if(log)
            System.out.println("in " + Integer.toString(codeword) );
        // String st = val;
        if(log)
            System.out.println( "out " + val + "\n");

        String output = val;
        
        while (true) {
            binaryStdOut.write(val);
            codeword = binaryStdIn.readInt(W);
            if(log)
                System.out.print("in " + Integer.toString(codeword));
            if (codeword == R) {
                if(log)
                    System.out.println("\ncodeword == " + "R");
                break;
            }
            String s = st[codeword];
            if(log)
                System.out.println( " st: " + s);
            output = output + s;
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) {
                st[i++] = val + s.charAt(0);
                if(log)
                    System.out.println( "i<L " + st[i-1]);
            }
            val = s;
            if(log)
                System.out.println( "out " + val + "\n");
        }
        if(log)
            System.out.println(output);
        binaryStdOut.close();
    }



    // public static void main(String[] args) {
    //     binaryStdIn = new BinaryStdIn(System.in);
    //     binaryStdOut = new BinaryStdOut(System.out);

    //     if      (args[0].equals("-")) compress();
    //     else if (args[0].equals("+")) expand();
    //     else throw new RuntimeException("Illegal command line argument");
    // }

}
