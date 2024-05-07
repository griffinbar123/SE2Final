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

public class SchubsL 
{
    public static final String FILEPATH =  "";

    public static void main(String[] args) {

        if (args.length < 1) {
           System.out.println("ERROR: 0 args provided. 1+ required. Ex: SchubsL [File1, File1...]");
           return;
        }

        try {
            for(int i = 0; i < args.length; i++){
                File in1 = new File(args[i]);
                if( in1.exists() && in1.isDirectory()) {
                    System.out.println("WARNING: Directory instead of file - "  + args[i]);
                    continue;
                }
                if (!in1.exists()) {
                System.out.println("WARNING: File does not exist - " +  args[i]);
                    continue;
                }
                String comp = args[i] + ".ll";
                String uncomp = args[i];
                
                FileOutputStream out = Utils.openOutputFile(comp);
                FileInputStream in = Utils.openInputFile(uncomp);
                
                LZW lzw = new LZW(out, in, false);
                lzw.compress();
            }
        } catch (Exception e) {
            System.out.println("You Messed Up!: " + e.getMessage());
        }



    }
}