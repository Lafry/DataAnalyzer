package com.example.excelreader;

import javafx.scene.control.Alert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Utils {
    //Posizione del file di configurazione. Come un programma con installer
    private static final String CONFIGURATION_FILE_PATH = "C:\\Program Files\\DataAnalyzer\\config\\serverconfig.config";

    public static String generateExcelPath(String day, String month, int year) {
        //Il path generato trova i file condivisi dal server con indirizzo ip e path indicati nel file di configurazione
        String ipServer = getIPFromConfig();
        String pathServer = getPathFromConfig();
        return("\\\\"+ipServer+pathServer+"\\"+year+"\\"+month+"\\"+day+".xlsx");
    }

    public static StringBuilder convertMomentsInHourAndMinutes(List<Integer> listMoments){
        List<String> listMomentsConvertedInHourAndMinute = new ArrayList<>();
        for (int numberOfMinute : listMoments) {
            listMomentsConvertedInHourAndMinute.add(String.format("%02d", numberOfMinute / 60) + ":" + String.format("%02d", (numberOfMinute % 60)));
        }
        //Funzione che permette di scrivere riga per riga i singoli valori
        StringBuilder text = new StringBuilder();
        for (String s : listMomentsConvertedInHourAndMinute)
            text.append(s).append(" ");

        return text;
    }

    public static String getMonth(String month){
        return switch (month) {
            case "JANUARY" -> "Gennaio";
            case "FEBRUARY" -> "Febbraio";
            case "MARCH" -> "Marzo";
            case "APRIL" -> "Aprile";
            case "MAY" -> "Maggio";
            case "JUNE" -> "Giugno";
            case "JULY" -> "Luglio";
            case "AUGUST" -> "Agosto";
            case "SEPTEMBER" -> "Settembre";
            case "OCTOBER" -> "Ottobre";
            case "NOVEMBER" -> "Novembre";
            case "DECEMBER" -> "Dicembre";
            default -> null;
        };
    }

    public static void displayDialogError(String stringError, boolean closeProgram){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR!");
        alert.setHeaderText(stringError);
        //Se il valore è true, allora premere ok nella dialog di errore comporterà la chiusura del programma
        if(closeProgram){
            alert.setOnCloseRequest(event -> System.exit(0));
        }
        alert.showAndWait();
    }

    public static void displayDialogInformation(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Completato");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static List<String> convertMomentsIntoHourAndMinute(List<Integer> numberList){
        List<String> result = new ArrayList<>();
        for(Integer n : numberList){
            if(n==null)
                result.add("Valore non trovato");
            else
                result.add(String.format("%02d", (n / 60)) + ":" + String.format("%02d", (n % 60)));
        }
        return result;
    }

    public static String getIPFromConfig(){
        Properties p = setupProperties();
        return p.getProperty("ip");
    }

    private static String getPathFromConfig() {
        Properties p = setupProperties();
        return p.getProperty("path");
    }

    public static Properties setupProperties(){
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(CONFIGURATION_FILE_PATH));
        } catch (IOException e) {
            displayDialogError("File di configurazione non trovato, verificare la corretta posizione del file di configurazione", true);
        }
        return properties;

    }

}
