package ru.starstreet.huffman;


public class PrefixTree {
    Byte value;
    PrefixTree right;
    PrefixTree left;

    @Override
    public String toString() {
        return helper(0, this);
    }

    private String helper(int lvl, PrefixTree tree) {
        if (tree == null) return "";
        return "\nlvl: " + lvl + " >>> " + tree.value + " r:" + helper(lvl + 1, tree.right) + "l:" + helper(lvl + 1, tree.left);
    }


}
