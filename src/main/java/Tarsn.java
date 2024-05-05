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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Tarsn
{
    public static void main(String[] args) throws IOException {
        File in = null;
        BinaryIn bin1 = null;
        BinaryOut out = null;
        
        char separator =  (char) 255;  // all ones 11111111

        if (args.length <= 1) {
            throw new IOException("Wrong number of args");
        }
        out = new BinaryOut(args[0]);


        for(int i = 1; i < args.length; i++){
            try { 
                //loop through args and compress them
                in = new File(args[i]);
                if( in.exists() && in.isDirectory()) {
                    throw new IOException("File to be tarsed is a directory");
                }
                if (!in.exists() || !in.isFile()) return;
        
                long filesize = in.length();
                int filenamesize = args[i].length();
                byte[] content = Utils.getBytes(args[i]);
        
                // archive file is at args[0]
                // layout: file-name-length, separator, filename, file-size, file
                //
        
                out.write(filenamesize);
                out.write(separator);
        
                out.write(args[i]);
                out.write(separator);
                // write the file size for the first file
                out.write(filesize);
                
                // write the separator
                out.write(separator);
                // now copy the input file to the output, one character at a time for the first file
                for (byte b : content) {
                    
                    out.write(b);
                }
        
            } catch (Exception e) {
                if (e.getMessage().length() >= 27 && e.getMessage().substring(e.getMessage().length()-27, e.getMessage().length()) == "(No such file or directory)") {
                    System.out.println("Input file does not exist!");
                } else {
                    System.out.println(e.getMessage());
                }
            }
        }
        if (out != null)
            out.close();
    }

    
}