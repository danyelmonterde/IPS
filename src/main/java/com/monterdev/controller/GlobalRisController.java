package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.RequisitionIssueSlip;
import com.monterdev.model.ItemSubCategoryHeader;
import com.monterdev.repository.SubCategoryHeaderRepository;
import com.monterdev.util.ControlNumberGenerator;
import com.monterdev.util.Prompt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
@FxmlView("GlobalRisForm.fxml")
@Getter
public class GlobalRisController implements Initializable {

    @FXML
    private AnchorPane risPane;

    @FXML
    private JFXTextField txtFirstName;

    @FXML
    private JFXTextField txtMiddleName;

    @FXML
    private JFXTextField txtLastName;

    @FXML
    private JFXTextField txtControlNumber;

    @FXML
    private JFXTextField txtRisNumber;

    @FXML
    private JFXTextField txtOrNumber;

    @FXML
    private JFXTextField txtIncome;

    @FXML
    private JFXTextField txtCluster;

    @FXML
    private JFXTextField txtBarangay;

    @FXML
    private JFXTextField txtSerialNumber;

    @FXML
    private JFXTextField txtRequestedBy;

    @FXML
    private JFXDatePicker dateTransacted;

    @FXML
    private JFXButton btnCancel;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnReset;

    @FXML
    private JFXComboBox categoryHeader;

    @FXML
    private JFXComboBox isCustomerNew;

    @FXML
    private JFXTextField sales;

    @FXML
    private JFXTextField cost;

    @Autowired
    private SubCategoryHeaderRepository subCategoryHeaderRepository;

    @Autowired
    private RequisitionIssueSlip ris;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtControlNumber.setText(new ControlNumberGenerator().generate());
        Iterable<ItemSubCategoryHeader> subCategoryHeader = subCategoryHeaderRepository.findAll();

        subCategoryHeader.forEach(data ->{
            this.categoryHeader.getItems().add(data.getSub_category_header());
        });

        this.isCustomerNew.getItems().add("YES");
        this.isCustomerNew.getItems().add("NO");

        this.categoryHeader.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                System.out.println(newValue);
            }
        });

        isCustomerNew.valueProperty().addListener((observable, oldValue, newValue)->{
            if(oldValue!=newValue){
                ris.setIs_customer_new(newValue=="YES"?1:0);
            }
        });

        if(!ObjectUtils.isEmpty(ris.getIncome())){
            txtIncome.setText(String.valueOf(ris.getIncome()));
        }

        salesOnChange();
        costOnChange();
        incomeOnChange();
    }




    public void setDateTransacted(javafx.event.ActionEvent actionEvent) {
        ris.setDate_transacted( dtf.format(dateTransacted.getValue()));
    }

    private void resetFields(){
        txtFirstName.setText("");
        txtMiddleName.setText("");
        txtLastName.setText("");
        txtBarangay.setText("");
        txtCluster.setText("");
        txtControlNumber.setText(new ControlNumberGenerator().generate());
        txtRisNumber.setText("");
        txtOrNumber.setText("");
        txtIncome.setText("0.0");
        txtCluster.setText("");
        txtBarangay.setText("");
        txtSerialNumber.setText("");
        txtRequestedBy.setText("");

        ris.setCustomer_first_name("");
        ris.setCustomer_middle_name("");
        ris.setCustomer_last_name("");
        ris.setControl_number("");
        ris.setRis_number("");
        ris.setOr_number("");
        ris.setIncome(0);
        ris.setCluster("");
        ris.setBarangay("");
        ris.setSerial_number("");
        ris.setRequested_by("");

        ris.setDate_transacted( dtf.format(LocalDateTime.now()));
    }
    public void cancel(javafx.event.ActionEvent actionEvent) {
        resetFields();
        Stage stage = (Stage) txtFirstName.getScene().getWindow();
        stage.close();
    }

    public void addRisDetails(ActionEvent actionEvent) {
        if(!ObjectUtils.isEmpty(txtRisNumber.getText())){
            ris.setCustomer_first_name(txtFirstName.getText());
            ris.setCustomer_middle_name(txtMiddleName.getText());
            ris.setCustomer_last_name(txtLastName.getText());
            ris.setControl_number(txtControlNumber.getText());
            ris.setRis_number(txtRisNumber.getText());
            ris.setOr_number(txtOrNumber.getText());
            ris.setIncome(Double.parseDouble(txtIncome.getText()));
            ris.setCluster(txtCluster.getText());
            ris.setBarangay(txtBarangay.getText());
            ris.setSerial_number(txtSerialNumber.getText());
            ris.setRequested_by(txtRequestedBy.getText());
            ris.setCost(Double.parseDouble(cost.getText()));

            double result = computeIncomeValue();

            ris.setIncome(Double.parseDouble(String.format("%.2f", result)));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            ris.setDate_transacted(dateTransacted.getValue().format(dtf));
            Prompt.success("RIS Details updated successfully!");
            Stage stage = (Stage) txtFirstName.getScene().getWindow();
            stage.close();
        }else{
            Prompt.failed("RIS Number is Required!");
        }
    }

    private double computeIncomeValue() {
        double salesValue = Double.parseDouble(sales.getText());
        double costValue = Double.parseDouble(cost.getText());
        double result = salesValue-costValue;
        return result;
    }

    private void salesOnChange(){
        sales.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                ris.setIncome(Double.parseDouble(String.format("%.2f", computeIncomeValue())));
                ris.setSales(Double.parseDouble(newValue));
                txtIncome.setText(String.valueOf(Double.parseDouble(String.format("%.2f", computeIncomeValue()))));
            }
        });
    }

    private double formatDouble(String text){
        return Double.parseDouble(String.format("%.2f", Double.parseDouble(text)));
    }

    private String formatDouble(Double text){
        return String.format("%.2f", text);
    }

    private void costOnChange(){
        cost.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                ris.setIncome(Double.parseDouble(String.format("%.2f", computeIncomeValue())));
                ris.setCost(Double.parseDouble(newValue));
                ris.setSales(formatDouble(sales.getText()));
                txtIncome.setText(String.valueOf(Double.parseDouble(String.format("%.2f", computeIncomeValue()))));
            }
        });
    }

    private void incomeOnChange(){
        txtIncome.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){

            }
        });
    }

    public void resetRis(ActionEvent actionEvent) {
        resetFields();

    }

    public void updateSales(ActionEvent actionEvent) {
    }

    public void updateCategoryHeader(ActionEvent actionEvent) {
    }
}
