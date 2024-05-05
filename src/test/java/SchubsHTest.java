import static org.junit.Assert.assertEquals;
import org.junit.Test;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.io.InputStream;

//import org.apache.commons.io.IOUtils.*;
// import LZWSE

import java.io.IOException;


public class SchubsHTest {

   
    public static final String FILEPATH =  "src" + File.separator + "test" + File.separator + "resources" + File.separator + "schubsh" + File.separator;

    public String generateRandomString(int hh, int rl, int length) {
        int leftLimit = hh; 
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
        String output = FILEPATH + "empty-file.txt.hh";
        Utils.writeBytes(input, "".getBytes());

        try {
            SchubsH.main(new String[] { input });
        } catch (Exception e) {
            // check that this erros for the right reson (file is empty)
            assertEquals("File content is empty", e.getMessage(), "Reading from empty input stream");
        }
    }

    @Test 
    public void missingFile() throws IOException {
        try {
            SchubsH.main(new String[] { FILEPATH +  "missing" });
        } catch (Exception e) {
            // check that this errors for the right reson (No such file or directory)
            assertEquals("File content is missing", e.getMessage().substring(e.getMessage().length()-27, e.getMessage().length()), "(No such file or directory)");
        }
    }

    @Test
    public void normalCase() throws IOException {
        String input = FILEPATH + "normal-file.txt";
        String output = FILEPATH + "normal-file.txt.hh";

        String rand = generateRandomString(97, 122, 1000);

        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using Huffman encoding
        SchubsH.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expandH(output));
    }

    @Test
    public void containsManyThings() throws IOException {
        String input = FILEPATH + "many-things-file.txt";
        String output = FILEPATH + "many-things-file.txt.hh";

        String rand = generateRandomString(97, 122, 100000); // generate many more things

        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using Huffman encoding
        SchubsH.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expandH(output));
    }

    @Test
    public void longWordNoSpaces() throws IOException {
        String input = FILEPATH + "long-word-no-spaces-file.txt";
        String output = FILEPATH + "long-word-no-spaces-file.txt.hh";

        String rand = generateRandomString(97, 122, 10000); // generate many more things

        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using Huffman encoding
        SchubsH.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expandH(output));
    }

    @Test
    public void lowerCaseWord() throws IOException {
        String input = FILEPATH + "lower-case-file.txt";
        String output = FILEPATH + "lower-case-file.txt.hh";

        String rand = generateRandomString(97, 122, 1000); // this is the ascii charatcers for lowercase chars

        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using Huffman encoding
        SchubsH.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expandH(output));

    }

    @Test
    public void upperCaseWord() throws IOException {
        String input = FILEPATH + "upper-case-file.txt";
        String output = FILEPATH + "upper-case-file.txt.hh";

        String rand = generateRandomString(65, 90, 10000); // this is the ascii charatcers for uppercase chars
        
        // write base data to input file 
        Utils.writeBytes(input, rand.getBytes());

        // now call main, which will take from the file we give it and compress it using Huffman encoding
        SchubsH.main(new String[] { input });

        // check the compressde file and see if uncompresed string matches input
        assertEquals("Expand compressed output and check it to original string", rand, Utils.expandH(output));

    }

    @Test
    public void wrongNumberOfArgs() throws IOException {
        try {
            SchubsH.main(new String[] { });
        } catch (Exception e) {
            // check that this errors for the right reson (file is empty)
            assertEquals("Wrong number of args", e.getMessage(), "Wrong number of args");
        }
    }
}
