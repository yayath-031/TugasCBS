package org.example.Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void search(File file, String keyword, MainApp.SearchType searchType) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            
            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) { 
                    // Menggunakan **kata** untuk penanda bold
                    String highlightedLine = matcher.replaceAll("**$0**"); 
                    
                    app.appendResult(String.format("Found in %s (Line %d): %s\n",
                        file.getName(), lineNum, highlightedLine.trim()));
                }
                lineNum++;
            }
        } catch (IOException e) {
            app.appendResult("Error membaca file: " + file.getName() + "\n");
        }
    }
}