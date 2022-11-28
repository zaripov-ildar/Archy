package ru.starstreet.huffman;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanTest {


    @Test
    void getBit1() {
//        0000 0101
        assertEquals(0, Huffman.getBit((byte) 5, 6));
    }

    @Test
    void getBit2() {
        assertEquals(1, Huffman.getBit((byte) 5, 5));
    }
}