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
 *      Tars (with lzw)  : java -cp target/classes SchubsArc src/files/archive.tar src/files/test1.txt [file2 file3 ...] or
 *                         java -cp target/classes SchubsArc src/files/archive.tar src/files/*.txt
 * 
 *      Deschubs         : java -cp target/classes Deschubs src/files/archive.zl
 * 
 *  Note   : This program handles compressing, uncompressing, tarsing, and untarsing compressed files files. 
 *           does no need to be the hard coded files listed above
 *  Needs  : try/catch part for missing files.
 *           command line arguments differ based of commands being ran. see execute examples listed above
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;


public class Utils {

    public static final String FILEPATH = "";

    public static String expand(String ins) throws java.io.IOException {
        OutputStream output = new OutputStream()
        {
            private StringBuilder string = new StringBuilder();
            @Override
            public void write(int x) throws IOException {
                this.string.append((char) x );
            }
    
            public String toString(){
                return this.string.toString();
            }
        };
        FileInputStream in = openInputFile(ins);
        LZW lzw2 = new LZW(output, in, false);
        lzw2.expand();
        closeFileStream(in);
        return output.toString();
        // closeFileStream(out);
    }

    public static byte[] compress(String ins) throws java.io.IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        // OutputStream output = new OutputStream();
        FileInputStream in = openInputFile(ins);
        LZW lzw2 = new LZW(baos, in, false);
        lzw2.compress();
        closeFileStream(in);
        // baos.writeTo(myOutputStream); 
        return  baos.toByteArray();
        // closeFileStream(out);
    }

    public static String expandH(String ins) throws java.io.IOException {
        OutputStream output = new OutputStream()
        {
            private StringBuilder string = new StringBuilder();
            @Override
            public void write(int x) throws IOException {
                this.string.append((char) x );
            }
    
            public String toString(){
                return this.string.toString();
            }
        };
        FileInputStream in = openInputFile(ins);
        Huffman huffman = new Huffman(output, in, false);
        huffman.expand();
        closeFileStream(in);
        return output.toString();
        // closeFileStream(out);
    }

    public static byte[] compressH(String ins) throws java.io.IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        // OutputStream output = new OutputStream();
        FileInputStream in = openInputFile(ins);
        Huffman huffman = new Huffman(baos, in, false);
        huffman.compress();
        closeFileStream(in);
        // baos.writeTo(myOutputStream); 
        return  baos.toByteArray();
        // closeFileStream(out);
    }

    public static boolean checkIfFileExists(String file) {
        try {
            openInputFile(file);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static FileInputStream openInputFile(String inputFileString) throws IOException {
        // open an input file stream from a file string
        return new FileInputStream(FILEPATH + inputFileString);
    }
    public static FileOutputStream openOutputFile(String outputFileString) throws IOException {
        // open an output file stream from a file string
        return new FileOutputStream(FILEPATH + outputFileString);
    }

    public static void closeFileStream(InputStream stream) throws IOException {
        // close a file stream from a given io stream
        if(stream != null)
            stream.close();
    }
    public static void closeFileStream(OutputStream stream) throws IOException {
        // overloaded function to close a file stream from a given io stream
        if(stream != null)
            stream.close();
    }
    public static void writeBytes(String path, byte[] bytesFromInputFile) throws IOException{
        FileOutputStream outputFileStream = null;
        try {
            outputFileStream = openOutputFile(path);
            outputFileStream.write(bytesFromInputFile);
        } finally {
            closeFileStream(outputFileStream);
        }
    }
    public static byte[] getBytes(String path) throws IOException{
        // get a single byte from an input stream
        FileInputStream inputFileStream = null;
        try {
            inputFileStream = openInputFile(path);
            return inputFileStream.readAllBytes();
        } finally {
            closeFileStream(inputFileStream);
        }
    }

    public static void deleteFile(String file){
        File myObj = new File(file); 
        myObj.delete();
    }
}