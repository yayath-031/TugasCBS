// File: core/FileSearcher.java
package org.example.Core;

import java.io.File;
import java.util.Objects;
import org.example.ui.MainApp; // Anda perlu import SearchType dari MainApp

public class FileSearcher {
    private final File folder;
    private final String keyword;
    private final FileHandlerRegistry registry;
    private final MainApp.SearchType searchType; // Tambahkan field untuk SearchType

    // Modifikasi konstruktor
    public FileSearcher(File folder, String keyword, FileHandlerRegistry registry, MainApp.SearchType searchType) {
        this.folder = folder;
        this.keyword = keyword;
        this.registry = registry;
        this.searchType = searchType; // Simpan SearchType
    }

    public void search() {
        if (!folder.isDirectory()) {
            // System.out.println("Path bukan folder."); // Output ini bisa ke MainApp juga jika mau
            MainApp appInstance = findMainAppInstanceInHandlers(); // Cara sederhana mendapatkan instance MainApp
            if (appInstance != null) {
                appInstance.appendResult("Error: Path yang dipilih bukan folder.\n");
            } else {
                System.out.println("Path bukan folder.");
            }
            return;
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                FileHandler handler = registry.getHandler(file);
                if (handler != null) {
                    // Teruskan searchType ke handler
                    handler.search(file, keyword, searchType); 
                } else {
                    // System.out.println("Tidak ada handler untuk: " + file.getName());
                    MainApp appInstance = findMainAppInstanceInHandlers();
                    if (appInstance != null) {
                        appInstance.appendResult("Tidak ada handler yang cocok untuk file: " + file.getName() + "\n");
                    } else {
                        System.out.println("Tidak ada handler untuk: " + file.getName());
                    }
                }
            }
        }
    }

    // Helper sederhana untuk mendapatkan instance MainApp jika handler menyimpannya
    // Ini asumsi, cara yang lebih baik adalah dependency injection jika struktur lebih kompleks
    private MainApp findMainAppInstanceInHandlers() {
        // Ini hanya contoh dan mungkin tidak ideal. Lebih baik MainApp di-pass jika dibutuhkan di sini.
        // Untuk sekarang, kita biarkan output ke System.out atau MainApp jika handler melaporkan error
        return null; 
    }
}