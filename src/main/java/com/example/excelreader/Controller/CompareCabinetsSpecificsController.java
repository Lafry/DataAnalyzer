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
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class CompareCabinetsSpecificsController implements Initializable {

    @FXML private Label nomeCabina1Label, nomeCabina2Label, correnteLabelFasciaOrariaSelezionataCabinet1, correnteLabelFasciaOrariaSelezionataCabinet2;
    @FXML private TableView<RecordCabinet> valueFindedInAHourRangeTableCabinet1, valueFindedInAHourRangeTableCabinet2;
    @FXML private TableColumn<RecordCabinet, String> valueToFindInAHourRangeColumnOraCabinet1, valueToFindInAHourRangeColumnCorrenteCabinet1, valueToFindInAHourRangeColumnTensioneCabinet1, valueToFindInAHourRangeColumnEnergiaCabinet1, valueToFindInAHourRangeColumnPotenzaCabinet1, valueToFindInAHourRangeColumnOraCabinet2, valueToFindInAHourRangeColumnCorrenteCabinet2, valueToFindInAHourRangeColumnTensioneCabinet2, valueToFindInAHourRangeColumnEnergiaCabinet2, valueToFindInAHourRangeColumnPotenzaCabinet2;
    @FXML private ComboBox<String> comboBoxOra1, comboBoxMinuto1, comboBoxMinuto2, comboBoxOra2;
    @FXML private Button cercaInUnRangeDiTempoButton;
    @FXML private TextField maxCorrenteTextFieldCabina1, maxTensioneTextFieldCabina1, maxPotenzaTextFieldCabina1,maxCorrenteTextFieldCabina2, maxTensioneTextFieldCabina2, maxPotenzaTextFieldCabina2;
    @FXML private TextField minCorrenteTextFieldCabina1, minTensioneTextFieldCabina1, minPotenzaTextFieldCabina1, minCorrenteTextFieldCabina2, minTensioneTextFieldCabina2, minPotenzaTextFieldCabina2;
    @FXML private TextArea maxCorrenteTextAreaCabina1, maxTensioneTextAreaCabina1, maxPotenzaTextAreaCabina1, maxCorrenteTextAreaCabina2, maxTensioneTextAreaCabina2, maxPotenzaTextAreaCabina2;
    @FXML private TextArea minCorrenteTextAreaCabina1, minTensioneTextAreaCabina1, minPotenzaTextAreaCabina1, minCorrenteTextAreaCabina2, minTensioneTextAreaCabina2, minPotenzaTextAreaCabina2;

    private int comboBoxOra1Index, comboBoxMinute1Index, comboBoxOra2Index, comboBoxMinute2Index;
    private final EnergyController energyController = new EnergyController();
    private final GraphicsController graphicsController = new GraphicsController();
    private final XSSFSheet sheet = SelectCabinetController.getSheet();
    private final XSSFSheet sheet2 = SelectCabinetController.getSheet2();

    private final ExcelFileDAO excelFileDAO = new ExcelFileDAO_DB();

    private final int nCabina1 = SelectCabinetController.getSelectedCabinet(), nCabina2 = SelectCabinetController.getSelectedCabinet2();
    private final String nomeCabina1 = energyController.getCabinetName(SelectCabinetController.getSheet(), nCabina1);
    private final String nomeCabina2 =energyController.getCabinetName(SelectCabinetController.getSheet2(), nCabina2);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nomeCabina1Label.setText(nomeCabina1);
        nomeCabina2Label.setText(nomeCabina2);
        graphicsController.setMaxText(sheet, nCabina1, maxCorrenteTextFieldCabina1, maxTensioneTextFieldCabina1, maxPotenzaTextFieldCabina1, maxCorrenteTextAreaCabina1, maxTensioneTextAreaCabina1, maxPotenzaTextAreaCabina1);
        graphicsController.setMaxText(sheet2, nCabina2, maxCorrenteTextFieldCabina2, maxTensioneTextFieldCabina2, maxPotenzaTextFieldCabina2, maxCorrenteTextAreaCabina2, maxTensioneTextAreaCabina2, maxPotenzaTextAreaCabina2);

        graphicsController.setMinText(sheet, nCabina1, minCorrenteTextFieldCabina1, minTensioneTextFieldCabina1, minPotenzaTextFieldCabina1, minCorrenteTextAreaCabina1, minTensioneTextAreaCabina1, minPotenzaTextAreaCabina1);
        graphicsController.setMinText(sheet2, nCabina2, minCorrenteTextFieldCabina2, minTensioneTextFieldCabina2, minPotenzaTextFieldCabina2, minCorrenteTextAreaCabina2, minTensioneTextAreaCabina2, minPotenzaTextAreaCabina2);
    }

    public void cercaInUnRangeDiTempo() {
        //converto i due orari in minuti assoluti, in modo da poter poi calcolare la differenza di energia
        int moment1 = (comboBoxOra1Index*60)+comboBoxMinute1Index;
        int moment2 = (comboBoxOra2Index*60)+comboBoxMinute2Index;

        //Creo l'elenco dei valori trovati nel range di tempo richiesto
        List<RecordCabinet> recordCabinetListCabinet1 = energyController.getValueOfDataSelected(sheet, nCabina1, comboBoxOra1Index,
                comboBoxMinute1Index, comboBoxOra2Index, comboBoxMinute2Index);
        List<RecordCabinet> recordCabinetListCabinet2 = energyController.getValueOfDataSelected(sheet2, nCabina2, comboBoxOra1Index,
                comboBoxMinute1Index, comboBoxOra2Index, comboBoxMinute2Index);
        //l'intero blocco verifica che l'intervallo di tempo sia coerente, ossia che l'ora di inizio non sia maggiore di quella di fine
        if(moment1>moment2) {
            Utils.displayDialogError("L'ora di inizio del range di tempo è maggiore dell'ora di fine");
        }else {
            graphicsController.populateTableValueToFindInAHourRange(recordCabinetListCabinet1, valueFindedInAHourRangeTableCabinet1, valueToFindInAHourRangeColumnOraCabinet1, valueToFindInAHourRangeColumnEnergiaCabinet1, valueToFindInAHourRangeColumnTensioneCabinet1, valueToFindInAHourRangeColumnCorrenteCabinet1, valueToFindInAHourRangeColumnPotenzaCabinet1);
            graphicsController.populateTableValueToFindInAHourRange(recordCabinetListCabinet2, valueFindedInAHourRangeTableCabinet2, valueToFindInAHourRangeColumnOraCabinet2, valueToFindInAHourRangeColumnEnergiaCabinet2, valueToFindInAHourRangeColumnTensioneCabinet2, valueToFindInAHourRangeColumnCorrenteCabinet2, valueToFindInAHourRangeColumnPotenzaCabinet2);
            correnteLabelFasciaOrariaSelezionataCabinet1.setVisible(true);
            correnteLabelFasciaOrariaSelezionataCabinet2.setVisible(true);
            try{
                correnteLabelFasciaOrariaSelezionataCabinet1.setText("L'energia consumata nella fascia oraria selezionata è: " + String.format(Locale.US, "%.3f", energyController.getDifferences(sheet, nCabina1, excelFileDAO.getFirstValidCell(sheet, moment1, 1), excelFileDAO.getFirstValidCell(sheet, moment2, 1))) + " kWh");
            }catch (NullPointerException e){
                correnteLabelFasciaOrariaSelezionataCabinet1.setText("Non sono registrati valori validi per la fascia oraria scelta");
            }

            try{
                correnteLabelFasciaOrariaSelezionataCabinet2.setText("L'energia consumata nella fascia oraria selezionata è: " + String.format(Locale.US, "%.3f", energyController.getDifferences(sheet2, nCabina2, excelFileDAO.getFirstValidCell(sheet2, moment1, 1), excelFileDAO.getFirstValidCell(sheet2, moment2, 1))) + " kWh");
            }catch (NullPointerException e){
                correnteLabelFasciaOrariaSelezionataCabinet1.setText("Non sono registrati valori validi per la fascia oraria scelta");
            }
        }

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

    public void back(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/SelectCabinetView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setScene(scene);
            appStage.setTitle("Cabinet choice!");
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToGeneral(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CompareCabinetsGeneralView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle(nomeCabina1 + " & " + nomeCabina2);
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToGraph(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CompareCabinetsGraphs.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle("Grafici di " + nomeCabina1 + " & "+ nomeCabina2);
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportValueFindedInAHourRangeTableCabinet1IntoExcel() {
        try{
            String nomeCabina = energyController.getCabinetName(sheet, nCabina1);
            String date = SelectCabinetController.getDateOfDatePicker().toString();
            String from = String.format("%02d", comboBoxOra1Index) + "_" + String.format("%02d", comboBoxMinute1Index);
            String to = String.format("%02d", comboBoxOra2Index) + "_" + String.format("%02d", comboBoxMinute2Index);
            graphicsController.exportTableIntoExcelFile(valueFindedInAHourRangeTableCabinet1, (nomeCabina+"_"+date+"_from_"+from+"_to_"+to));
            Utils.displayDialogInformation("Tabella correttamente esportata. Controlla nella cartella Documenti");
        }catch (IOException e){
            Utils.displayDialogError("Non è stato possibile esportare la tabella");
        }
    }

    public void exportValueFindedInAHourRangeTableCabinet2IntoExcel() {
        try{
            String nomeCabina = energyController.getCabinetName(sheet2, nCabina2);
            String date = SelectCabinetController.getDateOfDatePicker2().toString();
            String from = String.format("%02d", comboBoxOra1Index) + "_" + String.format("%02d", comboBoxMinute1Index);
            String to = String.format("%02d", comboBoxOra2Index) + "_" + String.format("%02d", comboBoxMinute2Index);
            graphicsController.exportTableIntoExcelFile(valueFindedInAHourRangeTableCabinet2, (nomeCabina+"_"+date+"_from_"+from+"_to_"+to));
            Utils.displayDialogInformation("Tabella correttamente esportata. Controlla nella cartella Documenti");
        }catch (IOException e){
            Utils.displayDialogError("Non è stato possibile esportare la tabella");
        }
    }
}
