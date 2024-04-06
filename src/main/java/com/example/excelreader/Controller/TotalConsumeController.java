package com.example.excelreader.Controller;

import com.example.excelreader.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class TotalConsumeController implements Initializable {
    @FXML private Button calculateButton;
    @FXML private Label labelMeseScelto, caricamentoLabel;
    @FXML private TextField consumeTextField, yearTextField;
    @FXML private ComboBox<String> monthComboBox;
    private LocalDate localDate;
    private final EnergyController energyController = new EnergyController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        localDate = SelectCabinetController.getDateOfDatePicker();
        labelMeseScelto.setText("Il consumo delle principali cabine dall'inizio di " + Utils.getMonth(localDate.getMonth().toString()) + " al " + localDate.getDayOfMonth() + " è:");
        yearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                yearTextField.setText(newValue.replaceAll("\\D", ""));
            }
        });
        yearTextField.textProperty().addListener((observable, oldValue, newValue) -> calculateButton.setDisable(false));

        //Questo thread si occupa di mostrare all'utente il caricamento
        new Thread(() -> {
            caricamentoLabel.setVisible(true);
        }).start();

        //Ciò assicura che il blocco verrà eseguito solo dopo aver mostrato la schermata
        Platform.runLater(() -> {
            String month = Utils.getMonth(localDate.getMonth().toString());
            double consumoCorrente = energyController.calcolaConsumoMeseCorrente(localDate.getDayOfMonth(), month, localDate.getYear());
            consumeTextField.setVisible(true);
            labelMeseScelto.setVisible(true);
            consumeTextField.setText(String.format(Locale.US, "%.3f", consumoCorrente));
            caricamentoLabel.setVisible(false);
            monthComboBox.setVisible(true);
            yearTextField.setVisible(true);
            calculateButton.setVisible(true);
        });

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

    public void calcolaClicked() {
        caricamentoLabel.setVisible(true);
        Platform.runLater(() -> {
            double consumoCorrente = energyController.calcolaConsumoMeseCorrente(31, monthComboBox.getSelectionModel().getSelectedItem(), Integer.parseInt(yearTextField.getText()));
            if(consumoCorrente==0) {
                Utils.displayDialogError("C'è stato un errore. Ricontrolla i dati inseriti e riprova", false);
                consumeTextField.setText("");
            }else {
                labelMeseScelto.setText("Il consumo delle principali cabine nel mese di " + monthComboBox.getSelectionModel().getSelectedItem() + "  è:");
                consumeTextField.setText(String.format(Locale.US, "%.3f", consumoCorrente));
            }
        });
        caricamentoLabel.setVisible(false);
    }

    public void monthSelected() {
        yearTextField.setDisable(false);
    }

    public void yearTextFieldKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            calcolaClicked();
        }
    }
}
