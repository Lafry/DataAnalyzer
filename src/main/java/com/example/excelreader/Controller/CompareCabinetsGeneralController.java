package com.example.excelreader.Controller;

import com.example.excelreader.DAO.Implements.ExcelFileDAO_DB;
import com.example.excelreader.DAO.Interfaces.ExcelFileDAO;
import com.example.excelreader.DAO.RecordCabinet;
import com.example.excelreader.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CompareCabinetsGeneralController implements Initializable {
    @FXML private TextField textFieldConsumoEnergiaCabina1, textFieldMediaPotenzaCabina1, textFieldMediaTensioneCabina1, textFieldMediaCorrenteCabina1;
    @FXML private TextField textFieldConsumoEnergiaCabina2, textFieldMediaPotenzaCabina2, textFieldMediaTensioneCabina2, textFieldMediaCorrenteCabina2;
    @FXML private Label nomeCabina1Label;
    @FXML private Label nomeCabina2Label;
    @FXML private TableView<RecordCabinet> cabinet1Table, cabinet2Table;
    @FXML private TableColumn<RecordCabinet, String> columnOra1,  columnOra2;
    @FXML private TableColumn<RecordCabinet, String> columnCorrente1, columnTensione1, columnEnergia1, columnPotenza1,columnCorrente2, columnTensione2, columnEnergia2, columnPotenza2;

    private final EnergyController energyController = new EnergyController();
    private final GraphicsController graphicsController = new GraphicsController();
    private final XSSFSheet sheet = SelectCabinetController.getSheet();

    private final XSSFSheet sheet2 = SelectCabinetController.getSheet2();
    private final int cabinet1 = SelectCabinetController.getSelectedCabinet();
    private final int cabinet2 = SelectCabinetController.getSelectedCabinet2();
    private final ExcelFileDAO excelFileDAO = new ExcelFileDAO_DB();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nomeCabina1Label.setText(energyController.getCabinetName(sheet, cabinet1) + " in data: "+ SelectCabinetController.getDateOfDatePicker());
        nomeCabina2Label.setText(energyController.getCabinetName(sheet2, cabinet2) + " in data: "+ SelectCabinetController.getDateOfDatePicker2());

        //Dato che le righe della tabella accettano solo Stringhe, allora andranno preparate dal metodo initializeListString,
        // dopodichè verranno usate nel metodo populateTable per popolare la tabella associata
        List<RecordCabinet> recordCabinetListTable1 = energyController.getValueOfDataSelected(sheet, cabinet1, 0, 0, 23, 59);
        graphicsController.populateTableOfTheWholeDay(recordCabinetListTable1, cabinet1Table, columnOra1, columnEnergia1, columnCorrente1, columnTensione1, columnPotenza1);

        List<RecordCabinet> recordCabinetListTable2 = energyController.getValueOfDataSelected(sheet2, cabinet2, 0, 0, 23, 59);
        graphicsController.populateTableOfTheWholeDay(recordCabinetListTable2, cabinet2Table, columnOra2, columnEnergia2, columnCorrente2, columnTensione2, columnPotenza2);

        setTextFields();
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

    public void goToSpecifics(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CompareCabinetsSpecifics.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle("Specifiche di " + nomeCabina1Label.getText() + " & "+ nomeCabina2Label.getText());
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToGraphs(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CompareCabinetsGraphs.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle("Grafici di " + nomeCabina1Label.getText() + " & "+ nomeCabina2Label.getText());
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportTable1IntoExcel() {
        try{
            String nomeCabina = energyController.getCabinetName(sheet, SelectCabinetController.getSelectedCabinet());
            String date = SelectCabinetController.getDateOfDatePicker().toString();
            graphicsController.exportTableIntoExcelFile(cabinet1Table, (nomeCabina+"_"+date));
            Utils.displayDialogInformation("Tabella correttamente esportata. Controlla nella cartella Documenti");
        }catch (IOException e){
            Utils.displayDialogError("Non è stato possibile esportare la tabella", false);
        }
    }

    public void exportTable2IntoExcel() {
        try{
            String nomeCabina = energyController.getCabinetName(sheet2, SelectCabinetController.getSelectedCabinet2());
            String date = SelectCabinetController.getDateOfDatePicker2().toString();
            graphicsController.exportTableIntoExcelFile(cabinet2Table, (nomeCabina+"_"+date));
            Utils.displayDialogInformation("Tabella correttamente esportata. Controlla nella cartella Documenti");
        }catch (IOException e){
            Utils.displayDialogError("Non è stato possibile esportare la tabella", false);
        }
    }

    public void setTextFields(){
        textFieldConsumoEnergiaCabina1.setText(String.format(Locale.US, "%.3f",(energyController.getDifferences(sheet, cabinet1,
                (excelFileDAO.getFirstValidCell(sheet, 3, 1)-3), (sheet.getLastRowNum()-3)))));
        textFieldMediaCorrenteCabina1.setText(String.format(Locale.US, "%.3f", (energyController.getMedia(sheet, cabinet1, "corrente"))));
        textFieldMediaTensioneCabina1.setText(String.format(Locale.US, "%.3f", (energyController.getMedia(sheet, cabinet1, "tensione"))));
        textFieldMediaPotenzaCabina1.setText(String.format(Locale.US, "%.3f", (energyController.getMedia(sheet, cabinet1, "potenza"))));

        textFieldConsumoEnergiaCabina2.setText(String.format(Locale.US, "%.3f",(energyController.getDifferences(sheet, cabinet2,
                (excelFileDAO.getFirstValidCell(sheet, 3, 1)-3), (sheet.getLastRowNum()-3)))));
        textFieldMediaCorrenteCabina2.setText(String.format(Locale.US, "%.3f", (energyController.getMedia(sheet, cabinet2, "corrente"))));
        textFieldMediaTensioneCabina2.setText(String.format(Locale.US, "%.3f", (energyController.getMedia(sheet, cabinet2, "tensione"))));
        textFieldMediaPotenzaCabina2.setText(String.format(Locale.US, "%.3f", (energyController.getMedia(sheet, cabinet2, "potenza"))));
    }

    public void goToOverviewCabinet1(ActionEvent actionEvent) {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CabinetOverviewView.fxml")));
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(root);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            Scene scene = new Scene(scrollPane, 1503, 826);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle(energyController.getCabinetName(SelectCabinetController.getSheet(), cabinet1) + " " + SelectCabinetController.getDateOfDatePicker());
            appStage.setScene(scene);
            appStage.setMaxHeight(screenBounds.getHeight());
            appStage.setMaxWidth(screenBounds.getWidth());
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToOverviewCabinet2(ActionEvent actionEvent) {
        SelectCabinetController.setSelectedCabinet(cabinet2);
        SelectCabinetController.setDateOfDatePicker(SelectCabinetController.getDateOfDatePicker2());
        SelectCabinetController.setSheet(SelectCabinetController.getSheet2());
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CabinetOverviewView.fxml")));
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(root);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            Scene scene = new Scene(scrollPane, 1503, 826);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle(energyController.getCabinetName(SelectCabinetController.getSheet(), cabinet2) + " " + SelectCabinetController.getDateOfDatePicker());
            appStage.setScene(scene);
            appStage.setMaxHeight(screenBounds.getHeight());
            appStage.setMaxWidth(screenBounds.getWidth());
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
