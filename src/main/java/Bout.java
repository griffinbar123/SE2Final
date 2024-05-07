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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public final class Bout {

    private BufferedOutputStream out;  
    private int buffer;          
    private int N;                


   
    public Bout(OutputStream os) {
        out = new BufferedOutputStream(os);
    }

    public Bout() {
        this(System.out);
    }

    public Bout(String s) {
        try {
            OutputStream os = new FileOutputStream(s);
            out = new BufferedOutputStream(os);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private void writeBit(boolean bit) {
        buffer <<= 1;
        if (bit) buffer |= 1;

        N++;
        if (N == 8) clearBuffer();
    } 

    private void writeByte(int x) {
        assert x >= 0 && x < 256;

        if (N == 0) {
            try { out.write(x); }
            catch (IOException e) { e.printStackTrace(); }
            return;
        }

        for (int i = 0; i < 8; i++) {
            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    private void clearBuffer() {
        if (N == 0) return;
        if (N > 0) buffer <<= (8 - N);
        try { out.write(buffer); }
        catch (IOException e) { e.printStackTrace(); }
        N = 0;
        buffer = 0;
    }

    public void flush() {
        clearBuffer();
        try { out.flush(); }
        catch (IOException e) { e.printStackTrace(); }
    }


    public void close() {
        flush();
        try { out.close(); }
        catch (IOException e) { e.printStackTrace(); }
    }


    public void write(boolean x) {
        writeBit(x);
    } 

    public void write(byte x) {
        writeByte(x & 0xff);
    }

    public void write(int x) {
        writeByte((x >>> 24) & 0xff);
        writeByte((x >>> 16) & 0xff);
        writeByte((x >>>  8) & 0xff);
        writeByte((x >>>  0) & 0xff);
    }

    public void write(int x, int r) {
        if (r == 32) write(x);
        for (int i = 0; i < r; i++) {
            boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }


    public void write(double x) {
        write(Double.doubleToRawLongBits(x));
    }

    public void write(long x) {
        writeByte((int) ((x >>> 56) & 0xff));
        writeByte((int) ((x >>> 48) & 0xff));
        writeByte((int) ((x >>> 40) & 0xff));
        writeByte((int) ((x >>> 32) & 0xff));
        writeByte((int) ((x >>> 24) & 0xff));
        writeByte((int) ((x >>> 16) & 0xff));
        writeByte((int) ((x >>>  8) & 0xff));
        writeByte((int) ((x >>>  0) & 0xff));
    }

    public void write(float x) {
        write(Float.floatToRawIntBits(x));
    }

    public void write(short x) {
        writeByte((x >>>  8) & 0xff);
        writeByte((x >>>  0) & 0xff);
    }


    public void write(char x) {
        writeByte(x);
    }

    public void write(char x, int r) {
        if (r == 8) write(x);
        for (int i = 0; i < r; i++) {
            boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    public void write(String s) {
        for (int i = 0; i < s.length(); i++)
            write(s.charAt(i));
    }

    public void write(String s, int r) {
        for (int i = 0; i < s.length(); i++)
            write(s.charAt(i), r);
    }
}
