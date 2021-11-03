package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.Customer;
import com.monterdev.model.RequisitionIssueSlip;
import com.monterdev.model.SelectedRisTemplate;
import javafx.event.ActionEvent;
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
import java.util.Arrays;
import java.util.ResourceBundle;

import static com.monterdev.constants.GlobalConfiguration.*;

@Component
@FxmlView("CustomizeRis.fxml")
@Getter
public class CustomizeRequisitionIssueSlipController implements Initializable {

    @FXML
    private Label currentDate;

    @FXML
    private JFXTextField firstName;

    @FXML
    private JFXTextField middleName;

    @FXML
    private JFXTextField lastName;

    @FXML
    private JFXComboBox customerType;

    @FXML
    private JFXComboBox comboRisTemplate;

    @FXML
    private JFXButton btnRisTemplate;

    @FXML
    private JFXButton btnAddRIS;

    @FXML
    private JFXButton btnCancel;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private Customer customer;

    @Autowired
    private RequisitionIssueSlip requisitionIssueSlip;

    @Autowired
    private SelectedRisTemplate selectedRisTemplate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetFields();
        customerTypeOnload();
        comboRisTemplateOnload();
        setFirstName();
        setMiddleName();
        setLastName();
    }

    public void customerTypeOnload() {
        String [] customerTypes = getCustomerTypes().split(",");
        Arrays.asList(customerTypes).stream().forEach(d ->this.customerType.getItems().add(d));
    }

    public void comboRisTemplateOnload() {
        String [] risTemplates = getRisTemplates().split(",");
        Arrays.asList(risTemplates).stream().forEach(d -> this.comboRisTemplate.getItems().add(d));
    }

    public void setFirstName() {
        firstName.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                customer.setFirst_name(newValue);
            }
        });
    }

    public void setMiddleName() {
        middleName.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                customer.setMiddle_name(newValue);
            }
        });
    }

    public void setLastName() {
        lastName.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                customer.setLast_name(newValue);
            }
        });
    }

    public void createRISTemplate(ActionEvent actionEvent) {
        cancel(null);
    }

    public void addRIS(ActionEvent actionEvent) {
        requisitionIssueSlip.setCustomer_name(customer.getFirst_name()+" "+customer.getMiddle_name()+" "+customer.getLast_name());
        customer.setFirst_name(customer.getFirst_name());
        customer.setMiddle_name(customer.getMiddle_name());
        customer.setLast_name((customer.getLast_name()));
        selectedRisTemplate.setSelectedRisTemplate(this.comboRisTemplate.getValue().toString().intern());
        Stage stage = (Stage) btnRisTemplate.getScene().getWindow();
        stage.close();
    }

    public void cancel(ActionEvent actionEvent) {
        resetFields();
        Stage stage = (Stage) btnRisTemplate.getScene().getWindow();
        stage.close();
    }

    public void resetFields(){
        firstName.setText("");
        middleName.setText("");
        lastName.setText("");
        selectedRisTemplate.setSelectedRisTemplate("");
        requisitionIssueSlip.setCustomer_name("");
        requisitionIssueSlip.setIs_customer_new(0);
    }

    public void selectCustomerType(ActionEvent actionEvent) {
        //Assumming OLD customer is setup first on the JSON Configuration "CUSTOMER_TYPES" , 0=old ,1=new
        String oldCustomer = getConfigValue(customerTypes).split(",")[0];
        requisitionIssueSlip.setIs_customer_new(this.customerType.getValue().toString().equalsIgnoreCase(oldCustomer)?0:1 );
    }

    public void selectRisTemplate(ActionEvent actionEvent) {
        selectedRisTemplate.setSelectedRisTemplate(this.comboRisTemplate.getValue().toString().intern());
    }
}
