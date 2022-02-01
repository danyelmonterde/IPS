package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.constants.InventoryTypeConstants;
import com.monterdev.model.Balance;
import com.monterdev.repository.BalanceRepository;
import com.monterdev.repository.CategoryRepository;
import com.monterdev.util.AppTime;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.monterdev.constants.GlobalConfiguration.getCalenderYears;
import static com.monterdev.constants.GlobalConfiguration.getMonthsofCalender;

@Component
@FxmlView("BalanceSettings.fxml")
@Getter
public class BalanceSettingsController implements Initializable {

    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @FXML
    private JFXTextField txtBeginBalance;
    @FXML
    private JFXTextField txtEndBalance;
    @FXML
    private JFXComboBox comboInventoryType;
    @FXML
    private JFXComboBox comboYear;
    @FXML
    private JFXComboBox comboMonth;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXTextField balanceId;


    private static String SELECTED_INVENTORY_TYPE = "";
    private static String SELECTED_MONTH = "";
    private static String SELECTED_YEAR = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        loadYear();
        loadMonths();
        setTextFieldsObservable();
        btnSaveOnAction();
        btnCancelOnAction();
    }

    private void btnCancelOnAction() {
        btnCancel.setOnAction(e->{
            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            new StageLoader().load(MainDashboardController.class, applicationContext,currentStage);

        });

    }

    private void btnSaveOnAction() {
        btnSave.setOnAction(e -> {
            Balance balance = createBalanceObject();
            if(!balanceId.getText().equalsIgnoreCase("0")){
                balance.setId(Integer.parseInt(balanceId.getText()));
                saveUpdateBalance(balance,false);
            }else{
                balance.setId(0);
                saveUpdateBalance(balance,true);
            }
        });
    }

    private void saveUpdateBalance(Balance balance, boolean isNew) {
        Balance savedBalance = balanceRepository.save(balance);
        if(!ObjectUtils.isEmpty(savedBalance)){
            if(isNew){
                Prompt.success("New Balances were saved!");
            }else{
                Prompt.success("Balances Updated!");
            }
        }else{
            Prompt.failed("Failed Updating/Saving Balance!");
        }
        balanceId.setText(String.valueOf(savedBalance.getId()));
    }

    private Balance createBalanceObject() {
        Balance balance = new Balance();
        balance.setBeginbalance(Double.parseDouble(txtBeginBalance.getText()));
        balance.setEndbalance(Double.parseDouble(txtEndBalance.getText()));
        balance.setCategory((String) comboInventoryType.valueProperty().getValue());
        balance.setMonth(SELECTED_MONTH);
        balance.setYear(SELECTED_YEAR);
        balance.setDatecreated(String.valueOf(AppTime.now()));
        return balance;
    }

    private void setTextFieldsObservable() {
        txtBeginBalance.textProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != newValue) {

            }
        });

        txtEndBalance.textProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != newValue) {

            }
        });

        balanceId.textProperty().addListener((obs, oldValue, newValue) -> {

        });
    }

    private void loadCategories() {
        categoryRepository.findAll().forEach(e -> {
            comboInventoryType.getItems().add(e.getCategory_name());
        });
        comboInventoryType.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                SELECTED_INVENTORY_TYPE = (String) newValue;
                setBalances();
            }
        });
    }

    private void loadMonths() {
        List<String> monthList = Arrays.asList(getMonthsofCalender().split(","));
        monthList.stream().forEach(e -> {
            comboMonth.getItems().add(e);
        });
        comboMonth.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                SELECTED_MONTH = (String) newValue;
                setBalances();
            }
        });
    }

    private void loadYear() {
        List<String> yearList = Arrays.asList(getCalenderYears().split(","));
        yearList.stream().forEach(e -> {
            comboYear.getItems().add(e);
        });

        comboYear.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                SELECTED_YEAR = (String) newValue;
                setBalances();
            }
        });
    }


    private void setBalances() {
        txtBeginBalance.setText(String.valueOf(getBalance(
                SELECTED_INVENTORY_TYPE,
                SELECTED_YEAR,
                SELECTED_MONTH
        ).getBeginbalance()));
        txtEndBalance.setText(String.valueOf(getBalance(
                SELECTED_INVENTORY_TYPE,
                SELECTED_YEAR,
                SELECTED_MONTH
        ).getEndbalance()));
    }

    private Balance getBalance(String category, String year, String month) {
        Balance balance = balanceRepository.getBalances(category, month, year);
        if (!ObjectUtils.isEmpty(balance)) {
            balanceId.setText(String.valueOf(balance.getId()));
            return balance;
        } else {
            balanceId.setText(String.valueOf(0));
            return new Balance();
        }
    }
}
