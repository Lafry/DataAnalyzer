package com.example.excelreader.Controller;

import com.example.excelreader.DAO.RecordCabinet;
import com.example.excelreader.Utils;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GraphicsController {

    private final EnergyController energyController = new EnergyController();

    public GraphicsController() {}

    //popola la tabella passata negli argomenti con le liste di stringhe passate + una calcolata di default (quella delle ore)
    public void populateTableOfTheWholeDay(List<RecordCabinet> recordCabinetList, TableView<RecordCabinet> tableView, TableColumn<RecordCabinet, String> tableColumnOra, TableColumn<RecordCabinet, String> tableColumnEnergia, TableColumn<RecordCabinet, String> tableColumnCorrente, TableColumn<RecordCabinet, String> tableColumnTensione, TableColumn<RecordCabinet, String> tableColumnPotenza){

        tableColumnOra.setCellValueFactory(new PropertyValueFactory<>("ora"));
        tableColumnCorrente.setCellValueFactory(new PropertyValueFactory<>("corrente"));
        tableColumnTensione.setCellValueFactory(new PropertyValueFactory<>("tensione"));
        tableColumnEnergia.setCellValueFactory(new PropertyValueFactory<>("energia"));
        tableColumnPotenza.setCellValueFactory(new PropertyValueFactory<>("potenza"));

        tableView.getItems().setAll(recordCabinetList);
    }

    //popola la tabella passata in input con l'elenco dei momenti in cui il valore è manifesto dato in input e un valore da trovare sempre passato in input
    public void populateTableValueToFind(String valueType, TableView<RecordCabinet> tableView, TableColumn<RecordCabinet, String> tableColumnOra, TableColumn<RecordCabinet, String> tableColumnValue, List<String> oraValues, Double valueToFind){
        //creo una lista di RecordCabinet che verranno inizializzati e utilizzati per popolare la tabella
        List<RecordCabinet> recordCabinetList =new ArrayList<>();
        for (String s : oraValues) {
            RecordCabinet recordCabinet = new RecordCabinet();
            //in base a cosa è stato richiesto dall'utente, creo dei recordCabinets con il solo valore richiesto trovato e l'ora
            switch (valueType) {
                case "corrente" -> recordCabinet = new RecordCabinet(s, valueToFind.toString(), null, null, null);
                case "tensione" -> recordCabinet = new RecordCabinet(s, null, valueToFind.toString(), null, null);
                case "energia" -> recordCabinet = new RecordCabinet(s, null, null, valueToFind.toString(), null);
                case "potenza" -> recordCabinet = new RecordCabinet(s, null, null, null, valueToFind.toString());
            }
            recordCabinetList.add(recordCabinet);
        }

        tableColumnOra.setCellValueFactory(new PropertyValueFactory<>("ora"));
        tableColumnValue.setCellValueFactory(new PropertyValueFactory<>(valueType));

        tableView.getItems().setAll(recordCabinetList);
    }

    //setta i textField e le textArea passate in input con valore massimo
    public void setMaxText(XSSFSheet sheet, int cabinetSelected, TextField textFieldCorrente, TextField textFieldTensione,TextField textFieldPotenza ,TextArea textAreaCorrente, TextArea textAreaTensione, TextArea textAreaPotenza){
        double maxTensioneFloat = energyController.getMax(sheet, cabinetSelected, "tensione");
        double maxCorrenteFloat = energyController.getMax(sheet, cabinetSelected, "corrente");
        double maxPotenzaFloat = energyController.getMax(sheet, cabinetSelected, "potenza");

        List<Integer> hourInNumberCorrente = energyController.seeAtWhatTimeTheValueIsLikeTheInput(sheet, cabinetSelected, "corrente", maxCorrenteFloat);
        List<Integer> hourInNumberTensione = energyController.seeAtWhatTimeTheValueIsLikeTheInput(sheet, cabinetSelected, "tensione", maxTensioneFloat);
        List<Integer> hourInNumberPotenza = energyController.seeAtWhatTimeTheValueIsLikeTheInput(sheet, cabinetSelected, "potenza", maxPotenzaFloat);

        textFieldCorrente.setText(String.format(Locale.US, "%.3f", maxCorrenteFloat));
        textFieldTensione.setText(String.format(Locale.US, "%.3f", maxTensioneFloat));
        textFieldPotenza.setText(String.format(Locale.US, "%.3f", maxPotenzaFloat));

        StringBuilder correnteValues = Utils.convertMomentsInHourAndMinutes(hourInNumberCorrente);
        StringBuilder tensioneValues = Utils.convertMomentsInHourAndMinutes(hourInNumberTensione);
        StringBuilder potenzaValues = Utils.convertMomentsInHourAndMinutes(hourInNumberPotenza);

        textAreaCorrente.setText(correnteValues.toString());
        textAreaTensione.setText(tensioneValues.toString());
        textAreaPotenza.setText(potenzaValues.toString());
    }

    //setta i textField e le textArea passate in input con valore minimo
    public void setMinText(XSSFSheet sheet, int cabinetSelected, TextField textFieldCorrente, TextField textFieldTensione, TextField textFieldPotenza, TextArea textAreaCorrente, TextArea textAreaTensione, TextArea textAreaPotenza){
        double minTensioneFloat = energyController.getMin(sheet, cabinetSelected, "tensione");
        double minCorrenteFloat = energyController.getMin(sheet, cabinetSelected, "corrente");
        double minPotenzaFloat = energyController.getMin(sheet, cabinetSelected, "potenza");


        List<Integer> hourInNumberCorrente = energyController.seeAtWhatTimeTheValueIsLikeTheInput(sheet, cabinetSelected, "corrente", minCorrenteFloat);
        List<Integer> hourInNumberTensione = energyController.seeAtWhatTimeTheValueIsLikeTheInput(sheet, cabinetSelected, "tensione", minTensioneFloat);
        List<Integer> hourInNumberPotenza = energyController.seeAtWhatTimeTheValueIsLikeTheInput(sheet, cabinetSelected, "potenza", minPotenzaFloat);

        textFieldCorrente.setText(String.format(Locale.US, "%.3f", minCorrenteFloat));
        textFieldTensione.setText(String.format(Locale.US, "%.3f", minTensioneFloat));
        textFieldPotenza.setText(String.format(Locale.US, "%.3f", minPotenzaFloat));

        StringBuilder correnteValues = Utils.convertMomentsInHourAndMinutes(hourInNumberCorrente);
        StringBuilder tensioneValues = Utils.convertMomentsInHourAndMinutes(hourInNumberTensione);
        StringBuilder potenzaValues = Utils.convertMomentsInHourAndMinutes(hourInNumberPotenza);


        textAreaCorrente.setText(correnteValues.toString());
        textAreaTensione.setText(tensioneValues.toString());
        textAreaPotenza.setText(potenzaValues.toString());
    }

    //Inizializza il grafico passato in input.
    public void startLineChart(XSSFSheet sheet, XSSFSheet sheet2, int nCabinet1, int nCabinet2, String value, NumberAxis yAxis, CategoryAxis xAxis) {
        double minCabina1 = energyController.getMin(sheet, nCabinet1, value);
        double maxCabina1 = energyController.getMax(sheet, nCabinet1, value);

        yAxis.setAutoRanging(false);
        xAxis.setLabel("Ora");

        switch (value) {
            case "energia" -> yAxis.setLabel("Energia (gWh)");
            case "tensione" -> yAxis.setLabel("Tensione (V)");
            case "corrente" -> yAxis.setLabel("Corrente (A)");
            case "potenza" -> yAxis.setLabel("Potenza (kW)");

        }
        //se la cabina 2 è -1 allora il grafico va popolato con una sola cabina, quindi regolo il range di analisi sui soli valori di cabina 1
        if(nCabinet2 == -1){
            if(Objects.equals(value, "energia")){
                //Se il valore è Energia converto i kWh in GWh per rendere il grafico più leggibile
                yAxis.setLowerBound(minCabina1/1000000);
                yAxis.setUpperBound(maxCabina1/1000000);
                yAxis.setTickUnit(0.001);
            }else{
                yAxis.setLowerBound(minCabina1);
                yAxis.setUpperBound(maxCabina1);
                yAxis.setTickUnit(1);
            }
        }else{
            //Se abbiamo un grafico da due cabine vorrà dire che non avremo mai il valore "enegia", mostrato sempre in due grafici diversi
            double minCabina2 = energyController.getMin(sheet2, nCabinet2, value);
            double maxCabina2 = energyController.getMax(sheet2, nCabinet2, value);
            yAxis.setLowerBound(Math.min(minCabina1, minCabina2));
            yAxis.setUpperBound(Math.max(maxCabina1, maxCabina2));
            yAxis.setTickUnit(1);
        }
    }

    //popola il grafico precedentemente inizializzato passato in input
    public void populateLineChart(XSSFSheet sheet, LocalDate localDate, int nCabinet, LineChart<String, Number> lineChart, String value){
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(value +" di " + energyController.getCabinetName(sheet, nCabinet) + " in data " + localDate);
        List<RecordCabinet> recordCabinetList = energyController.getValueOfDataSelected(sheet, nCabinet, 0, 0, 23, 59);
        for(int i=0; i<recordCabinetList.size(); i++){
            if(i%60==0)
                if(!Objects.equals(recordCabinetList.get(i).getEnergia(), "Nessun valore registrato")) {
                    switch (value) {
                        case "energia" ->
                                series.getData().add(new XYChart.Data<>(String.format("%02d", i / 60), Double.parseDouble(recordCabinetList.get(i).getEnergia()) / 1000000));
                        case "tensione" ->
                                series.getData().add(new XYChart.Data<>(String.format("%02d", i / 60), Double.parseDouble(recordCabinetList.get(i).getTensione())));
                        case "corrente" ->
                                series.getData().add(new XYChart.Data<>(String.format("%02d", i / 60), Double.parseDouble(recordCabinetList.get(i).getCorrente())));
                        case "potenza" ->
                                series.getData().add(new XYChart.Data<>(String.format("%02d", i / 60), Double.parseDouble(recordCabinetList.get(i).getPotenza())));

                    }
                }else
                    series.getData().add(new XYChart.Data<>(String.format("%02d", i/60), null));

        }
        lineChart.getData().add(series);
    }

    public void populateTableValueToFindInAHourRange(List<RecordCabinet> recordCabinetList, TableView<RecordCabinet> tableView, TableColumn<RecordCabinet, String> tableColumnOra, TableColumn<RecordCabinet, String> tableColumnEnergia, TableColumn<RecordCabinet, String> tableColumnTensione, TableColumn<RecordCabinet, String> tableColumnCorrente, TableColumn<RecordCabinet, String> tableColumnPotenza) {
        tableColumnOra.setCellValueFactory(new PropertyValueFactory<>("ora"));
        tableColumnCorrente.setCellValueFactory(new PropertyValueFactory<>("corrente"));
        tableColumnTensione.setCellValueFactory(new PropertyValueFactory<>("tensione"));
        tableColumnPotenza.setCellValueFactory(new PropertyValueFactory<>("potenza"));
        tableColumnEnergia.setCellValueFactory(new PropertyValueFactory<>("energia"));


        tableView.getItems().setAll(recordCabinetList);
    }

    public void exportTableIntoExcelFile(TableView<RecordCabinet> tableView, String nameCabinet) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet spreadsheet = workbook.createSheet("Foglio1");

        Row row = spreadsheet.createRow(0);

        for (int j = 0; j < tableView.getColumns().size(); j++) {
            row.createCell(j).setCellValue(tableView.getColumns().get(j).getText());
            spreadsheet.autoSizeColumn(j);
        }

        for (int i = 0; i < tableView.getItems().size(); i++) {
            row = spreadsheet.createRow(i + 1);
            row.createCell(0).setCellValue(tableView.getColumns().get(0).getCellData(i).toString());
            for (int j = 1; j < tableView.getColumns().size(); j++) {
                if(!Objects.equals(tableView.getColumns().get(j).getCellData(i).toString(), "Nessun valore registrato")) {
                    row.createCell(j).setCellValue(Double.parseDouble(tableView.getColumns().get(j).getCellData(i).toString()));
                }
                else {
                    row.createCell(j).setCellValue("");
                }
            }
        }
        spreadsheet.setColumnWidth(0, ((7 * 256) + 200));
        spreadsheet.setColumnWidth(1, ((13 * 256) + 200));
        spreadsheet.setColumnWidth(2, ((13 * 256) + 200));
        spreadsheet.setColumnWidth(3, ((13 * 256) + 200));
        spreadsheet.setColumnWidth(4, ((13 * 256) + 200));

        //Creo il percorso che porterà alla cartella che conterrà il file esportato
        Path path = Path.of(System.getProperty("user.home") + "\\Documents\\DatiCabineExcel");

        //Controllo se la cartella esiste, se non esiste allora la creo
        if(!Files.exists(path))
            Files.createDirectories(path);

        //Salvo il file nella cartella creata aggiungendo al path il nome del file con l'estensione
        FileOutputStream fileOut = new FileOutputStream( path + "\\" + nameCabinet+".xls");
        workbook.write(fileOut);
        fileOut.close();
    }

}
