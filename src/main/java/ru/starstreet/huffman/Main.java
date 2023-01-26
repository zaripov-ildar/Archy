package ru.starstreet.huffman;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Archy archy = new Archy();
        if (args.length == 0) {
            archy.help();
        }
        switch (args[0]) {
            case "-unpack":
            case "-u": {
                archy.unpack(args);
                break;
            }
            case "-pack":
            case "-p": {
                archy.pack(args);
                break;
            }
            case "-help":
            case "-h": {
                archy.help();
                break;
            }
            case "-version":
            case "-v": {
                archy.version();
                break;
            }
            default: {
                System.out.println("Unknown command");
                System.out.println("You can get additional information by using the \"touch --help\" command.");
            }
        }
    }
}
