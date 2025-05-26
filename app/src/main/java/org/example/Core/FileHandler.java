// File: core/FileHandler.java
package org.example.Core;

import java.io.File;
import org.example.ui.MainApp; // Import SearchType

public interface FileHandler {
    boolean canHandle(File file);
    // Tambahkan searchType ke signature metode search
    void search(File file, String keyword, MainApp.SearchType searchType);
}