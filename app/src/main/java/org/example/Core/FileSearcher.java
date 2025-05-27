// File: core/FileSearcher.java
package org.example.Core;

import java.io.File;
import java.util.Objects;
import org.example.ui.MainApp;

public class FileSearcher {
    private final File folder;
    private final String keyword;
    private final FileHandlerRegistry registry;
    private final MainApp.SearchType searchType;
    private final MainApp app; // Tambahkan instance MainApp

    // Modifikasi konstruktor untuk menerima MainApp
    public FileSearcher(File folder, String keyword, FileHandlerRegistry registry, MainApp.SearchType searchType, MainApp app) {
        this.folder = folder;
        this.keyword = keyword;
        this.registry = registry;
        this.searchType = searchType;
        this.app = app; // Simpan instance MainApp
    }

    public void search() {
        if (!folder.isDirectory()) {
            app.appendMessage("Error: Path yang dipilih bukan folder.\n"); // Gunakan app.appendMessage
            return;
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                FileHandler handler = registry.getHandler(file);
                if (handler != null) {
                    // Teruskan app instance ke handler
                    handler.search(file, keyword, searchType, app); 
                } else {
                    app.appendMessage("Tidak ada handler yang cocok untuk file: " + file.getName() + "\n"); // Gunakan app.appendMessage
                }
            }
        }
    }
    // Metode findMainAppInstanceInHandlers tidak lagi diperlukan
}