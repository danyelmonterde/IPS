package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.util.LoginUtil;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    @Autowired
    private StageLoader stageLoader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtUsernameOnEnter();
        txtPasswordOnEnter();
        buttonLoginOnClick();
        buttonExitOnClick();
    }

    private void txtUsernameOnEnter() {
        txtUsername.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                login();
            }
        });
    }

    private void txtPasswordOnEnter() {
        txtPassword.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                login();
            }
        });
    }

    private void buttonExitOnClick() {
        btnExit.setOnAction(e->{
            System.exit(-176);
        });
    }

    private void buttonLoginOnClick() {
        btnLogin.setOnAction(e->{
            login();
        });
    }

    private void login() {
        boolean isUserAuthenticated = new LoginUtil().isAuthenticated(txtUsername.getText(),txtPassword.getText(),applicationContext);
        if(isUserAuthenticated){
            Prompt.success("Welcome to IQWD Inventory System!");
            Stage primaryStage = (Stage)btnLogin.getScene().getWindow();
            stageLoader.load(MainDashboardController.class,applicationContext,primaryStage);
        }else{
            Prompt.failed("Invalid Username or Password! Please try again!");
        }
    }
}
