// File: handler/WordFileHandler.java
package org.example.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.example.Core.FileHandler;

public class WordFileHandler implements FileHandler {
    @Override
    public boolean canHandle(File file) {
        return file.getName().toLowerCase().endsWith(".docx");
    }

    @Override
    public void search(File file, String keyword) {
        try (FileInputStream fis = new FileInputStream(file)) {
            XWPFDocument doc = new XWPFDocument(fis);
            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText();
                if (text.contains(keyword)) {
                    System.out.printf("[docx] %s: %s\n", file.getName(), text.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + file.getName());
        }
    }
}