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

public class Deschubs 
{
    public static final String FILEPATH =  "";

    public static void main(String[] args) throws java.io.IOException {

        if (args.length == 0) {
            throw new IOException("Wrong number of args");
        }
        
        String encodedType = args[0].substring(args[0].length()-3, args[0].length());

        if (encodedType.equals(".zh")) {
            System.out.println("This file type not supported");
            return;
        } else if (encodedType.equals(".zl")) {
            args = Untars.main(args);
        }

        for(int i = 0; i < args.length; i++){
            encodedType = args[i].substring(args[i].length()-3, args[i].length());
            String comp = args[i];
            String uncomp = args[i].substring(0, args[i].length()-3) + (encodedType.equals(".zl") ? ".tar" : "");
            
            FileInputStream in = Utils.openInputFile(comp);
            FileOutputStream out = Utils.openOutputFile(uncomp);
            
            if(encodedType.equals(".ll")){
                LZW lzw = new LZW(out, in, false);
                lzw.expand();
            } else if (encodedType.equals(".hh")) {
                Huffman huffman = new Huffman(out, in, false);
                huffman.expand();
            }
        }

    }
}