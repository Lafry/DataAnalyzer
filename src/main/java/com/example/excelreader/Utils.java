package com.example.excelreader;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    private static final String CONFIGURATION_FILE_PATH = "C:\\Program Files\\DataAnalyzer\\config\\ipconfig.config";

    public static String generateExcelPath(String day, String month, int year) {
        //Il path generato trova i file condivisi dal server con indirizzo ip indicato. Essendo statico l'ip non cambierà
        String ipServer = getIPFromFile();
        return("\\\\"+ipServer+"\\Scada\\Database\\"+year+"\\"+month+"\\"+day+".xlsx");
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

    public static void displayDialogError(String stringError){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR!");
        alert.setHeaderText(stringError);
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

    public static String getIPFromFile() {
        File file = new File(CONFIGURATION_FILE_PATH);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("ip=")) {
                    // Trovato il valore "ip="
                    // Estrai il valore numerico successivo al simbolo "="
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        return parts[1].trim();
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR!");
            alert.setHeaderText("File di configurazione non trovato, verificare la corretta posizione del file di configurazione");
            alert.setOnCloseRequest(event -> System.exit(0));
            alert.showAndWait();
        }
        return null; // Se non viene trovato il valore "ip="
    }

}
