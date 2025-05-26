package org.example.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher; // Import Matcher
import java.util.regex.Pattern; // Import Pattern

import org.example.Core.FileHandler;
import org.example.ui.MainApp; // Import MainApp untuk SearchType

public class TextBasedFileHandler implements FileHandler {
    private final List<String> supportedExtensions = Arrays.asList(
            ".csv", ".json", ".xml", ".log", ".html", ".java", ".py"
    );

    // Jika handler ini perlu mengirim output ke UI MainApp, Anda perlu menambahkan konstruktor
    // dan field untuk MainApp, seperti pada handler lainnya.
    // Untuk saat ini, saya akan biarkan outputnya ke System.out, tapi dengan format baru.
    // private MainApp app;
    // public TextBasedFileHandler(MainApp app) {
    //     this.app = app;
    // }

    public TextBasedFileHandler() {
        // Konstruktor default jika tidak perlu instance MainApp (misalnya, output tetap ke console)
    }

    @Override
    public boolean canHandle(File file) {
        return supportedExtensions.stream().anyMatch(ext -> file.getName().toLowerCase().endsWith(ext));
    }

    @Override
    public void search(File file, String keyword, MainApp.SearchType searchType) { // Metode search disesuaikan
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;

            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String highlightedLine = matcher.replaceAll("_$0_");
                    // Output ke System.out dengan format baru
                    // Jika ingin ke UI, gunakan: app.appendResult(...)
                    System.out.printf("Found in %s (Line %d): %s\n",
                            file.getName(), lineNum, highlightedLine.trim());
                }
                lineNum++;
            }
        } catch (IOException e) {
            // Jika ingin ke UI, gunakan: app.appendResult(...)
            System.out.println("Gagal membaca file (TextBased): " + file.getName());
        }
    }
}