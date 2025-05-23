// File: handler/TextBasedFileHandler.java
package org.example.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.example.Core.FileHandler;

public class TextBasedFileHandler implements FileHandler {
    private final List<String> supportedExtensions = Arrays.asList(
        ".csv", ".json", ".xml", ".log", ".html", ".java", ".py"
    );

    @Override
    public boolean canHandle(File file) {
        return supportedExtensions.stream().anyMatch(ext -> file.getName().toLowerCase().endsWith(ext));
    }

    @Override
    public void search(File file, String keyword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                if (line.contains(keyword)) {
                    System.out.printf("[text] %s (baris %d): %s\n", file.getName(), lineNum, line.trim());
                }
                lineNum++;
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + file.getName());
        }
    }
}
