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

import java.io.InputStream;
import java.io.OutputStream;

public class Huffman {

    private  final int R = 256;

    public Bout binaryStdOut;
    public Bin binaryStdIn;
    public boolean log;

    public Huffman(OutputStream outs, InputStream ins, boolean l) {
        binaryStdIn = new Bin(ins);
        binaryStdOut = new Bout(outs);
        log = l;
    }

    private  class Node implements Comparable<Node> {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        private boolean isLeaf() {
            assert (left == null && right == null) || (left != null && right != null);
            return (left == null && right == null);
        }

        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }


    public  void compress() {
        String s = binaryStdIn.readString();
        char[] input = s.toCharArray();

        int[] freq = new int[R];
        for (int i = 0; i < input.length; i++)
            freq[input[i]]++;

        Node root = buildTrie(freq);

        String[] st = new String[R];
        buildCode(st, root, "");

        writeTrie(root);

        binaryStdOut.write(input.length);

        for (int i = 0; i < input.length; i++) {
            String code = st[input[i]];
            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '0') {
                    binaryStdOut.write(false);
                }
                else if (code.charAt(j) == '1') {
                    binaryStdOut.write(true);
                }
                else throw new RuntimeException("Illegal state");
            }
        }

        binaryStdOut.flush();
    }

    private  Node buildTrie(int[] freq) {

        MinPQ<Node> pq = new MinPQ<Node>();
        for (char i = 0; i < R; i++)
            if (freq[i] > 0)
                pq.insert(new Node(i, freq[i], null, null));

        while (pq.size() > 1) {
            Node left  = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.insert(parent);
        }
        return pq.delMin();
    }


    private  void writeTrie(Node x) {
        if (x.isLeaf()) {
            binaryStdOut.write(true);
            binaryStdOut.write(x.ch);
            return;
        }
        binaryStdOut.write(false);
        writeTrie(x.left);
        writeTrie(x.right);
    }

    private  void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left,  s + '0');
            buildCode(st, x.right, s + '1');
        }
        else {
            st[x.ch] = s;
        }
    }


    public  void expand() {

        Node root = readTrie(); 

        int length = binaryStdIn.readInt();

        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = binaryStdIn.readBoolean();
                if (bit) x = x.right;
                else     x = x.left;
            }
            binaryStdOut.write(x.ch);
        }
        binaryStdOut.flush();
    }


    private  Node readTrie() {
        boolean isLeaf = binaryStdIn.readBoolean();
        if (isLeaf) {
            return new Node(binaryStdIn.readChar(), -1, null, null);
        }
        else {
            return new Node('\0', -1, readTrie(), readTrie());
        }
    }


    public  void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
