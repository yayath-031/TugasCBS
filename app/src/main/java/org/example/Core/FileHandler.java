// File: core/FileHandler.java
package org.example.Core;

import java.io.File;
import org.example.ui.MainApp;

public interface FileHandler {
    boolean canHandle(File file);
    // Tambahkan parameter fullLineContent untuk dikirimkan ke UI
    void search(File file, String keyword, MainApp.SearchType searchType, MainApp app); // app instance juga diperlukan
}