package com.monterdev.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.monterdev.configuration.GlobalConfiguration.getCalenderYears;
import static com.monterdev.configuration.GlobalConfiguration.getMonthsofCalender;

public class Prompt {

    public static void success(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
        Timeline idlestage = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            //alert.setResult(ButtonType.CANCEL);
            alert.hide();
        }));
        idlestage.setCycleCount(1);
        idlestage.play();
    }

    public static void failed(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.show();
        Timeline idlestage = new Timeline(new KeyFrame(Duration.seconds(2), event -> alert.hide()));
        idlestage.setCycleCount(1);
        idlestage.play();
    }

    public static Optional<ButtonType> confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }

    public static Optional<String> importConfirm(String message) {
        List<String> monthsList = Arrays.asList(getMonthsofCalender().split(","));
        List<String> yearsList = Arrays.asList(getCalenderYears().split(","));
        List<String> combinedYearandMonthsList = new ArrayList<>();
        yearsList.stream().forEach(year->{
            monthsList.stream().forEach(month->{
                combinedYearandMonthsList.add(month+"-"+year);
            });
        });
        ChoiceDialog<String> dialog = new ChoiceDialog<>("***", combinedYearandMonthsList);
        dialog.setTitle("IMPORT ITEM LIST");
        dialog.setHeaderText("Import Items by Period");
        dialog.setContentText("Choose a period when to import the list of items: ");

        Optional<String> result = dialog.showAndWait();
        return result;
    }
}