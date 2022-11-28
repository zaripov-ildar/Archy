package ru.starstreet.huffman;

import java.util.ArrayList;
import java.util.List;

public class HuffmanTree implements Comparable<HuffmanTree> {
    List<Byte> bytes;
    long frequency;
    HuffmanTree left;
    HuffmanTree right;

    public HuffmanTree() {
        frequency = -1;
    }

    public HuffmanTree(long frequency, List<Byte> bytes) {
        this.frequency = frequency;
        this.bytes = new ArrayList<>();
        this.bytes.addAll(bytes);
    }

    public HuffmanTree(HuffmanTree t1, HuffmanTree t2) {
        this.frequency = t1.frequency + t2.frequency;
        this.bytes = new ArrayList<>();
        this.bytes.addAll(t1.bytes);
        this.bytes.addAll(t2.bytes);
        right = t2;
        left = t1;
    }

    public String getCode(byte b) {
        HuffmanTree root = this;
        return getCode(root, b);
    }

    private String getCode(HuffmanTree root, byte b) {
        if (root.right.bytes.contains(b)) {
            if (root.right.bytes.size() == 1) return "1";
            return "1" + getCode(root.right, b);
        }
        if (root.left.bytes.contains(b)) {
            if (root.left.bytes.size() == 1) return "0";
            return "0" + getCode(root.left, b);
        }
        return "";
    }

    @Override
    public int compareTo(HuffmanTree o) {
        return Long.compare(this.frequency, o.frequency);
    }
}
