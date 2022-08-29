package com.example.excelreader.DAO.Interfaces;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.IOException;

public interface ExcelFileDAO {
    int getRowCount(XSSFSheet sheet);
    int getColumCount(XSSFSheet sheet);
    Number getNumericValueFromExcel(XSSFSheet sheet, int row, int column);
    String getStringValueFromExcel(XSSFSheet sheet, int row, int column);

    XSSFSheet getSheet(String pathFile) throws IOException;

    boolean checkIfCellIsValid(XSSFSheet sheet, int row, int cell);

    int getFirstValidCell(XSSFSheet sheet, int startingRow, int column);
}
