package com.example.excelreader.Controller;

import com.example.excelreader.DAO.Implements.ExcelFileDAO_DB;
import com.example.excelreader.DAO.Interfaces.ExcelFileDAO;
import com.example.excelreader.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class SelectCabinetController implements Initializable {
    private final EnergyController energyController = new EnergyController();
    @FXML private ComboBox<String> cabinetsComboBox, cabinetsComboBox1;
    @FXML private DatePicker datePicker;
    @FXML private Button goButton, resetComboBox2Button, goToTotalConsumeButton;
    private static int selectedCabinet, selectedCabinet2;
    private static XSSFSheet sheet, sheet2;
    private static LocalDate dateOfDatePicker, dateOfDatePicker2;
    private final ExcelFileDAO excelFileDAO = new ExcelFileDAO_DB();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                //Disattiva nel date picker le date superiori alla data del giorno corrente
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0 );
            }
        });

    }
    public void onDatePickerClicked() {
        setDateOfDatePicker(datePicker.getValue());
        String localDay = new DecimalFormat("00").format(dateOfDatePicker.getDayOfMonth());
        String localMonth = Utils.getMonth(String.valueOf(dateOfDatePicker.getMonth()));
        try{
            setSheet(excelFileDAO.getSheet(Utils.generateExcelPath(localDay, localMonth, dateOfDatePicker.getYear())));
            List<String> cabinetsNames = energyController.getAllCabinetsName(SelectCabinetController.getSheet());
            ObservableList<String> data = FXCollections.observableArrayList(cabinetsNames);
            cabinetsComboBox.setItems(data);
            cabinetsComboBox1.setItems(data);
            setSelectedCabinet2(-1);
            SelectCabinetController.setDateOfDatePicker2(datePicker.getValue());
            SelectCabinetController.setSheet2(SelectCabinetController.getSheet());
            cabinetsComboBox.setDisable(false);
            goToTotalConsumeButton.setDisable(false);
        }catch (Exception e){
            Utils.displayDialogError("File non trovato, verificare che il server sia attivo e connesso alla rete, \n o che il file non sia aperto da un altro programma");
        }

    }

    public void back(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/WelcomeView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.getIcons().add(new Image(Objects.requireNonNull(WelcomeController.class.getResourceAsStream("/images/calendarIcon.png"))));
            appStage.setTitle("Hello!");
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cabinetSelected() {
        //poiché gli indici delle combobox partono da zero, per avere il numero di cabina selezionato devo incrementare di uno l'indice
        setSelectedCabinet((cabinetsComboBox.getSelectionModel().getSelectedIndex())+1);
        cabinetsComboBox1.setDisable(false);
        goButton.setDisable(false);
    }

    public void cabinetSelected2() {
        //poiché gli indici delle combobox partono da zero, per avere il numero di cabina selezionato devo incrementare di uno l'indice
        setSelectedCabinet2((cabinetsComboBox1.getSelectionModel().getSelectedIndex())+1);
        resetComboBox2Button.setDisable(false);
    }

    public void goButtonPressed(ActionEvent actionEvent) {
        //una volta premuto il pulsante, se non sono state selezionate due cabine (uno dei due combobox è nullo) allora lancio un'eccezione,
        // gestita mandando l'utente all'overview della singola cabina
        try{
            String cabinetName1 = energyController.getCabinetName(SelectCabinetController.getSheet(), getSelectedCabinet());
            String cabinetName2 = energyController.getCabinetName(SelectCabinetController.getSheet2(), getSelectedCabinet2());
            System.out.println(Objects.equals(SelectCabinetController.getSheet(),SelectCabinetController.getSheet2()));
            //Se le cabine hanno lo stesso nome allora sono uguali, quindi verrà aperta la scheda di overview della cabina
            //Se le cabine sono diverse allora verrà aperta la scheda di comparazione tra le due cabine scelte
            if(Objects.equals(cabinetName1, cabinetName2))
                goToOverviewCabinet(actionEvent);
            else
                goToComparison(actionEvent);

        }catch (IllegalArgumentException illegalArgumentException){
            goToOverviewCabinet(actionEvent);
        }
    }

    public void refreshSecondCombobox() {
        cabinetsComboBox1.getSelectionModel().select(-1);
        setSelectedCabinet2(-1);
    }

    public void goToOverviewCabinet(ActionEvent actionEvent){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CabinetOverviewView.fxml")));
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(root);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            Scene scene = new Scene(scrollPane, 1503, 826);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle(energyController.getCabinetName(getSheet(), getSelectedCabinet()) + " " + getDateOfDatePicker());
            appStage.setScene(scene);
            appStage.setMaxHeight(screenBounds.getHeight());
            appStage.setMaxWidth(screenBounds.getWidth());
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToComparison(ActionEvent actionEvent){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/CompareCabinetsGeneralView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.setTitle(energyController.getCabinetName(SelectCabinetController.getSheet(), getSelectedCabinet()) + " & " + energyController.getCabinetName(SelectCabinetController.getSheet(), getSelectedCabinet2()));
            appStage.setScene(scene);
            appStage.setMaxHeight(screenBounds.getHeight());
            appStage.setMaxWidth(screenBounds.getWidth());
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonTotalConsumeClicked(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/excelreader/TotalConsumeView.fxml")));
            Scene scene = new Scene(root);
            Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            appStage.getIcons().add(new Image(Objects.requireNonNull(SelectCabinetController.class.getResourceAsStream("/images/lightningIcon.png"))));
            appStage.setResizable(false);
            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LocalDate getDateOfDatePicker() {
        return dateOfDatePicker;
    }

    public static LocalDate getDateOfDatePicker2() {
        return dateOfDatePicker2;
    }

    public static void setDateOfDatePicker(LocalDate dateOfDatePicker) {
        SelectCabinetController.dateOfDatePicker = dateOfDatePicker;
    }

    public static void setDateOfDatePicker2(LocalDate dateOfDatePicker2) {
        SelectCabinetController.dateOfDatePicker2 = dateOfDatePicker2;
    }

    public static int getSelectedCabinet() {
        return selectedCabinet;
    }

    public static void setSelectedCabinet(int selectedCabinet) {
        SelectCabinetController.selectedCabinet = selectedCabinet;
    }

    public static int getSelectedCabinet2() {
        return selectedCabinet2;
    }

    public static void setSelectedCabinet2(int selectedCabinet2) {
        SelectCabinetController.selectedCabinet2 = selectedCabinet2;
    }

    public static XSSFSheet getSheet() {
        return sheet;
    }

    public static void setSheet(XSSFSheet sheet) {
        SelectCabinetController.sheet = sheet;
    }

    public static XSSFSheet getSheet2() {
        return sheet2;
    }

    public static void setSheet2(XSSFSheet sheet2) {
        SelectCabinetController.sheet2 = sheet2;
    }

}
