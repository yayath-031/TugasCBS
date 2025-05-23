// File: core/FileHandlerRegistry.java
package org.example.Core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHandlerRegistry {
    private final List<FileHandler> handlers = new ArrayList<>();

    public void registerHandler(FileHandler handler) {
        handlers.add(handler);
    }

    public FileHandler getHandler(File file) {
        for (FileHandler handler : handlers) {
            if (handler.canHandle(file)) {
                return handler;
            }
        }
        return null;
    }
}