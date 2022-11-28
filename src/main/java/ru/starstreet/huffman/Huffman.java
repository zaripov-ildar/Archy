package ru.starstreet.huffman;

import org.json.JSONObject;

import java.util.*;

public class Huffman {
    public static byte[] pack(byte[] bytes) {
        Map<Byte, Integer> frequencyMap = new HashMap<>();
        fillFrequencyMap(frequencyMap, bytes);
        Map<Byte, String> dictionary = new HashMap<>();
        fillDictionary(dictionary, frequencyMap);
        long packedBitsAmount = getPackedBits(frequencyMap, dictionary);
        JSONObject jHead = createHead(dictionary, packedBitsAmount, bytes.length);
        byte[] head = jHead.toString().getBytes();
        int headSize = head.length;
        byte[] preHead = new byte[]{
                (byte) (headSize >>> 24),
                (byte) (headSize >>> 16),
                (byte) (headSize >>> 8),
                (byte) (headSize)
        };
        int wholeBytes = (int) (packedBitsAmount % 8 == 0 ? (packedBitsAmount / 8) : (packedBitsAmount / 8 + 1));
        byte[] body = new byte[wholeBytes];
        translate(body, bytes, dictionary);

        return concat(preHead, head, body);
    }

    private static JSONObject createHead(Map<Byte, String> dictionary, long packedBitsAmount, int size) {
        JSONObject head = new JSONObject();
        head.put("packedSize", packedBitsAmount);
        head.put("size", size);
        JSONObject invertedDictionary = new JSONObject();
        for (Byte aByte : dictionary.keySet()) {
            invertedDictionary.put(dictionary.get(aByte), aByte);
        }
        head.put("dictionary", invertedDictionary);
        return head;
    }

    private static void fillFrequencyMap(Map<Byte, Integer> frequencyMap, byte[] bytes) {
        for (byte aByte : bytes) {
            if (!frequencyMap.containsKey(aByte)) {
                frequencyMap.put(aByte, 0);
            }
            frequencyMap.put(aByte, frequencyMap.get(aByte) + 1);
        }
    }

    private static void fillDictionary(Map<Byte, String> dictionary, Map<Byte, Integer> frequencyMap) {
        HuffmanTree root = createTree(frequencyMap);
        for (Byte key : frequencyMap.keySet()) {
            dictionary.put(key, root.getCode(key));
        }
    }

    private static long getPackedBits(Map<Byte, Integer> frequencyMap, Map<Byte, String> dictionary) {
        long packedBits = 0;
        for (Byte key : frequencyMap.keySet()) {
            packedBits += (long) frequencyMap.get(key) * dictionary.get(key).length();
        }
        return packedBits;
    }

    private static HuffmanTree createTree(Map<Byte, Integer> frequencyMap) {
        List<HuffmanTree> trees = new LinkedList<>();
        HuffmanTree root = new HuffmanTree();
        for (Byte aByte : frequencyMap.keySet()) {
            List<Byte> byteList = new ArrayList<>();
            byteList.add(aByte);
            trees.add(new HuffmanTree(frequencyMap.get(aByte), byteList));
        }
        while (trees.size() > 1) {
            Collections.sort(trees);
            root = new HuffmanTree(trees.get(0), trees.get(1));
            trees.remove(0);
            trees.remove(0);
            trees.add(root);
        }
        return root;
    }


    private static byte[] concat(byte[] preHead, byte[] head, byte[] body) {
        int resultSize = preHead.length + head.length + body.length;
        byte[] result = new byte[resultSize];
        int i = 0;
        //todo turn to system copy array
        for (byte b : preHead) {
            result[i++] = b;
        }
        for (byte b : head) {
            result[i++] = b;
        }
        for (byte b : body) {
            result[i++] = b;
        }
        return result;
    }

    private static void translate(byte[] body, byte[] bytes, Map<Byte, String> dictionary) {
        long position = 0;
        for (byte aByte : bytes) {
            String code = dictionary.get(aByte);
            for (int i = 0; i < code.length(); i++) {
                if (code.charAt(i) == '1') {
                    putBit(body, position);
                }
                position++;
            }
        }
    }

    private static void putBit(byte[] result, long position) {
        int byteNumber = (int) (position / 8);
        byte inBytePosition = (byte) (position % 8);
        result[byteNumber] = setBit(result[byteNumber], inBytePosition);
    }

    private static byte setBit(int b, byte inBytePosition) {
        int mask = 1 << (7 - inBytePosition);
        return (byte) (b | mask);
    }


    /**
     * just divide pack and unpack blocks
     **/


    public static byte[] unpack(byte[] bytes) {
        int headSize = getHeadSize(bytes);

        JSONObject jHead = getHead(headSize, bytes);

        long packedBits = jHead.getLong("packedSize");
        JSONObject jDictionary = jHead.getJSONObject("dictionary");
//        PrefixTree tree = createPrefixTree(jDictionary);
        Map<String, Byte> dic = createDic(jDictionary);
        int size = jHead.getInt("size");
        byte[] result = new byte[size];
//        translateBack(result, bytes, tree, (long) (headSize + 4) * 8, packedBits);
//        todo fix: pointer bigger than packedBits
        long lastBit = (long) (headSize + 4) * 8 + packedBits;
        translateBack(result, bytes, dic, (long) (headSize + 4) * 8, lastBit);
        return result;
    }

    private static void translateBack(byte[] result, byte[] bytes, Map<String, Byte> dic, long pointer, long packedBits) {
        int i = 0;
        String code = "";
        while (pointer < packedBits) {
            code = code + getNextBit(bytes, pointer);
            if (dic.containsKey(code)) {
                result[i++] = dic.get(code);
                code = "";
            }
            pointer++;
        }
    }


    private static Map<String, Byte> createDic(JSONObject jDictionary) {
        Map<String, Byte> result = new HashMap<>();
        for (String s : jDictionary.keySet()) {
            result.put(s, (byte) jDictionary.getInt(s));
        }
        return result;
    }

    private static PrefixTree createPrefixTree(JSONObject jDictionary) {
        PrefixTree root = new PrefixTree();
        PrefixTree current = root;
        for (String s : jDictionary.keySet()) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '1') {
                    if (root.right == null) root.right = new PrefixTree();
                    root = root.right;
                } else {
                    if (root.left == null) root.left = new PrefixTree();
                    root = root.left;
                }
            }
            root.value = (byte) jDictionary.getInt(s);
            root = current;
        }

        return current;
    }

    private static void translateBack(byte[] result, byte[] bytes, PrefixTree root, long pointer, long packedBits) {
        PrefixTree current = root;
        int i = 0;
        while (pointer < packedBits) {
            int bit = getNextBit(bytes, pointer);
//            if (current == null) continue;
            if (bit == 1) {
                current = current.right;
            } else {
                current = current.left;
            }
            if (current.value != null) {
                result[i] = current.value;
                i++;
                current = root;
            }
            pointer++;
        }
    }

    private static int getNextBit(byte[] bytes, long pointer) {
        long wholeBytes = pointer / 8;
        long tail = pointer % 8;
        return getBit(bytes[(int) wholeBytes], tail);
    }

    //    todo make it private
    public static byte getBit(byte aByte, long tail) {
        byte delta = (byte) (7 - tail);
        int res = (aByte >>> delta) & 1;
        return (byte) res;
    }

    private static JSONObject getHead(int headSize, byte[] bytes) {
        byte[] headBytes = new byte[headSize];
        for (int i = 0; i < headSize; i++) {
            headBytes[i] = bytes[i + 4];
        }
//        System.arraycopy(bytes, 4, headBytes, 0, headSize);
        String s = new String(headBytes);
        return new JSONObject(s);
    }

    private static int getHeadSize(byte[] bytes) {
        int headSize = 0;
        for (int i = 0; i < 4; i++) {
            headSize = (headSize << 8) + (bytes[i] & 0xFF);
        }
        return headSize;
    }
}
