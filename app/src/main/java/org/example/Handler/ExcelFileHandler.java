package org.example.Handler;

import java.io.File;
import java.io.FileInputStream;

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
    public void search(File file, String keyword) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getCellType() == CellType.STRING) {
                            String value = cell.getStringCellValue();
                            if (value.contains(keyword)) {
                                app.appendResult(String.format(
                                    "[XLSX] %s (Sheet: %s, Row: %d, Col: %d): %s\n",
                                    file.getName(),
                                    sheet.getSheetName(),
                                    row.getRowNum() + 1,
                                    cell.getColumnIndex() + 1,
                                    value.trim()
                                ));
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