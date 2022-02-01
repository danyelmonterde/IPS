package com.monterdev.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

import java.util.Optional;

public class Prompt {

    public static void success(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
        Timeline idlestage = new Timeline( new KeyFrame( Duration.seconds(1 ), new EventHandler<ActionEvent>()
        {

            @Override
            public void handle( ActionEvent event )
            {
                //alert.setResult(ButtonType.CANCEL);
                alert.hide();
            }
        } ) );
        idlestage.setCycleCount( 1 );
        idlestage.play();
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