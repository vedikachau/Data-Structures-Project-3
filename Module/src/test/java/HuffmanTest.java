import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Starter test suite for Project 3 (Huffman Encoding/Decoding).
 * Write more tests!
 * These tests assume the test files are in the same directory as HuffmanTest.java
 */
public class HuffmanTest {

    @Test
    public void testCntFreq() throws IOException {
        int[] freqList = Huffman.cntFreq("file2.txt");
        int[] ansList = {2, 4, 8, 16, 0, 2, 0}; // counts for ascii 97..103
        for (int i = 0; i < ansList.length; i++) {
            assertEquals(ansList[i], freqList[97 + i], "mismatch at ascii " + (97 + i));
        }
    }

    @Test
    public void testComesBefore() {
        HuffmanNode d = new HuffmanNode('d', 5);
        HuffmanNode k = new HuffmanNode('k', 5);
        assertTrue(HuffmanNode.comesBefore(d, k));
        assertFalse(HuffmanNode.comesBefore(k, d));
    }

    @Test
    public void testCreateHeader() throws IOException {
        int[] freqList = Huffman.cntFreq("file2.txt");
        assertEquals("97 2 98 4 99 8 100 16 102 2", Huffman.createHeader(freqList));
    }

    @Test
    public void test01Textfile() throws IOException {
        Huffman.huffmanEncode("file1.txt", "file1_out.txt");
        assertTrue(compareFiles("file1_out.txt", "file1_soln.txt"));
    }

    @Test
    public void combine(){
        HuffmanNode a = new HuffmanNode(67, 23);
        HuffmanNode b = new HuffmanNode(24, 25);
        HuffmanNode c = HuffmanNode.combine(a,b);
        HuffmanNode d = new HuffmanNode(24,48,a,b);
        assertEquals(c,d);
    }
    @Test
    public void addNode() {
        ArrayList<HuffmanNode> list = new ArrayList<>();
        HuffmanNode a = new HuffmanNode(97, 4);
        HuffmanNode b = new HuffmanNode(98, 3);
        HuffmanNode space = new HuffmanNode(32, 3); // ties with b, smaller ASCII
        HuffmanNode d = new HuffmanNode(100, 1);

        Huffman.addNode(list, a);
        Huffman.addNode(list, b);
        Huffman.addNode(list, space);
        Huffman.addNode(list, d);

        assertEquals(4, list.size());
        assertEquals(d, list.get(0));
        assertEquals(space, list.get(1)); // tie broken by ASCII value
        assertEquals(b, list.get(2));
        assertEquals(a, list.get(3));
    }

    @Test
    public void removemin() {
        ArrayList<HuffmanNode> list = new ArrayList<>();
        HuffmanNode a = new HuffmanNode(97, 4);
        HuffmanNode b = new HuffmanNode(98, 3);
        HuffmanNode space = new HuffmanNode(32, 3);
        HuffmanNode d = new HuffmanNode(100, 1);
        Huffman.addNode(list, a);
        Huffman.addNode(list, b);
        Huffman.addNode(list, space);
        Huffman.addNode(list, d);

        assertEquals(d, Huffman.removeMin(list));
        assertEquals(3, list.size()); //node was removed
        assertEquals(space, Huffman.removeMin(list));
        assertEquals(b, Huffman.removeMin(list));
        assertEquals(a, Huffman.removeMin(list));
        assertTrue(list.isEmpty());
    }

    @Test
    public void removeminEmptyCollection() {
        assertNull(Huffman.removeMin(new ArrayList<HuffmanNode>()));
    }
    /**
     * Compares two files line by line, trimming trailing CR/LF so the
     * comparison works the same on Windows and Unix-style line endings.
     */

    private static boolean compareFiles(String file1, String file2) throws IOException {
        try (BufferedReader r1 = new BufferedReader(new FileReader(file1));
             BufferedReader r2 = new BufferedReader(new FileReader(file2))) {
            String line1, line2;
            while (true) {
                line1 = r1.readLine();
                line2 = r2.readLine();
                if (line1 == null && line2 == null) {
                    return true;
                }
                if (line1 == null || line2 == null) {
                    return false;
                }
                if (!line1.trim().equals(line2.trim())) {
                    return false;
                }
            }
        }
    }

    //create header
    @Test
    public void testCreateHeaderEmpty() {
        assertEquals("", Huffman.createHeader(new int[Huffman.NUM_CHARS]));
    }

    //parse header
    @Test
    public void testParseHeader() {
        int[] freq = Huffman.parseHeader("97 3 98 4 99 2");
        assertEquals(Huffman.NUM_CHARS, freq.length);
        assertEquals(3, freq[97]);
        assertEquals(4, freq[98]);
        assertEquals(2, freq[99]);
        assertEquals(0, freq[100]);
    }

    @Test
    public void testParseHeaderEmpty() {
        int[] freq = Huffman.parseHeader("");
        assertEquals(Huffman.NUM_CHARS, freq.length);
        for (int count : freq) {
            assertEquals(0, count);
        }
    }

    //create code
    @Test
    public void testCreateCodeExample() {
        // "abcd abc ab a"
        int[] freq = new int[Huffman.NUM_CHARS];
        freq[32] = 3;
        freq[97] = 4;
        freq[98] = 3;
        freq[99] = 2;
        freq[100] = 1;
        String[] codes = Huffman.createCode(Huffman.createHuffTree(freq));
        assertEquals(Huffman.NUM_CHARS, codes.length);
        assertEquals("00", codes[32]);
        assertEquals("01", codes[98]);
        assertEquals("100", codes[100]);
        assertEquals("101", codes[99]);
        assertEquals("11", codes[97]);
        assertEquals("", codes[120]); //random one that never appears
    }

    @Test
    public void testCreateCodeEmpty() {
        String[] codes = Huffman.createCode(null);
        assertEquals(Huffman.NUM_CHARS, codes.length);
        for (String code : codes) {
            assertEquals("", code);
        }
    }

    //one char
    @Test
    public void testCreateCodeSingleLeaf() {
        String[] codes = Huffman.createCode(new HuffmanNode(97, 5));
        assertEquals("", codes[97]);
    }

    //huffman encode

    //special case: one char
    @Test
    public void testHuffmanEncodeOneChar(@TempDir Path dir) throws IOException {
        Path in = dir.resolve("one.txt");
        Path out = dir.resolve("one_out.txt");
        Files.writeString(in, "aaaaa");
        Huffman.huffmanEncode(in.toString(), out.toString());
        assertEquals("97 5", Files.readString(out)); //header only
    }

    //if empty
    @Test
    public void testHuffmanEncodeEmpty(@TempDir Path dir) throws IOException {
        Path in = dir.resolve("empty.txt");
        Path out = dir.resolve("empty_out.txt");
        Files.writeString(in, "");
        Huffman.huffmanEncode(in.toString(), out.toString());
        assertEquals("", Files.readString(out));
    }

    //if file doesn't exist
    @Test
    public void testHuffmanEncodeMissingFile(@TempDir Path dir) {
        assertThrows(FileNotFoundException.class, () ->
                Huffman.huffmanEncode(dir.resolve("nope.txt").toString(),
                        dir.resolve("out.txt").toString()));
    }

    //normal case
    @Test
    public void testHuffmanEncodeNormal(@TempDir Path dir) throws IOException {
        Path in = dir.resolve("normal.txt");
        Path out = dir.resolve("normal_out.txt");
        Files.writeString(in, "abcd abc ab a");
        Huffman.huffmanEncode(in.toString(), out.toString());
        assertEquals("32 3 97 4 98 3 99 2 100 1\n11011011000011011010011010011",
                Files.readString(out));
    }

    //huffman decode
    @Test
    public void testHuffmanDecodeExample(@TempDir Path dir) throws IOException {
        Path enc = dir.resolve("enc.txt");
        Path dec = dir.resolve("dec.txt");
        Files.writeString(enc, "32 3 97 4 98 3 99 2 100 1\n11011011000011011010011010011");
        Huffman.huffmanDecode(enc.toString(), dec.toString());
        assertEquals("abcd abc ab a", Files.readString(dec));
    }


    //just one line
    @Test
    public void testHuffmanDecodeOneChar(@TempDir Path dir) throws IOException {
        Path enc = dir.resolve("one_enc.txt");
        Path dec = dir.resolve("one_dec.txt");
        Files.writeString(enc, "97 5");
        Huffman.huffmanDecode(enc.toString(), dec.toString());
        assertEquals("aaaaa", Files.readString(dec));
    }

    //if file doesn't exist
    @Test
    public void testHuffmanDecodeEmptyFile(@TempDir Path dir) throws IOException {
        Path enc = dir.resolve("empty_enc.txt");
        Path dec = dir.resolve("empty_dec.txt");
        Files.writeString(enc, "");
        Huffman.huffmanDecode(enc.toString(), dec.toString());
        assertEquals("", Files.readString(dec));
    }

    @Test
    public void testHuffmanDecodeMissingFile(@TempDir Path dir) {
        assertThrows(FileNotFoundException.class, () ->
                Huffman.huffmanDecode(dir.resolve("nope.txt").toString(),
                        dir.resolve("out.txt").toString()));
    }


}
