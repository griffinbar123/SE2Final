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


import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.io.InputStream;


import java.io.IOException;


public class UntarsTest {

    public static final String FILEPATH =  "src" + File.separator + "test" + File.separator + "resources" + File.separator + "untars" + File.separator;
    String tars_file = FILEPATH + "testing.tar";

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

        // deleting input files as this is the normal case, and we assume the orignal txts dont exist whe uncompressing
        Utils.deleteFile(FILEPATH + "normal-test1.txt");
        Utils.deleteFile(FILEPATH + "normal-test2.txt");
        Utils.deleteFile(FILEPATH + "normal-test3.txt");

        //uncompress tars files
        Untars.main(new String[] { FILEPATH + "normal-tars.tar" });

        assertEquals("Checking uncompressed tars files' content to the original strings", file1_string, new String(Utils.getBytes(FILEPATH + "normal-test1.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file2_string, new String(Utils.getBytes(FILEPATH + "normal-test2.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file3_string, new String(Utils.getBytes(FILEPATH + "normal-test3.txt")));
    }

    @Test
    public void emptyTarsFile() throws IOException {

        // now call main and see if it errors correctly
        try {
            Untars.main(new String[] { FILEPATH + "empty-tars.tar"});
        } catch (Exception e) {
            // check that this erros for the right reson (file is empty)
            assertEquals("File content is empty", e.getMessage(), "Reading from empty input stream");
        }
    }

    @Test 
    public void missingFile() throws IOException {
        try {
            Untars.main(new String[] { "missing"});
        } catch (Exception e) {
            // check that this errors for the right reson (No such file or directory)
            assertEquals("File content is missing", e.getMessage().substring(e.getMessage().length()-27, e.getMessage().length()), "(No such file or directory)");
        }
    }

    @Test
    public void alreadyExistingDest() throws IOException {

        Utils.writeBytes(FILEPATH + "already-existing-dest-test1.txt", "".getBytes());
        Utils.writeBytes(FILEPATH + "already-existing-dest-test2.txt", "".getBytes());
        Utils.writeBytes(FILEPATH + "already-existing-dest-test3.txt", "".getBytes());

        String file1_string = new String(Utils.getBytes(FILEPATH + "already-existing-dest-test1.txt"));
        String file2_string = new String(Utils.getBytes(FILEPATH + "already-existing-dest-test2.txt"));
        String file3_string = new String(Utils.getBytes(FILEPATH + "already-existing-dest-test3.txt"));

        //uncompress tars files
        Untars.main(new String[] { FILEPATH + "already-existing-dest-tars.tar" });

        assertEquals("Checking uncompressed tars files' content to the original strings", file1_string, new String(Utils.getBytes(FILEPATH + "already-existing-dest-test1.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file2_string, new String(Utils.getBytes(FILEPATH + "already-existing-dest-test2.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file3_string, new String(Utils.getBytes(FILEPATH + "already-existing-dest-test3.txt")));
    }

    @Test
    public void veryLargeTars() throws IOException {
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
    public void verySmallTars() throws IOException {
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
    public void wrongNumberOfArgs() throws IOException {
        // generate random string to write to files
        String file1_string = generateRandomString(97, 122, 10000);
        String file2_string = generateRandomString(97, 122, 10000);
        String file3_string = generateRandomString(97, 122, 10000);

        // write strings to files
        Utils.writeBytes(FILEPATH + "wrong-number-args-test1.txt", file1_string.getBytes());
        Utils.writeBytes(FILEPATH + "wrong-number-args-test2.txt", file2_string.getBytes());
        Utils.writeBytes(FILEPATH + "wrong-number-args-test3.txt", file3_string.getBytes());

        // compress files to tars file
        Tarsn.main(new String[] {FILEPATH + "wrong-number-args-tars.tar", FILEPATH + "wrong-number-args-test1.txt", FILEPATH + "wrong-number-args-test2.txt", FILEPATH + "wrong-number-args-test3.txt"});

        // deleting input files as this is like the normal case, and we assume the orignal txts dont exist whe uncompressing
        Utils.deleteFile(FILEPATH + "wrong-number-args-test1.txt");
        Utils.deleteFile(FILEPATH + "wrong-number-args-test2.txt");
        Utils.deleteFile(FILEPATH + "wrong-number-args-test3.txt");

        //uncompress tars files
        Untars.main(new String[] { FILEPATH + "wrong-number-args-tars.tar", "random arg" });


        assertEquals("Checking uncompressed tars files' content to the original strings", file1_string, new String(Utils.getBytes(FILEPATH + "wrong-number-args-test1.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file2_string, new String(Utils.getBytes(FILEPATH + "wrong-number-args-test2.txt")));
        assertEquals("Checking uncompressed tars files' content to the original strings", file3_string, new String(Utils.getBytes(FILEPATH + "wrong-number-args-test3.txt")));
    }

    @Test
    public void dirInsteadOfTars() throws IOException {
        try {
            Untars.main(new String[] {FILEPATH + "normal-tars.tar"});
        } catch (Exception e) {
            // check that this errors for the right reson (file is empty)
            assertEquals("File is a directory", e.getMessage(), "File to be untarsed is a directory");
        }
    }
}
