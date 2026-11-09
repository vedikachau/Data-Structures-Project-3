import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

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
    public void test01Textfile() throws IOException {
        Huffman.huffmanEncode("file1.txt", "file1_out.txt");
        assertTrue(compareFiles("file1_out.txt", "file1_soln.txt"));
    }

    @Test
    public void testIsLeaf() {
        assertTrue(new HuffmanNode(97, 5).isLeaf());
    }

    @Test
    public void testIsLeafFalse() {
        HuffmanNode n = new HuffmanNode(97, 3,
                new HuffmanNode(97, 1),
                new HuffmanNode(98, 2));
        assertEquals(false, n.isLeaf());
    }

    @Test
    public void testCombineOtherWay() {
        HuffmanNode a = new HuffmanNode(67, 23);
        HuffmanNode b = new HuffmanNode(24, 25);
        HuffmanNode result = HuffmanNode.combine(b, a);
        assertEquals(new HuffmanNode(24, 48, a, b), result);
    }

    @Test
    public void testCombineTieBreak() {
        HuffmanNode space = new HuffmanNode(32, 3);
        HuffmanNode b = new HuffmanNode(98, 3);
        HuffmanNode result = HuffmanNode.combine(b, space);
        assertEquals(new HuffmanNode(32, 6, space, b), result);
    }

    @Test
    public void testCompareLess() {
        HuffmanNode small = new HuffmanNode(97, 1);
        HuffmanNode big = new HuffmanNode(98, 2);
        assertEquals(-1, small.compareTo(big));
    }

    @Test
    public void testCompareGreater() {
        HuffmanNode small = new HuffmanNode(97, 1);
        HuffmanNode big = new HuffmanNode(98, 2);
        assertEquals(1, big.compareTo(small));
    }

    @Test
    public void testCompareEqual() {
        HuffmanNode x = new HuffmanNode(97, 4);
        HuffmanNode y = new HuffmanNode(97, 4);
        assertEquals(0, x.compareTo(y));
    }

    @Test
    public void testCompareToTie() {
        HuffmanNode d = new HuffmanNode('d', 5);
        HuffmanNode k = new HuffmanNode('k', 5);
        assertEquals(-1, d.compareTo(k));
        assertEquals(1, k.compareTo(d));
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

    @Test
    public void testCreateHuffTreeEmpty() {
        assertNull(Huffman.createHuffTree(new int[Huffman.NUM_CHARS]));
    }

    @Test
    public void testCreateHuffTreeUniqueChar() {
        int[] freq = new int[Huffman.NUM_CHARS];
        freq[97] = 5;
        HuffmanNode root = Huffman.createHuffTree(freq);
        assertEquals(new HuffmanNode(97, 5), root);
        assertNull(root.left());
        assertNull(root.right());
    }

    @Test
    public void testCreateHuffTreeTwoChars() {
        int[] freq = new int[Huffman.NUM_CHARS];
        freq[97] = 1;
        freq[98] = 2;
        HuffmanNode expected = new HuffmanNode(97, 3,
                new HuffmanNode(97, 1),
                new HuffmanNode(98, 2));
        assertEquals(expected, Huffman.createHuffTree(freq));
    }

    @Test
    public void testCreateHuffTreeWorkedExample() {
        int[] freq = new int[Huffman.NUM_CHARS];
        freq[32] = 3;
        freq[97] = 4;
        freq[98] = 3;
        freq[99] = 2;
        freq[100] = 1;

        HuffmanNode spaceLeaf = new HuffmanNode(32, 3);
        HuffmanNode bLeaf = new HuffmanNode(98, 3);
        HuffmanNode aLeaf = new HuffmanNode(97, 4);
        HuffmanNode cLeaf = new HuffmanNode(99, 2);
        HuffmanNode dLeaf = new HuffmanNode(100, 1);

        HuffmanNode dcNode = new HuffmanNode(99, 3, dLeaf, cLeaf);          // 3 / 'c'
        HuffmanNode spaceBNode = new HuffmanNode(32, 6, spaceLeaf, bLeaf);  // 6 / ' ' (tie: ' ' before 'b')
        HuffmanNode rightNode = new HuffmanNode(97, 7, dcNode, aLeaf);      // 7 / 'a'
        HuffmanNode expectedRoot = new HuffmanNode(32, 13, spaceBNode, rightNode); // 13 / ' '

        assertEquals(expectedRoot, Huffman.createHuffTree(freq));
    }


    //create header
    @Test
    public void testCreateHeader() throws IOException {
        int[] freqList = Huffman.cntFreq("file2.txt");
        assertEquals("97 2 98 4 99 8 100 16 102 2", Huffman.createHeader(freqList));
    }

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
    public void testHuffmanEncodeOneChar() throws IOException {
        Huffman.huffmanEncode("file3.txt", "file3_out.txt");
        assertTrue(compareFiles("file3_out.txt", "file3_soln.txt"));
    }

    //if empty
    @Test
    public void testHuffmanEncodeEmpty() throws IOException {
        Huffman.huffmanEncode("file4.txt", "file4_out.txt");
        assertTrue(compareFiles("file4_out.txt", "file4_soln.txt"));
    }

    //if file doesn't exist
    @Test
    public void testHuffmanEncodeMissingFile() {
        assertThrows(FileNotFoundException.class, () ->
                Huffman.huffmanEncode("file5.txt",
                        "file5_soln.txt"));
    }

    //normal case
    @Test
    public void testHuffmanEncodeNormal() throws IOException {
        Huffman.huffmanEncode("file2.txt", "file2_soln.txt");
        assertTrue(compareFiles("file2_out.txt", "file2_soln.txt"));
    }

    @Test
    public void testHuffmanEncodeMultipleLines() throws IOException {
        Huffman.huffmanEncode("multiline.txt", "multiline_out.txt");
        assertTrue(compareFiles("multiline_out.txt", "multiline_soln.txt"));
    }

    //huffman decode
    @Test
    public void testHuffmanDecode() throws IOException {
        Huffman.huffmanDecode("file2_soln.txt", "file2.txt");
        assertTrue(compareFiles("file2.txt", "file2_org.txt"));
    }

    @Test
    public void testHuffmanDecodeMultipleLines() throws IOException {
        Huffman.huffmanDecode("multiline_soln.txt", "multiline.txt");
        assertTrue(compareFiles("multiline.txt", "multiline_org.txt"));
    }

    //just one line
    @Test
    public void testHuffmanDecodeOneChar() throws IOException {
        Huffman.huffmanDecode("file3_soln.txt", "file3.txt");
        assertTrue(compareFiles("file3_org.txt", "file3.txt"));
    }

    @Test
    public void testHuffmanDecodeEmptyFile() throws IOException {
        Huffman.huffmanDecode("file4_soln.txt", "file4.txt");
        assertTrue(compareFiles("file4_out.txt", "file4.txt"));
    }

    @Test
    public void testHuffmanDecodeMissingFile() {
        assertThrows(FileNotFoundException.class, () ->
                Huffman.huffmanDecode("file5.txt",
                        "file5_soln.txt"));
    }


}
