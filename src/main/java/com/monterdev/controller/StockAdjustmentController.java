package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.util.StageLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("StockAdjustment.fxml")
@Getter
public class StockAdjustmentController implements Initializable {

    @FXML
    private JFXTextField txtSearchItem;

    @FXML
    private JFXTextField txtQuantity;

    @FXML
    private JFXTextField txtCost;

    @FXML
    private JFXTextField txtInStock;

    @FXML
    private JFXTextField txtLowStock;

    @FXML
    private JFXTextField txtItemName;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnCancel;

    @Autowired
    private ConfigurableApplicationContext applicationContext;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancelOnAction();
    }

    private void btnCancelOnAction() {
        btnCancel.setOnAction(e->{
            Stage currentStage = (Stage) btnCancel.getScene().getWindow();

            new StageLoader().load(MainDashboardController.class, applicationContext,currentStage);
            

        });
    }
}
