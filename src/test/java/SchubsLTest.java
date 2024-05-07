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
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.io.InputStream;
import java.io.IOException;



public class SchubsLTest {

   
    public static final String FILEPATH =  "src" + File.separator + "test" + File.separator + "resources" + File.separator + "schubsl" + File.separator;

    private final ByteArrayOutputStream newOut = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(newOut));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }


    public String generateRandomString(int ll, int rl, int length) {
        int leftLimit = ll; 
        int rightLimit = rl; 
        int targetStringLength = length;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
            (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    @Test
    public void emptyFile() throws IOException {
        String input = FILEPATH + "empty-file.txt";
        String output = FILEPATH + "empty-file.txt.ll";
        Utils.writeBytes(input, "".getBytes());

        try {
            SchubsL.main(new String[] { input });
        } catch (Exception e) {
            // check that this erros for the right reson (file is empty)
            assertEquals("File content is empty", e.getMessage(), "Reading from empty input stream");
        }
    }

    @Test 
    public void missingFile() throws IOException {
        SchubsL.main(new String[] { FILEPATH + "missing1"});
        
        // extra character at end, delete it.
        String outs = newOut.toString();
        outs = outs.substring(0, outs.length() - 1);

        assertEquals("File content is missing", outs, "WARNING: File does not exist - " + FILEPATH+"missing1");
    }

    @Test
    public void normalCase() throws IOException {
        String input = FILEPATH + "normal-file.txt";
        String output = FILEPATH + "normal-file.txt.ll";

        String rand = generateRandomString(97, 122, 1000);

        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using LZW
        SchubsL.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expand(output));
    }

    @Test
    public void containsManyThings() throws IOException {
        String input = FILEPATH + "many-things-file.txt";
        String output = FILEPATH + "many-things-file.txt.ll";

        String rand = generateRandomString(97, 122, 100000); // generate many more things

        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using LZW
        SchubsL.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expand(output));
    }

    @Test
    public void longWordNoSpaces() throws IOException {
        String input = FILEPATH + "long-word-no-spaces-file.txt";
        String output = FILEPATH + "long-word-no-spaces-file.txt.ll";

        String rand = generateRandomString(97, 122, 10000); // generate many more things

        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using LZW
        SchubsL.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expand(output));
    }

    @Test
    public void lowerCaseWord() throws IOException {
        String input = FILEPATH + "lower-case-file.txt";
        String output = FILEPATH + "lower-case-file.txt.ll";

        String rand = generateRandomString(97, 122, 1000); // this is the ascii charatcers for lowercase chars

        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using LZW
        SchubsL.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expand(output));

    }

    @Test
    public void upperCaseWord() throws IOException {
        String input = FILEPATH + "upper-case-file.txt";
        String output = FILEPATH + "upper-case-file.txt.ll";

        String rand = generateRandomString(65, 90, 10000); // this is the ascii charatcers for uppercase chars
        
        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using LZW
        SchubsL.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expand(output));

    }

    @Test
    public void wrongNumberOfArgs() throws IOException {
        SchubsL.main(new String[] { });
        // extra character at end, delete it.
        String outs = newOut.toString();
        outs = outs.substring(0, outs.length() - 1);

        assertEquals("Wrong number of args", outs, "ERROR: 0 args provided. 1+ required. Ex: SchubsL [File1, File1...]");
    }

    @Test
    public void dirInsteadOfFile() throws IOException {
        File f = new File(FILEPATH + "directory-instead-of-file" ); 
        f.mkdir();
        SchubsL.main(new String[] { FILEPATH + "directory-instead-of-file" });
        // extra character at end, delete it.
        String outs = newOut.toString();
        outs = outs.substring(0, outs.length() - 1);

        assertEquals("Directory instead of file", outs, "WARNING: Directory instead of file - " + FILEPATH+"directory-instead-of-file");
    }
}
