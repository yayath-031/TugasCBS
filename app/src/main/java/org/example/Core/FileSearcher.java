// File: core/FileSearcher.java
package org.example.Core;

import java.io.File;
import java.util.Objects;

public class FileSearcher {
    private final File folder;
    private final String keyword;
    private final FileHandlerRegistry registry;

    public FileSearcher(File folder, String keyword, FileHandlerRegistry registry) {
        this.folder = folder;
        this.keyword = keyword;
        this.registry = registry;
    }

    public void search() {
        if (!folder.isDirectory()) {
            System.out.println("Path bukan folder.");
            return;
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                FileHandler handler = registry.getHandler(file);
                if (handler != null) {
                    handler.search(file, keyword);
                } else {
                    System.out.println("Tidak ada handler untuk: " + file.getName());
                }
            }
        }
    }
}
