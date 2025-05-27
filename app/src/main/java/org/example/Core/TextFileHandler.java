package org.example.Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.ui.MainApp;

public class TextFileHandler implements FileHandler {
    private final MainApp app; // Tetap simpan instance app

    public TextFileHandler(MainApp app) {
        this.app = app;
    }

    @Override
    public boolean canHandle(File file) {
        return file.getName().toLowerCase().endsWith(".txt");
    }

    @Override
    public void search(File file, String keyword, MainApp.SearchType searchType, MainApp app) { // Ambil app dari parameter
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) { 
                    // Kirim semua data ke MainApp untuk ditampilkan sebagai kartu
                    app.displaySearchResult(
                        file.getName(),
                        file.getAbsolutePath(),
                        "Baris " + lineNum, // Konteks: "Baris N"
                        line.trim(),         // Konten penuh dari baris
                        keyword              // Keyword untuk highlighting di MainApp
                    );
                }
                lineNum++;
            }
        } catch (IOException e) {
            app.appendMessage("Error membaca file: " + file.getName() + "\n");
        }
    }
}