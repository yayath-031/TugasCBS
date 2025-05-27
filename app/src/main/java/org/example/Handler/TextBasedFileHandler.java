package org.example.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.Core.FileHandler;
import org.example.ui.MainApp; // Pastikan MainApp diimpor

public class TextBasedFileHandler implements FileHandler {
    private final List<String> supportedExtensions = Arrays.asList(
            ".csv", ".json", ".xml", ".log", ".html", ".java", ".py"
    );

    // Handler ini sekarang akan membutuhkan instance MainApp untuk menampilkan hasil di UI
    private final MainApp app;

    public TextBasedFileHandler(MainApp app) {
        this.app = app;
    }

    @Override
    public boolean canHandle(File file) {
        return supportedExtensions.stream().anyMatch(ext -> file.getName().toLowerCase().endsWith(ext));
    }

    @Override
    public void search(File file, String keyword, MainApp.SearchType searchType, MainApp appInstance) {
        // Gunakan appInstance yang dioper dari FileSearcher (yang merupakan this.app)
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;

            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // Mengirim data ke MainApp untuk ditampilkan sebagai kartu
                    // Sama seperti handler lainnya
                    this.app.displaySearchResult( // atau gunakan appInstance jika Anda lebih suka
                        file.getName(),
                        file.getAbsolutePath(),
                        "Baris " + lineNum,      // Konteks
                        line.trim(),             // Konten penuh baris
                        keyword                  // Keyword untuk highlighting
                    );
                }
                lineNum++;
            }
        } catch (IOException e) {
            this.app.appendMessage("Gagal membaca file (TextBased): " + file.getName() + "\n");
        }
    }
}