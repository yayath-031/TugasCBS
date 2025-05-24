package org.example.Handler;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.example.Core.FileHandler;
import org.example.ui.MainApp;

public class WordFileHandler implements FileHandler {
    private final MainApp app;

    public WordFileHandler(MainApp app) {
        this.app = app;
    }

    @Override
    public boolean canHandle(File file) {
        return file.getName().toLowerCase().endsWith(".docx");
    }

    @Override
    public void search(File file, String keyword) {
        try (FileInputStream fis = new FileInputStream(file)) {
            XWPFDocument doc = new XWPFDocument(fis);
            
            // Cari di paragraph biasa
            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText();
                if (text.contains(keyword)) {
                    app.appendResult(String.format("[DOCX] %s: %s\n", 
                        file.getName(), text.trim()));
                }
            }
            
            // Cari di tabel
            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        String text = cell.getText();
                        if (text.contains(keyword)) {
                            app.appendResult(String.format("[DOCX-Tabel] %s: %s\n",
                                file.getName(), text.trim()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            app.appendResult("Gagal baca DOCX: " + file.getName() + "\n");
        }
    }
}