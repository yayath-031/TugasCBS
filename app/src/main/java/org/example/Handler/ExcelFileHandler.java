package org.example.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.Core.FileHandler;

public class ExcelFileHandler implements FileHandler {
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
                            String cellValue = cell.getStringCellValue();
                            if (cellValue.contains(keyword)) {
                                System.out.printf("[xlsx] %s (%s!R%dC%d): %s\n",
                                        file.getName(),
                                        sheet.getSheetName(),
                                        row.getRowNum() + 1,
                                        cell.getColumnIndex() + 1,
                                        cellValue.trim());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + file.getName());
        }
    }
}