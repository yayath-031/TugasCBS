package org.example.Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.example.ui.MainApp;

public class TextFileHandler implements FileHandler {
    private final MainApp app;

    public TextFileHandler(MainApp app) {
        this.app = app;
    }

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
                    String result = String.format("[%s] Baris %d: %s\n",
                        file.getName(), lineNum, line.trim());
                    app.appendResult(result);
                }
                lineNum++;
            }
        } catch (IOException e) {
            app.appendResult("Error membaca file: " + file.getName() + "\n");
        }
    }
}