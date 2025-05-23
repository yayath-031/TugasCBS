// File: handler/TextFileHandler.java
package org.example.Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextFileHandler implements FileHandler {
    @Override
    public boolean canHandle(File file) {
        return file.getName().toLowerCase().endsWith(".txt");
    }

    @Override
    public void search(File file, String keyword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                if (line.contains(keyword)) {
                    System.out.printf("[.txt] %s (baris %d): %s\n", file.getName(), lineNum, line.trim());
                }
                lineNum++;
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + file.getName());
        }
    }
}
