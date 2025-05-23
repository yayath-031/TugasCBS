// File: core/FileHandler.java
package org.example.Core;

import java.io.File;

public interface FileHandler {
    boolean canHandle(File file);
    void search(File file, String keyword);
}
