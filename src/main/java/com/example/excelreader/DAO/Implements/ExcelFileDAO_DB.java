package com.example.excelreader.DAO.Implements;

import com.example.excelreader.DAO.Interfaces.ExcelFileDAO;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class ExcelFileDAO_DB implements ExcelFileDAO {
    @Override
    public int getRowCount(XSSFSheet sheet) {
        //getLastRowNum ritorna il numero di righe partendo da 0, va ridotto di altri due eliminando l'intestazione della tabella
        return (sheet.getLastRowNum()-2);
    }

    @Override
    public int getColumCount(XSSFSheet sheet) {
        return (sheet.getRow(1).getLastCellNum());
    }

    @Override
    public Number getNumericValueFromExcel(XSSFSheet sheet, int row, int column) {
        return sheet.getRow(row).getCell(column).getNumericCellValue();
    }

    @Override
    public String getStringValueFromExcel(XSSFSheet sheet, int row, int column) {
        return sheet.getRow(row).getCell(column).getStringCellValue();
    }

    //Lancia la eccezione nel caso in cui non venga trovato il file. Va gestita dal chiamante
    @Override
    public XSSFSheet getSheet(String pathFile) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(pathFile)) {
            return workbook.getSheet("Foglio1");
        }
    }

    @Override
    public boolean checkIfCellIsValid(XSSFSheet sheet, int row, int cell){
        //Se la riga è nulla lo sarà sicuramente la cella, quindi salta il controllo di essa e ritrona false
        if(sheet.getRow(row)!=null) {
            return sheet.getRow(row).getCell(cell) != null;
        }
        return false;
    }

    @Override
    public int getFirstValidCell(XSSFSheet sheet, int startingRow, int column) {
        while(!checkIfCellIsValid(sheet, startingRow, column))
            startingRow++;

        return startingRow;
    }

}
