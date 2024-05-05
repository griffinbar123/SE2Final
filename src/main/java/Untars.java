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
import java.util.ArrayList;
import java.util.List;

public class Untars 
{
    public static String[] main(String[] args) throws IOException {
        BinaryIn in = null;
        BinaryOut out = null;
        
        char sep =  (char) 255;  // all ones 11111111
    
        // nerf through archive, extracting files
        // int lengthoffilename, sep, filename, sep, lengthoffile, sep, bits

        if(args.length > 1) {
            System.out.println("WARNING: too many args. 1 required args");
        }
        
        List<String> filenames = new ArrayList<String>();

        try {
            File f = new File(args[0]);
            if( f.exists() && f.isDirectory()) {
                throw new IOException("File to be untarsed is a directory");
            }
            
            in = new BinaryIn( args[0] );
            
            while(true){
                int filenamesize;
                try {
                    filenamesize = in.readInt();
                    if(out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    break;
                }
                sep = in.readChar();
                String filename="";
                for (int i=0; i<filenamesize; i++)
                  // concatenate characters to string
                  filename += in.readChar();
                filenames.add(filename);
                sep = in.readChar();
                long filesize = in.readLong();
                sep = in.readChar();
                System.out.println("Extracting file: " + filename + " ("+ filesize +").");
                out = new BinaryOut( filename );
                for (int i=0; i<filesize; i++)
                  // copy input to output
                  out.write(in.readChar());
                
            }
            
    
        } finally {
            if (out != null)
            out.close();
        }	
        String[] filenamesSimple = new String[ filenames.size() ];
        filenames.toArray( filenamesSimple );
        return filenamesSimple;
    }
}