package org.example.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void search(File file, String keyword, MainApp.SearchType searchType) {
        try (FileInputStream fis = new FileInputStream(file)) {
            XWPFDocument doc = new XWPFDocument(fis);
            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

            // Cari di paragraf biasa
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph para = paragraphs.get(i);
                String paragraphText = para.getText();
                if (paragraphText != null && !paragraphText.isEmpty()) {
                    Matcher matcher = pattern.matcher(paragraphText);
                    if (matcher.find()) {
                        // Menggunakan **kata** untuk penanda bold
                        String contextToDisplay = matcher.replaceAll("**$0**");
                        
                        app.appendResult(String.format("Found in %s (Paragraph %d): %s\n",
                            file.getName(), i + 1, contextToDisplay.trim()));
                    }
                }
            }
            
            // Cari di tabel
            List<XWPFTable> tables = doc.getTables();
            for (int t = 0; t < tables.size(); t++) {
                XWPFTable table = tables.get(t);
                List<XWPFTableRow> rows = table.getRows();
                for (int r = 0; r < rows.size(); r++) {
                    XWPFTableRow row = rows.get(r);
                    List<XWPFTableCell> cells = row.getTableCells();
                    for (int c = 0; c < cells.size(); c++) {
                        XWPFTableCell cell = cells.get(c);
                        String cellText = cell.getText();
                        if (cellText != null && !cellText.isEmpty()) {
                            Matcher matcher = pattern.matcher(cellText);
                            if (matcher.find()) {
                                // Menggunakan **kata** untuk penanda bold
                                String highlightedCellText = matcher.replaceAll("**$0**");
                                app.appendResult(String.format("Found in %s (Table %d, Row %d, Col %d): %s\n",
                                    file.getName(), t + 1, r + 1, c + 1, highlightedCellText.trim()));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            app.appendResult("Gagal baca DOCX: " + file.getName() + "\n");
        }
    }
}