package com.example.excelreader.Controller;

import com.example.excelreader.DAO.Implements.ExcelFileDAO_DB;
import com.example.excelreader.DAO.Interfaces.ExcelFileDAO;
import com.example.excelreader.DAO.RecordCabinet;
import com.example.excelreader.Utils;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.List;


public class EnergyController {
    private final ExcelFileDAO excelFileDAO = new ExcelFileDAO_DB();

    //La seguente funzione prende il numero della cassetta, una fascia oraria e il valore richiesto, ritorna la lista di valori richiesti manifestati in quella fascia oraria
    //Se non viene selezionato un secondo valore allora hour2 e minute2 verranno impostati dal chiamante come -1
    //Il chiamante dovrà verificare che hour1/minute1 sia minore di hour2/minute2
    public List<RecordCabinet> getValueOfDataSelected(XSSFSheet sheet, int ncassetta, int firstHour, int firstMinute, int secondHour, int secondMinute) {
        java.util.List<RecordCabinet> listRecords = new ArrayList<>();
        int firstRowRequest = (firstHour * 60) + firstMinute + 3; //dato che ogni riga equivale ad un minuto, converto l'ora richiesta in minuti e aggiungo 4, ossia le righe minime per accedere al primo dato
        int lastRowRequest = firstRowRequest;
        int energyRequest = (ncassetta * 4); //restituisce il numero della colonna dell'energia relativa alla cassetta richiesta.
        int tensionRequest = (ncassetta * 4) - 2; //riduco di uno per avere la colonna della tensione relativa alla cassetta richiesta
        int streamRequest = (ncassetta * 4) - 3;  //riduco di due per avere la colonna della corrente relativa alla cassetta richiesta
        int powerRequest = (ncassetta * 4) - 1;  //riduco di due per avere la colonna della corrente relativa alla cassetta richiesta


        //Se il secondo orario è nullo allora sto richiedendo il valore manifestato nel singolo minuto richiesto da hour1:minute1
        if(secondHour!=-1 && secondMinute!=-1)
            lastRowRequest = (secondHour * 60) + secondMinute + 3;

        int differenceBetweenTwoHours = lastRowRequest - firstRowRequest;

        for(int i=0; i<=differenceBetweenTwoHours;i++){
            String orario = String.format("%02d", ((firstRowRequest-3)+i)/60)+":"+String.format("%02d", ((firstRowRequest-3)+i)%60);
            //incremento con i firstRowRequest così che possa trovarmi sempre nella riga successiva ad ogni giro di for
            if ((!excelFileDAO.checkIfCellIsValid(sheet, firstRowRequest+i, energyRequest)) && (!excelFileDAO.checkIfCellIsValid(sheet, firstRowRequest+i, tensionRequest))
                    && (!excelFileDAO.checkIfCellIsValid(sheet, firstRowRequest+i, streamRequest)) && (!excelFileDAO.checkIfCellIsValid(sheet, firstRowRequest+i, powerRequest)))
                listRecords.add(new RecordCabinet(orario, "Nessun valore registrato", "Nessun valore registrato", "Nessun valore registrato", "Nessun valore registrato"));
            else {
                Number valoreCorrente = sheet.getRow(firstRowRequest+i).getCell(streamRequest).getNumericCellValue();
                Number valoreTensione = sheet.getRow(firstRowRequest+i).getCell(tensionRequest).getNumericCellValue();
                Number valoreEnergia = sheet.getRow(firstRowRequest+i).getCell(energyRequest).getNumericCellValue();
                Number valorePotenza = sheet.getRow(firstRowRequest+i).getCell(powerRequest).getNumericCellValue();
                listRecords.add(new RecordCabinet(orario, valoreCorrente.toString(), valoreTensione.toString(), valoreEnergia.toString(), valorePotenza.toString()));
            }
        }
        return listRecords;

    }

    //La differenza può essere fatta solo sull'energia, unico valore sempre in aumento. il chiamante controllerà che minute2 sia maggiore di minute1 e che siano entrambi valori validi
    public double getDifferences(XSSFSheet sheet,int nCabinet, int moment1, int moment2) {
        //N.B. minute1 e minute2 sono i due momenti da confontare. Quando verrà chiamata bisognerà convertire l'ora in minuti prima di passarlo come argometo. Quindi (hour*60)+minute
        int cellRequest = nCabinet*4;
        double dataAtMinute1 = excelFileDAO.getNumericValueFromExcel(sheet, moment1+3, cellRequest).doubleValue();
        double dataAtMinute2 = excelFileDAO.getNumericValueFromExcel(sheet, moment2+3, cellRequest).doubleValue();
        return (dataAtMinute2 - dataAtMinute1);
    }

    public double getMedia(XSSFSheet sheet, int nCabinet, String valueRequested) {
        double max=0;
        int validRow=0;
        int columnNumber = switch (valueRequested) {
            case "corrente" -> (nCabinet * 4) - 3;
            case "tensione" -> (nCabinet * 4) - 2;
            case "potenza" -> (nCabinet * 4) - 1;
            default -> 0;
        };
        for(int i=0;i<excelFileDAO.getRowCount(sheet);i++) {
            if (excelFileDAO.checkIfCellIsValid(sheet, i + 3, columnNumber)){
                max = max + sheet.getRow(i + 3).getCell(columnNumber).getNumericCellValue();
                validRow = validRow + 1;
            }
        }
        return (max/validRow);
    }

    public double getMax(XSSFSheet sheet, int nCabinet, String valueRequested) {
        double max=0;
        int columnNumber = switch (valueRequested) {
            case "energia" -> nCabinet * 4;
            case "corrente" -> (nCabinet * 4) - 3;
            case "tensione" -> (nCabinet * 4) - 2;
            case "potenza" -> (nCabinet * 4) - 1;
            default -> 0;
        };
        for(int i=0;i<excelFileDAO.getRowCount(sheet);i++) {
            if (excelFileDAO.checkIfCellIsValid(sheet, i + 3, columnNumber)){
                double currentValue = sheet.getRow(i + 3).getCell(columnNumber).getNumericCellValue();
                if(currentValue>max)
                    max = currentValue;
            }
        }
        return max;
    }

    public double getMin(XSSFSheet sheet, int nCabinet, String valueRequested) {
        double min;
        int columnNumber = switch (valueRequested) {
            case "energia" -> nCabinet * 4;
            case "corrente" -> (nCabinet * 4) - 3;
            case "tensione" -> (nCabinet * 4) - 2;
            case "potenza" -> (nCabinet * 4) - 1;
            default -> 0;
        };
        int j=3;
        while(!excelFileDAO.checkIfCellIsValid(sheet,j, columnNumber))
            j++;

        min = sheet.getRow(j).getCell(columnNumber).getNumericCellValue();
        for(int i=0;i<excelFileDAO.getRowCount(sheet);i++) {
            if (excelFileDAO.checkIfCellIsValid(sheet, i + 3, columnNumber)){
                double currentValue = sheet.getRow(i + 3).getCell(columnNumber).getNumericCellValue();
                if(currentValue<min)
                    min = currentValue;

            }
        }
        return min;
    }

    //Gli elementi della lista sono da intendere in minuti assoluti. Andranno poi convertiti in ore e minuti relativi dal chiamante
    public List<Integer> seeAtWhatTimeTheValueIsLikeTheInput(XSSFSheet sheet, int nCabinet, String valueRequested, double valueSerched) {
        List<Integer> time = new ArrayList<>();
        int columnNumber = switch (valueRequested) {
            case "energia" -> nCabinet * 4;
            case "corrente" -> (nCabinet * 4) - 3;
            case "tensione" -> (nCabinet * 4) - 2;
            case "potenza" -> (nCabinet * 4) - 1;
            default -> 0;
        };
        for (int i = 0; i < excelFileDAO.getRowCount(sheet); i++) {
            if (excelFileDAO.checkIfCellIsValid(sheet, i + 3, columnNumber)) {
                if ((sheet.getRow(i + 3).getCell(columnNumber).getNumericCellValue()) == valueSerched) {
                    time.add(i);
                }
            }
        }
        return time;
    }

    public String getCabinetName(XSSFSheet sheet, int nCabinet){
        return sheet.getRow(1).getCell((nCabinet * 4) - 3).getStringCellValue();
    }

    public List<String> getAllCabinetsName(XSSFSheet sheet){
        List<String> list = new ArrayList<>();
        for(int i=1; i<=((excelFileDAO.getColumCount(sheet)-1)/4); i++){
            list.add(getCabinetName(sheet, i));
        }
        return list;
    }

    public double calcolaConsumoMeseCorrente(int dayOfMonth, String month, int year) {
        double result=0;
        for(int i=dayOfMonth; i>0; i--){
            String pathFile = Utils.generateExcelPath(String.format("%02d", i), month, year);
            try {
                XSSFSheet currentSheet = excelFileDAO.getSheet(pathFile);
                double energyDayCabinaC1 = getDifferences(currentSheet, 1, excelFileDAO.getFirstValidCell(currentSheet, 3, 1)-3, (currentSheet.getLastRowNum()-3));
                double energyDayCabinaC2Trafo1 = getDifferences(currentSheet, 5, excelFileDAO.getFirstValidCell(currentSheet, 3, 1)-3, (currentSheet.getLastRowNum()-3));
                double energyDayCabinaC2Trafo2 = getDifferences(currentSheet, 11, excelFileDAO.getFirstValidCell(currentSheet, 3, 1)-3, (currentSheet.getLastRowNum()-3));
                double energyDayCabinaC3Trafo1 = getDifferences(currentSheet, 8, excelFileDAO.getFirstValidCell(currentSheet, 3, 1)-3, (currentSheet.getLastRowNum()-3));
                double energyDayCabinaC3Trafo2 = getDifferences(currentSheet, 9, excelFileDAO.getFirstValidCell(currentSheet, 3, 1)-3, (currentSheet.getLastRowNum()-3));
                double energyDayCabinaC4 = getDifferences(currentSheet, 10, excelFileDAO.getFirstValidCell(currentSheet, 3, 1)-3, (currentSheet.getLastRowNum()-3));
                result = result+
                        energyDayCabinaC1+
                        energyDayCabinaC2Trafo1+
                        energyDayCabinaC3Trafo1+
                        energyDayCabinaC4+
                        energyDayCabinaC2Trafo2+
                        energyDayCabinaC3Trafo2;
            } catch (Exception e) {
                i--;
            }
        }
        return result;
    }
}
