package com.monterdev.util;

import com.monterdev.model.Item;
import com.monterdev.model.RequisitionIssueSlip;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.monterdev.configuration.GlobalConfiguration.getCalenderYears;
import static com.monterdev.configuration.GlobalConfiguration.getMonthsofCalender;

public class Prompt {
    static double totalAmount = 0.0;

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
        yearsList.stream().forEach(year -> {
            monthsList.stream().forEach(month -> {
                combinedYearandMonthsList.add(month + "-" + year);
            });
        });
        ChoiceDialog<String> dialog = new ChoiceDialog<>("***", combinedYearandMonthsList);
        dialog.setTitle("IMPORT ITEM LIST");
        dialog.setHeaderText("Import Items by Period");
        dialog.setContentText("Choose a period when to import the list of items: ");

        Optional<String> result = dialog.showAndWait();
        return result;
    }

    public static ButtonType confirmItemsToRelease(String message, RequisitionIssueSlip requisitionIssueSlip, List<Item> itemCart) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRM ITEM RELEASE");
        alert.setHeaderText(message);
        alert.setContentText("Verify the following items for releasing below: ");

        Label label = new Label("RIS Number: " + requisitionIssueSlip.getControl_number() + "   CUSTOMER TYPE:  " + (requisitionIssueSlip.getIs_customer_new() == 1 ? " NEW " : "EXISTING"));

        StringBuilder exceptionText = new StringBuilder();
        totalAmount = 0.0;

        for (int counter = 0; counter < itemCart.size(); counter++) {
            if (requisitionIssueSlip.getIs_customer_new() == 1) {
                totalAmount = (itemCart.get(counter).getCost() + (itemCart.get(counter).getCost() * .2)) * itemCart.get(counter).getQuantity();
            } else {
                totalAmount = (itemCart.get(counter).getCost()) * itemCart.get(counter).getQuantity();
            }
            exceptionText.append(itemCart.get(counter).getQuantity() + " " + itemCart.get(counter).getUnit() + " - " + itemCart.get(counter).getItem_name() + "        -  AMOUNT :   " + totalAmount + "\n");
        }

        exceptionText.append("\n\n");
        exceptionText.append("GRAND TOTAL:    " + itemCart.stream().mapToDouble(item -> item.computeAmount(item.getQuantity(), item.getCost(), requisitionIssueSlip.getIs_customer_new() == 1 ? true : false)).sum());
        TextArea textArea = new TextArea(exceptionText.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);


        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get();

    }
}