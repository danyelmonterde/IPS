package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.util.LoginUtil;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("Login.fxml")
@Getter
public class LoginController implements Initializable {

    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private Label lblVersion;

    @FXML
    private JFXButton btnLogin;

    @FXML
    private JFXButton btnExit;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonLoginOnClick();
        buttonExitOnClick();
    }

    private void buttonExitOnClick() {
        btnExit.setOnAction(e->{
            System.exit(-176);
        });
    }

    private void buttonLoginOnClick() {
        btnLogin.setOnAction(e->{
           boolean isUserAuthenticated = new LoginUtil().isAuthenticated(txtUsername.getText(),txtPassword.getText(),applicationContext);
           if(isUserAuthenticated){
               Prompt.success("Welcome to IQWD Inventory System!");
               Stage primaryStage = (Stage)btnLogin.getScene().getWindow();
               new StageLoader().load(MainDashboardController.class,applicationContext);
               primaryStage.close();
           }else{
               Prompt.failed("Invalid Username or Password! Please try again!");
           }
        });
    }
}
