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



public class TarsnTest {

    public static final String FILEPATH =  "src" + File.separator + "test" + File.separator + "resources" + File.separator + "tars" + File.separator;

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
    public void normalCase() throws IOException {
        // generate random string to write to files
        String file1_string = generateRandomString(97, 122, 10000);
        String file2_string = generateRandomString(97, 122, 10000);
        String file3_string = generateRandomString(97, 122, 10000);

        // write strings to files
        Utils.writeBytes(FILEPATH + "normal-test1.txt", file1_string.getBytes());
        Utils.writeBytes(FILEPATH + "normal-test2.txt", file2_string.getBytes());
        Utils.writeBytes(FILEPATH + "normal-test3.txt", file3_string.getBytes());

        // compress files to tars file
        Tarsn.main(new String[] {FILEPATH + "normal-tars.tar", FILEPATH + "normal-test1.txt", FILEPATH + "normal-test2.txt", FILEPATH + "normal-test3.txt"});

        // deleting input files as we assume the orignal txts dont exist whe uncompressing and want an accurate check
        Utils.deleteFile(FILEPATH + "normal-test1.txt");
        Utils.deleteFile(FILEPATH + "normal-test2.txt");
        Utils.deleteFile(FILEPATH + "normal-test3.txt");

        //uncompress tars files
        Untars.main(new String[] {  FILEPATH + "normal-tars.tar" });

        assertEquals("Checking uncompressed tars files' content to the original strings", file1_string, new String(Utils.getBytes(FILEPATH + "normal-test1.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file2_string, new String(Utils.getBytes(FILEPATH + "normal-test2.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file3_string, new String(Utils.getBytes(FILEPATH + "normal-test3.txt")));
    }

    @Test
    public void emptyFiles() throws IOException {

        Utils.writeBytes(FILEPATH + "empty-file-test1.txt", "".getBytes());
        Utils.writeBytes(FILEPATH + "empty-file-test2.txt", "".getBytes());
        Utils.writeBytes(FILEPATH + "empty-file-test3.txt", "".getBytes());

        // now call main and see if it errors correctly
        try {
            Tarsn.main(new String[] {FILEPATH + "empty-file-tars.tar", FILEPATH + "empty-file-test1.txt", FILEPATH + "empty-file-test2.txt", FILEPATH + "empty-file-test3.txt"});
        } catch (Exception e) {
            // check that this errors for the right reson (file is empty)
            assertEquals("File content is empty", e.getMessage(), "Reading from empty input stream");
        }
    }


    @Test
    public void alreadyExistingDest() throws IOException {

        // creatr tars files and chekc if program still works
        // Utils.writeBytes(FILEPATH + "already-existing-dest-tars.tar", "".getBytes());

        // generate random string to write to files
        String file1_string = generateRandomString(97, 122, 10000);
        String file2_string = generateRandomString(97, 122, 10000);
        String file3_string = generateRandomString(97, 122, 10000);

        // write strings to files
        Utils.writeBytes(FILEPATH + "already-existing-dest-test1.txt", file1_string.getBytes());
        Utils.writeBytes(FILEPATH + "already-existing-dest-test2.txt", file2_string.getBytes());
        Utils.writeBytes(FILEPATH + "already-existing-dest-test3.txt", file3_string.getBytes());

        //compress text files
        Tarsn.main(new String[] { FILEPATH + "already-existing-dest-tars.tar", FILEPATH + "already-existing-dest-test1.txt", FILEPATH + "already-existing-dest-test2.txt", FILEPATH + "already-existing-dest-test3.txt" });

        // deleting input files as we assume the orignal txts dont exist whe uncompressing and want an accurate check
        Utils.deleteFile(FILEPATH + "already-existing-dest-test1.txt");
        Utils.deleteFile(FILEPATH + "already-existing-dest-test2.txt");
        Utils.deleteFile(FILEPATH + "already-existing-dest-test3.txt");

        //uncompress tars files
        Untars.main(new String[] {  FILEPATH + "already-existing-dest-tars.tar" });

        assertEquals("Checking uncompressed tars files' content to the original strings", file1_string, new String(Utils.getBytes(FILEPATH + "already-existing-dest-test1.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file2_string, new String(Utils.getBytes(FILEPATH + "already-existing-dest-test2.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file3_string, new String(Utils.getBytes(FILEPATH + "already-existing-dest-test3.txt")));
    }

    @Test
    public void veryLargeFiles() throws IOException {
        // generate large random string to write to files
        String file1_string = generateRandomString(97, 122, 100000);
        String file2_string = generateRandomString(97, 122, 100000);
        String file3_string = generateRandomString(97, 122, 100000);

        // write strings to files
        Utils.writeBytes(FILEPATH + "very-large-test1.txt", file1_string.getBytes());
        Utils.writeBytes(FILEPATH + "very-large-test2.txt", file2_string.getBytes());
        Utils.writeBytes(FILEPATH + "very-large-test3.txt", file3_string.getBytes());

        // compress files to tars file
        Tarsn.main(new String[] {FILEPATH + "very-large-tars.tar", FILEPATH + "very-large-test1.txt", FILEPATH + "very-large-test2.txt", FILEPATH + "very-large-test3.txt"});

        // deleting input files as this is like the normal case, and we assume the orignal txts dont exist whe uncompressing
        Utils.deleteFile(FILEPATH + "very-large-test1.txt");
        Utils.deleteFile(FILEPATH + "very-large-test2.txt");
        Utils.deleteFile(FILEPATH + "very-large-test3.txt");

        //uncompress tars files
        Untars.main(new String[] { FILEPATH + "very-large-tars.tar" });

        assertEquals("Checking uncompressed tars files' content to the original strings", file1_string, new String(Utils.getBytes(FILEPATH + "very-large-test1.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file2_string, new String(Utils.getBytes(FILEPATH + "very-large-test2.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file3_string, new String(Utils.getBytes(FILEPATH + "very-large-test3.txt")));
    }

    @Test
    public void verySmallFiles() throws IOException {
        // generate large random string to write to files
        String file1_string = generateRandomString(97, 122, 5);
        String file2_string = generateRandomString(97, 122, 5);
        String file3_string = generateRandomString(97, 122, 5);

        // write strings to files
        Utils.writeBytes(FILEPATH + "very-small-test1.txt", file1_string.getBytes());
        Utils.writeBytes(FILEPATH + "very-small-test2.txt", file2_string.getBytes());
        Utils.writeBytes(FILEPATH + "very-small-test3.txt", file3_string.getBytes());

        // compress files to tars file
        Tarsn.main(new String[] {FILEPATH + "very-small-tars.tar", FILEPATH + "very-small-test1.txt", FILEPATH + "very-small-test2.txt", FILEPATH + "very-small-test3.txt"});

        // deleting input files as this is like the normal case, and we assume the orignal txts dont exist whe uncompressing
        Utils.deleteFile(FILEPATH + "very-small-test1.txt");
        Utils.deleteFile(FILEPATH + "very-small-test2.txt");
        Utils.deleteFile(FILEPATH + "very-small-test3.txt");

        //uncompress tars files
        Untars.main(new String[] { FILEPATH + "very-small-tars.tar" });

        assertEquals("Checking uncompressed tars files' content to the original strings", file1_string, new String(Utils.getBytes(FILEPATH + "very-small-test1.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file2_string, new String(Utils.getBytes(FILEPATH + "very-small-test2.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file3_string, new String(Utils.getBytes(FILEPATH + "very-small-test3.txt")));
    }

    @Test
    public void veryLargeVerySmallAndEmptyFiles() throws IOException {
        // generate large random string to write to files
        String file1_string = generateRandomString(97, 122, 100000);
        String file2_string = generateRandomString(97, 122, 5);
        String file3_string = "";

        // write strings to files
        Utils.writeBytes(FILEPATH + "very-large-very-small-and-empty-test1.txt", file1_string.getBytes());
        Utils.writeBytes(FILEPATH + "very-large-very-small-and-empty-test2.txt", file2_string.getBytes());
        Utils.writeBytes(FILEPATH + "very-large-very-small-and-empty-test3.txt", file3_string.getBytes());

        // compress files to tars file
        Tarsn.main(new String[] {FILEPATH + "very-large-very-small-and-empty-tars.tar", FILEPATH + "very-large-very-small-and-empty-test1.txt", FILEPATH + "very-large-very-small-and-empty-test2.txt", FILEPATH + "very-large-very-small-and-empty-test3.txt"});

        // deleting input files as this is like the normal case, and we assume the orignal txts dont exist whe uncompressing
        Utils.deleteFile(FILEPATH + "very-large-very-small-and-empty-test1.txt");
        Utils.deleteFile(FILEPATH + "very-large-very-small-and-empty-test2.txt");
        Utils.deleteFile(FILEPATH + "very-large-very-small-and-empty-test3.txt");

        //uncompress tars files
        Untars.main(new String[] { FILEPATH + "very-large-very-small-and-empty-tars.tar" });

        assertEquals("Checking uncompressed tars files' content to the original strings", file1_string, new String(Utils.getBytes(FILEPATH + "very-large-very-small-and-empty-test1.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file2_string, new String(Utils.getBytes(FILEPATH + "very-large-very-small-and-empty-test2.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file3_string, new String(Utils.getBytes(FILEPATH + "very-large-very-small-and-empty-test3.txt")));
    }

    @Test
    public void wrongNumberOfArgs() throws IOException {
        Tarsn.main(new String[] { });
        // extra character at end, delete it.
        String outs = newOut.toString();
        outs = outs.substring(0, outs.length() - 1);

        assertEquals("Wrong number of args", outs, "ERROR: 0 args provided. 2+ required. Ex: Tarsn <ArchiveName> [File1, File1...]");
        
    }

}
