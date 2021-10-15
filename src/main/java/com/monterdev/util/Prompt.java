package com.monterdev.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Prompt {

    public static void success(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
    }

    public static void failed(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.show();
    }

    public static Optional<ButtonType> confirm(String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        Optional<ButtonType> result=alert.showAndWait();
        return result;
    }
}