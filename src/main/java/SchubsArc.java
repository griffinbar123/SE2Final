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
 *      Tars             : java -cp target/classes SchubsArc src/files/archive.zl src/files/test1.txt [file2 file3 ...] or
 *                         java -cp target/classes SchubsArc src/files/archive.zl src/files/*.txt
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
import java.util.Arrays;

public class SchubsArc 
{
    public static final String FILEPATH =  "";

    public static void main(String[] args) {

        // create the ll files and then tars them
        try {
            if (args.length <= 1) {
                System.out.println("ERROR: " + args.length + " args provided. 2+ required. Ex: SchubsArc <ArchiveName> [File1, File1...]");
                return;
            }

            String[] schubsLArgs = Arrays.copyOfRange(args, 1, args.length);
            SchubsL.main(schubsLArgs);
            for(int i = 0; i < args.length; i++){
                if(i == 0){                    
                    String ext = args[i].substring(args[i].length()-3, args[i].length());
                    if(ext.equals(".zl"))
                        continue;
                    else if(ext.equals(".zh"))
                        args[0] = args[0].substring(0, args[0].length() - 3) + ".zl";
                    else
                        args[0] = args[0].substring(0, args[0].length() - (args[0].substring(args[0].length()-4, args[0].length()).equals(".tar") ? 4 : 0)) + ".zl";
                    continue;
                }
                args[i] = args[i] + ".ll";
            }

            Tarsn.main(args);

            for(int i = 1; i < args.length; i++){
                Utils.deleteFile(args[i]);
            }
            
        } catch (Exception e) {
            System.out.println("You Messed Up!: " + e.getMessage());
        }


    }
}