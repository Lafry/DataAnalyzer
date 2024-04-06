package com.example.excelreader.Controller;

import com.example.excelreader.DAO.Implements.ExcelFileDAO_DB;
import com.example.excelreader.DAO.Interfaces.ExcelFileDAO;
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
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CompareToAnotherCabinetSelectDateController implements Initializable {
    @FXML private Label labelCompareTo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> comboBoxSelectSecondCabinet;
    @FXML private Button goToComparisonButton;
    private final ExcelFileDAO excelFileDAO = new ExcelFileDAO_DB();
    private final EnergyController energyController = new EnergyController();

    public CompareToAnotherCabinetSelectDateController() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                //Disattiva nel date picker le date superiori alla data del giorno corrente
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        labelCompareTo.setText("Compara " + energyController.getCabinetName(SelectCabinetController.getSheet(),SelectCabinetController.getSelectedCabinet()) + " del giorno "+ SelectCabinetController.getDateOfDatePicker());

    }

    public void datePickerClicked(){
        SelectCabinetController.setDateOfDatePicker2(datePicker.getValue());
        comboBoxSelectSecondCabinet.getItems().clear();
        goToComparisonButton.setDisable(true);
        if(Objects.equals(datePicker.getValue(), SelectCabinetController.getDateOfDatePicker())){
            List<String> listCabinets = energyController.getAllCabinetsName(SelectCabinetController.getSheet());
            for (String s: listCabinets) {
                comboBoxSelectSecondCabinet.getItems().add(s);
            }
            comboBoxSelectSecondCabinet.setDisable(false);
        }else{
            String localDay = new DecimalFormat("00").format(datePicker.getValue().getDayOfMonth());
            String localMonth = Utils.getMonth(String.valueOf(datePicker.getValue().getMonth()));
            int year = datePicker.getValue().getYear();
            try{
                XSSFSheet sheet = excelFileDAO.getSheet(Utils.generateExcelPath(localDay, localMonth, year));
                SelectCabinetController.setSheet2(sheet);
                List<String> listCabinets = energyController.getAllCabinetsName(sheet);
                for (String s: listCabinets) {
                    comboBoxSelectSecondCabinet.getItems().add(s);
                }
                comboBoxSelectSecondCabinet.setDisable(false);
            }catch (Exception e){
                Utils.displayDialogError("Non esistono dati per la data selezionata", false);
            }
        }

    }

    public void goToComparisonButtonClicked(ActionEvent actionEvent) {
        if(Objects.equals(energyController.getCabinetName(SelectCabinetController.getSheet(), SelectCabinetController.getSelectedCabinet()), energyController.getCabinetName(SelectCabinetController.getSheet2(), SelectCabinetController.getSelectedCabinet2()))
            && Objects.equals(SelectCabinetController.getDateOfDatePicker(), SelectCabinetController.getDateOfDatePicker2()))
            Utils.displayDialogError("La cabina selezionata è uguale a quella di partenza. Selezionarne un'altra", false);
        else
            openCabinetComparator(actionEvent);
    }

    public void cabinetFromComboBoxSelected() {
        SelectCabinetController.setSelectedCabinet2((comboBoxSelectSecondCabinet.getSelectionModel().getSelectedIndex())+1);
        if(comboBoxSelectSecondCabinet.getSelectionModel().getSelectedIndex()>=0)
            goToComparisonButton.setDisable(false);
    }


    public void back(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/SelectCabinetView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle("Select Cabinet");
            appStage.setResizable(false);
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openCabinetComparator(ActionEvent actionEvent){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CompareCabinetsGeneralView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle(energyController.getCabinetName(SelectCabinetController.getSheet(), SelectCabinetController.getSelectedCabinet()) + " & " + energyController.getCabinetName(SelectCabinetController.getSheet2(), SelectCabinetController.getSelectedCabinet2()));
            appStage.setScene(scene);
            appStage.setMaxHeight(screenBounds.getHeight());
            appStage.setMaxWidth(screenBounds.getWidth());
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
