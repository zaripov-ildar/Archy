package ru.starstreet.huffman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Archy implements Terminal {
    public void pack(String[] args) {
        if (args.length == 1) {
            System.err.println("I need a file name to pack");
            help();
        } else {
            String fileName = joinFrom(args);
            pack(fileName);
        }

    }

    private String joinFrom(String[] args) {
        return String.valueOf(args[1]).repeat(args.length - 1);
    }

    private void pack(String fileName) {
        try {
            Path archyFile = Path.of(fileName + ".archy");
            byte[] bytes = Files.readAllBytes(Path.of(fileName));
            bytes = Huffman.pack(bytes);
            write(archyFile, bytes);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            help();
        }
    }

    private void write(Path path, byte[] bytes) throws IOException {
        Files.deleteIfExists(path);
        Files.createFile(path);
        Files.write(path, bytes);
    }

    public void unpack(String[] args) throws IOException {
        String fileName = joinFrom(args);
        if (!fileName.endsWith(".archy")) {
            System.err.println("File extension must be .archy");
            return;
        }
        byte[] bytes = getBytesFromFile(fileName);
        bytes = Huffman.unpack(bytes);
        Path path = Path.of(fileName.replaceAll(".archy$", ""));
        write(path, bytes);
    }

    private byte[] getBytesFromFile(String fileName) throws IOException {
        Path path = Path.of(fileName);
        return Files.readAllBytes(path);
    }


    @Override
    public void version() {
        showTextFile("src/main/resources/version.txt");
    }

    @Override
    public void help() {
        showTextFile("src/main/resources/help.txt");
    }

    private void showTextFile(String name) {
        Path path = Path.of(name);
        try {
            String content = Files.readString(path);
            System.out.println(content);
        } catch (IOException e) {
            System.err.println("Can't read file: " + path);
        }
    }
}
