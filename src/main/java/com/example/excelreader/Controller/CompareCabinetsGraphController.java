package com.example.excelreader.Controller;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CompareCabinetsGraphController implements Initializable {
    @FXML private CategoryAxis xAxis;
    @FXML private ComboBox<String> choiceComboBox;
    @FXML private NumberAxis yAxis;
    @FXML private LineChart<String, Number> lineChartBothCabinet;
    private final EnergyController energyController = new EnergyController();
    private final XSSFSheet sheet = SelectCabinetController.getSheet();
    private final XSSFSheet sheet2 = SelectCabinetController.getSheet2();
    private final GraphicsController graphicsController = new GraphicsController();
    private final String nomeCabina1 = energyController.getCabinetName(SelectCabinetController.getSheet(), SelectCabinetController.getSelectedCabinet());
    private final String nomeCabina2 =energyController.getCabinetName(SelectCabinetController.getSheet2(), SelectCabinetController.getSelectedCabinet2());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lineChartBothCabinet.setTitle("Consumo di "+nomeCabina1 + " e " + nomeCabina2);
        startLineChart(choiceComboBox.getSelectionModel().getSelectedItem().toLowerCase());
    }
    private void startLineChart(String value) {
        graphicsController.startLineChart(sheet, sheet2, SelectCabinetController.getSelectedCabinet(), SelectCabinetController.getSelectedCabinet2(), value, yAxis, xAxis);
        graphicsController.populateLineChart(sheet, SelectCabinetController.getDateOfDatePicker(), SelectCabinetController.getSelectedCabinet(), lineChartBothCabinet, value);
        graphicsController.populateLineChart(sheet2,SelectCabinetController.getDateOfDatePicker2(), SelectCabinetController.getSelectedCabinet2(), lineChartBothCabinet, value);
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

    public void goToSpecifics(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CompareCabinetsSpecifics.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle("Specifiche di " + nomeCabina1 + " & " + nomeCabina2);
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectedValue() {
        String value = choiceComboBox.getSelectionModel().getSelectedItem();
        lineChartBothCabinet.getData().clear();
        startLineChart(value.toLowerCase());
    }
}
