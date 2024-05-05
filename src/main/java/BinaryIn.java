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
 * 
 * 
*/

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public final class BinaryIn {
    private static final int EOF = -1;   

    private BufferedInputStream in;      
    private int buffer;                  
    private int N;                       


    public BinaryIn() {
        in = new BufferedInputStream(System.in);
        fillBuffer();
    }

    public BinaryIn(InputStream is) {
        in = new BufferedInputStream(is);
        fillBuffer();
    }


    public BinaryIn(String s) {

        try {
            File file = new File(s);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                in = new BufferedInputStream(fis);
                fillBuffer();
                return;
            }
        }
        catch (IOException ioe) {
            System.err.println("Could not open " + s);
        }
    }

    private void fillBuffer() {
        try { buffer = in.read(); N = 8; }
        catch (IOException e) { System.err.println("EOF"); buffer = EOF; N = -1; }
    }

    public boolean exists()  {
        return in != null;
    }

   public boolean isEmpty() {
        return buffer == EOF;
    }


    public boolean readBoolean() {
        if (isEmpty()) throw new RuntimeException("Reading from empty input stream");
        N--;
        boolean bit = ((buffer >> N) & 1) == 1;
        if (N == 0) fillBuffer();
        return bit;
    }
    
    public char readChar() {
        if (isEmpty()) throw new RuntimeException("Reading from empty input stream");

        if (N == 8) {
            int x = buffer;
            fillBuffer();
            return (char) (x & 0xff);
        }

        int x = buffer;
        x <<= (8-N);
        int oldN = N;
        fillBuffer();
        if (isEmpty()) throw new RuntimeException("Reading from empty input stream");
        N = oldN;
        x |= (buffer >>> N);
        return (char) (x & 0xff);
    }

    public char readChar(int r) {
        if (r == 8) return readChar();

        char x = 0;
        for (int i = 0; i < r; i++) {
            x <<= 1;
            boolean bit = readBoolean();
            if (bit) x |= 1;
        }
        return x;
    }


    public String readString() {
        if (isEmpty()) throw new RuntimeException("Reading from empty input stream");

        StringBuilder sb = new StringBuilder();
        while (!isEmpty()) {
            char c = readChar();
            sb.append(c);
        }
        return sb.toString();
    }

   public int readInt() {
        int x = 0;
        for (int i = 0; i < 4; i++) {
            char c = readChar();
            x <<= 8;
            x |= c;
        }
        return x;
    }

   public int readInt(int r) {

        // optimize r = 32 case
        if (r == 32) return readInt();

        int x = 0;
        for (int i = 0; i < r; i++) {
            x <<= 1;
            boolean bit = readBoolean();
            if (bit) x |= 1;
        }
        return x;
    }

   public long readLong() {
        long x = 0;
        for (int i = 0; i < 8; i++) {
            char c = readChar();
            x <<= 8;
            x |= c;
        }
        return x;
    }

    public byte readByte() {
        char c = readChar();
        byte x = (byte) (c & 0xff);
        return x;
    }
    
}
