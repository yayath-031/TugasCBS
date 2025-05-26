package org.example.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.Core.FileHandler;
import org.example.ui.MainApp;

public class ExcelFileHandler implements FileHandler {
    private final MainApp app;

    public ExcelFileHandler(MainApp app) {
        this.app = app;
    }

    @Override
    public boolean canHandle(File file) {
        return file.getName().toLowerCase().endsWith(".xlsx");
    }

    @Override
    public void search(File file, String keyword, MainApp.SearchType searchType) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getCellType() == CellType.STRING) {
                            String cellValue = cell.getStringCellValue();
                            if (cellValue != null && !cellValue.isEmpty()) {
                                Matcher matcher = pattern.matcher(cellValue);
                                if (matcher.find()) {
                                    // Menggunakan **kata** untuk penanda bold
                                    String highlightedCellValue = matcher.replaceAll("**$0**");
                                    app.appendResult(String.format(
                                        "Found in %s (Sheet '%s', Row %d, Col %d): %s\n",
                                        file.getName(),
                                        sheet.getSheetName(),
                                        row.getRowNum() + 1,
                                        cell.getColumnIndex() + 1,
                                        highlightedCellValue.trim()
                                    ));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            app.appendResult("Gagal baca Excel: " + file.getName() + "\n");
        }
    }
}