package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.monterdev.model.User;
import com.monterdev.repository.UserRepository;
import com.monterdev.util.LoginUtil;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("UpdatePassword.fxml")
@Getter
public class UpdatePasswordController implements Initializable {

    @FXML
    private JFXPasswordField txtCurrentPassword;

    @FXML
    private JFXPasswordField txtNewPassword;

    @FXML
    private JFXButton btnUpdatePassword;

    @FXML
    private JFXButton btnCancel;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private User user;

    @Autowired
    private LoginUtil loginUtil;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnCancelOnAction();
        btnUpdatePasswordOnAction();
    }

    private void btnUpdatePasswordOnAction() {
        btnUpdatePassword.setOnAction(e -> {
            if (loginUtil.isPasswordEqual(txtCurrentPassword.getText(), user.getPassword())) {
                User userUpdate = new User();
                userUpdate.setId(user.getId());
                userUpdate.setPassword(loginUtil.encryptPassword(txtNewPassword.getText()));
                userUpdate.setIs_super_admin(user.getIs_super_admin());
                userUpdate.setIs_admin(user.getIs_admin());
                userUpdate.setUsername(user.getUsername());

                //udpating below
                User updatedUser = userRepository.save(userUpdate);
                if (!ObjectUtils.isEmpty(updatedUser)) {
                    user.setPassword(updatedUser.getPassword());
                    Prompt.success("Password was successfully updated!");
                    Stage currentStage = (Stage) btnCancel.getScene().getWindow();
                    new StageLoader().load(MainDashboardController.class, applicationContext, currentStage);
                } else {
                    Prompt.failed("Error saving new password!");
                }
            } else {
                Prompt.failed("Current password doesn't match with your account credentials!");
            }
        });
    }

    private void btnCancelOnAction() {
        btnCancel.setOnAction(e -> {
            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            new StageLoader().load(MainDashboardController.class, applicationContext, currentStage);

        });
    }
}
