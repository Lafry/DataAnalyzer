package com.example.excelreader.Controller;

import com.example.excelreader.DAO.Implements.ExcelFileDAO_DB;
import com.example.excelreader.DAO.Interfaces.ExcelFileDAO;
import com.example.excelreader.DAO.RecordCabinet;
import com.example.excelreader.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class CabinetOverviewController implements Initializable {
    @FXML private NumberAxis yAxis;
    @FXML private CategoryAxis xAxis;
    @FXML private LineChart<String, Number> lineChartCabinet;
    @FXML private TableColumn<RecordCabinet, String> columnOra, columnTensione, columnCorrente, columnEnergia, columnPotenza;
    @FXML private TableView<RecordCabinet> cabinetTable;
    @FXML private TextArea maxCorrenteTextArea, maxTensioneTextArea, minCorrenteTextArea, minTensioneTextArea, maxPotenzaTextArea, minPotenzaTextArea;
    @FXML private TextField minCorrenteTextField, minTensioneTextField, valueToFindTextField, maxCorrenteTextField, maxTensioneTextField, maxPotenzaTextField, minPotenzaTextField;
    @FXML private TextField textfieldMediaCorrente, textfieldMediaTensione, textfieldMediaPotenza, textfieldEnergiaConsumata;
    @FXML private Label correnteLabelFasciaOrariaSelezionata, nomeCabina;
    @FXML private TableView<RecordCabinet> valueToFindTable, valueFindedInAHourRangeTable;
    @FXML private TableColumn<RecordCabinet, String> valueToFindColumnValore, valueToFindColumnOra, valueToFindInAHourRangeColumnOra, valueToFindInAHourRangeColumnCorrente, valueToFindInAHourRangeColumnTensione, valueToFindInAHourRangeColumnEnergia, valueToFindInAHourRangeColumnPotenza;
    @FXML private ComboBox<String> valueForChartComboBox, valueForSearchSelectedComboBox, comboBoxOra1, comboBoxMinuto1, comboBoxMinuto2, comboBoxOra2;
    @FXML private Button cercaInUnRangeDiTempoButton, cercaValoreButton, valueFindedInAHourRangeExportExcelButton;
    private int comboBoxOra1Index, comboBoxMinute1Index, comboBoxOra2Index, comboBoxMinute2Index;
    private final EnergyController energyController = new EnergyController();
    private final GraphicsController graphicsController = new GraphicsController();
    private final ExcelFileDAO excelFileDAO = new ExcelFileDAO_DB();
    private final XSSFSheet sheet = SelectCabinetController.getSheet();
    private final int cabinetSelected = SelectCabinetController.getSelectedCabinet();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setAllText();

        List<RecordCabinet> recordCabinetList = energyController.getValueOfDataSelected(sheet, cabinetSelected, 0, 0, 23, 59);
        graphicsController.populateTableOfTheWholeDay(recordCabinetList, cabinetTable, columnOra, columnEnergia, columnCorrente, columnTensione, columnPotenza);

        startingLineChart(valueForChartComboBox.getSelectionModel().getSelectedItem().toLowerCase());

        //Disabilito il campo di testo finchè non seleziono il tipo di dato richiesto
        valueToFindTextField.setDisable(true);
        //Disabilito il bottone finchè non ho un valore nel campo di testo
        cercaValoreButton.setDisable(true);

        cercaInUnRangeDiTempoButton.setDisable(true);

        valueToFindTextField.textProperty().addListener((observable, oldValue, newValue) -> cercaValoreButton.setDisable(false));
    }

    public void startingLineChart(String value) {
        graphicsController.startLineChart(sheet, null, cabinetSelected, -1, value, yAxis, xAxis);
        graphicsController.populateLineChart(sheet, SelectCabinetController.getDateOfDatePicker(),cabinetSelected, lineChartCabinet, value);
    }

    public void back(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/SelectCabinetView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle("Select Cabinet");
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cercaValore(){
        try {
            //Sostituisco eventuali virgole in input in punti, in modo da rendere compatibile la ricerca
            String str = valueToFindTextField.getText().replaceAll("\\.", ",");
            //Converte il numero preso dalla barra di testo in un double, per fare il paragone
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
            Number number = format.parse(str);
            double value = number.doubleValue();

            //Lista dei momenti in cui è avvenuto il fenomeno
            List<Integer> listMoments = energyController.seeAtWhatTimeTheValueIsLikeTheInput(sheet, SelectCabinetController.getSelectedCabinet(), valueForSearchSelectedComboBox.getValue().toLowerCase(), value);

            if(listMoments.isEmpty()){
                Utils.displayDialogError("Valore non trovato");
            }else{
                //Lista dei momenti convertita in stringhe di ore e minuti
                List<String> listMomentInString = Utils.convertMomentsIntoHourAndMinute(listMoments);
                String valueType = valueForSearchSelectedComboBox.getSelectionModel().getSelectedItem().toLowerCase();
                graphicsController.populateTableValueToFind(valueType, valueToFindTable, valueToFindColumnOra, valueToFindColumnValore, listMomentInString, value);
            }

        }catch (ParseException parseException){
            Utils.displayDialogError("Valore inserito non corretto");
        }
    }

    public void setAllText(){
        nomeCabina.setText(energyController.getCabinetName(sheet, cabinetSelected) + " il giorno " + SelectCabinetController.getDateOfDatePicker());

        //N.B. Non sapendo a priori se il file è completo o meno, ricavo il dato dalla prima riga valida e dall'ultima valida. Essendo righe
        // e non minuti devo sottrarre 3 poiché la funzione getDifferences prende i minuti come argomento
        textfieldEnergiaConsumata.setText(String.format(Locale.US, "%.3f",(energyController.getDifferences(sheet, cabinetSelected,
                (excelFileDAO.getFirstValidCell(sheet, 3, 1)-3), (sheet.getLastRowNum()-3)))));

        textfieldMediaTensione.setText(String.format(Locale.US, "%.3f",(energyController.getMedia(sheet, cabinetSelected, "tensione"))));
        textfieldMediaCorrente.setText(String.format(Locale.US, "%.3f",(energyController.getMedia(sheet, cabinetSelected, "corrente"))));
        textfieldMediaPotenza.setText(String.format(Locale.US, "%.3f",(energyController.getMedia(sheet, cabinetSelected, "potenza"))));

        correnteLabelFasciaOrariaSelezionata.setVisible(false);

        graphicsController.setMaxText(sheet, cabinetSelected, maxCorrenteTextField, maxTensioneTextField, maxPotenzaTextField, maxCorrenteTextArea, maxTensioneTextArea, maxPotenzaTextArea);
        graphicsController.setMinText(sheet, cabinetSelected, minCorrenteTextField, minTensioneTextField, minPotenzaTextField, minCorrenteTextArea, minTensioneTextArea, minPotenzaTextArea);
    }

    public void cercaInUnRangeDiTempo() {
        //converto i due orari in minuti assoluti, in modo da poter poi calcolare la differenza di energia
        int moment1 = (comboBoxOra1Index*60)+comboBoxMinute1Index;
        int moment2 = (comboBoxOra2Index*60)+comboBoxMinute2Index;
        //Creo l'elenco dei valori trovati nel range di tempo richiesto
        List<RecordCabinet> recordCabinetList = energyController.getValueOfDataSelected(sheet, cabinetSelected, comboBoxOra1Index,
                comboBoxMinute1Index, comboBoxOra2Index, comboBoxMinute2Index);

        //l'intero blocco verifica che l'intervallo di tempo sia coerente, ossia che l'ora di inizio non sia maggiore di quella di fine
        if(moment1>moment2) {
            Utils.displayDialogError("L'ora di inizio del range di tempo è maggiore dell'ora di fine");
        }else {
            graphicsController.populateTableValueToFindInAHourRange(recordCabinetList, valueFindedInAHourRangeTable, valueToFindInAHourRangeColumnOra, valueToFindInAHourRangeColumnEnergia, valueToFindInAHourRangeColumnTensione, valueToFindInAHourRangeColumnCorrente, valueToFindInAHourRangeColumnPotenza);
            correnteLabelFasciaOrariaSelezionata.setVisible(true);
            try{
                correnteLabelFasciaOrariaSelezionata.setText("L'energia consumata nella fascia oraria selezionata è: " + String.format(Locale.US, "%.3f", energyController.getDifferences(sheet, cabinetSelected, excelFileDAO.getFirstValidCell(sheet, moment1, 1), excelFileDAO.getFirstValidCell(sheet, moment2, 1))) + " kWh");
            }catch (NullPointerException e){
                correnteLabelFasciaOrariaSelezionata.setText("Non sono registrati valori validi per la fascia oraria scelta");
            }

        }

        valueFindedInAHourRangeExportExcelButton.setDisable(false);
    }

    public void onClickComboBoxOra1() {
        comboBoxMinuto1.setDisable(false);
        comboBoxOra1Index = comboBoxOra1.getSelectionModel().getSelectedIndex();

    }

    public void onClickComboBoxMinuto1() {
        comboBoxMinute1Index = comboBoxMinuto1.getSelectionModel().getSelectedIndex();
        comboBoxOra2.setDisable(false);
    }

    public void onClickComboBoxOra2() {
        comboBoxOra2Index = comboBoxOra2.getSelectionModel().getSelectedIndex();
        comboBoxMinuto2.setDisable(false);
    }

    public void onClickComboBoxMinuto2() {
        comboBoxMinute2Index = comboBoxMinuto2.getSelectionModel().getSelectedIndex();
        cercaInUnRangeDiTempoButton.setDisable(false);
    }

    public void onClickValueForSearchSelectedComboBox() {
        valueToFindTextField.setDisable(false);
        valueToFindTextField.setText("");
        cercaValoreButton.setDisable(true);
        valueToFindTable.getItems().clear();
    }

    public void valueForChartComboBoxChanged() {
        String value = valueForChartComboBox.getSelectionModel().getSelectedItem();
        lineChartCabinet.getData().clear();
        startingLineChart(value.toLowerCase());
    }

    public void refreshButtonClicked() {
        comboBoxOra1.getSelectionModel().clearSelection();
        comboBoxMinuto1.getSelectionModel().clearSelection();
        comboBoxMinuto1.setDisable(true);
        comboBoxOra2.getSelectionModel().clearSelection();
        comboBoxOra2.setDisable(true);
        comboBoxMinuto2.getSelectionModel().clearSelection();
        comboBoxMinuto2.setDisable(true);
        cercaInUnRangeDiTempoButton.setDisable(true);
        correnteLabelFasciaOrariaSelezionata.setVisible(false);
        valueFindedInAHourRangeTable.getItems().clear();
        valueFindedInAHourRangeExportExcelButton.setDisable(true);
    }

    public void exportGeneralTableIntoExcel() {
        try{
            String nomeCabina = energyController.getCabinetName(sheet, cabinetSelected);
            String date = SelectCabinetController.getDateOfDatePicker().toString();
            graphicsController.exportTableIntoExcelFile(cabinetTable, (nomeCabina+"_"+date));
            Utils.displayDialogInformation("Tabella correttamente esportata. Controlla nella cartella Documenti");
        }catch (IOException e){
            Utils.displayDialogError("Non è stato possibile esportare la tabella");
        }
    }

    public void exportValueFindedInAHourRangeTableIntoExcel() {
        try{
            String nomeCabina = energyController.getCabinetName(sheet, cabinetSelected);
            String date = SelectCabinetController.getDateOfDatePicker().toString();
            String from = String.format("%02d", comboBoxOra1Index) + "_" + String.format("%02d", comboBoxMinute1Index);
            String to = String.format("%02d", comboBoxOra2Index) + "_" + String.format("%02d", comboBoxMinute2Index);
            graphicsController.exportTableIntoExcelFile(valueFindedInAHourRangeTable, (nomeCabina+"_"+date+"_from_"+from+"_to_"+to));
            Utils.displayDialogInformation("Tabella correttamente esportata. Controlla nella cartella Documenti");
        }catch (IOException e){
            Utils.displayDialogError("Non è stato possibile esportare la tabella");
        }
    }

    public void goToSelectionOfAnotherCabinet(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CompareToAnotherCabinetSelectDateView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle("Cabinet choice!");
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cercaValoreTextFieldKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            cercaValore();
        }
    }
}
