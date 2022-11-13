package com.example.excelreader;

import javafx.scene.control.Alert;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String generateExcelPath(String day, String month, int year) {
        //Il path generato trova i file condivisi dal server con indirizzo ip indicato. Essendo statico l'ip non cambierà
        return("\\PATH_FROM_DB\\"+year+"\\"+month+"\\"+day+".xlsx");
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

}
