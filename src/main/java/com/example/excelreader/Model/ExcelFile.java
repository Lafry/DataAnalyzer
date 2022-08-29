package com.example.excelreader.Model;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;

public class ExcelFile {
    private String pathFile;
    List<String> cabinetNames;
    private XSSFSheet sheet;
    private int rowNumber, columnNumber, cabinetNumber;

    public ExcelFile(int rowNumber, int columnNumber, int cabinetNumber, String pathFile, List<String> cabinetNames, XSSFSheet sheet) {
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.cabinetNumber = cabinetNumber;
        this.pathFile = pathFile;
        this.cabinetNames = cabinetNames;
        this.sheet = sheet;
    }

    public XSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(XSSFSheet sheet) {
        this.sheet = sheet;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public int getCabinetNumber() {
        return cabinetNumber;
    }

    public void setCabinetNumber(int cabinetNumber) {
        this.cabinetNumber = cabinetNumber;
    }

    public List<String> getCabinetNames() {
        return cabinetNames;
    }

    public void setCabinetNames(List<String> cabinetNames) {
        this.cabinetNames = cabinetNames;
    }
}
