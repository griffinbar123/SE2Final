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

        if (args.length < 1) {
           System.out.println("ERROR: 0 args provided. 1+ required. Ex: Deschubs <ArchiveName> | [File1, File1...]");
           return;
        }

        // error if first file is directory or missing. if not, warn the user and try to uncompress other files
        File in1 = new File(args[0]);
        if( in1.exists() && in1.isDirectory()) {
            System.out.println("ERROR: Directory instead of file - "  + args[0]);
            return;
        }
        if (!in1.exists()) {
            System.out.println("ERROR: File does not exist - " +  args[0]);
            return;
        }
        
        
        String encodedType = args[0].substring(args[0].length()-3, args[0].length());

        if (encodedType.equals(".zh")) {
            System.out.println("This file type not supported");
            return;
        } else if (encodedType.equals(".zl")) {
            args = Untars.main(args);
        }

        for(int i = 0; i < args.length; i++){

            String arg = args[i];

            in1 = new File(arg);
            if( in1.exists() && in1.isDirectory()) { // do a warning if in loop bc we know other arguments could be correct
                System.out.println("WARNING: Directory instead of file - "  + arg);
                return;
            }
            if (!in1.exists()) {
                System.out.println("WARNING: File does not exist - " +  arg);
                return;
            }

            encodedType = arg.substring(args[i].length()-3, arg.length());
            String comp = arg;
            String uncomp = arg.substring(0, arg.length()-3) + (encodedType.equals(".zl") ? ".tar" : "");
            
            FileInputStream in = Utils.openInputFile(comp);
            FileOutputStream out = Utils.openOutputFile(uncomp);
            
            if(encodedType.equals(".ll")){
                LZW lzw = new LZW(out, in, false);
                lzw.expand();
                Utils.deleteFile(arg); // clean up file we don't need
            } else if (encodedType.equals(".hh")) {
                Huffman huffman = new Huffman(out, in, false);
                huffman.expand();
                Utils.deleteFile(arg); // clean up file we don't need
            }
        }

    }
}