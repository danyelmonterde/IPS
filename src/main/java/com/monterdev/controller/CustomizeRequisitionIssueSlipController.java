package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.RequisitionIssueSlip;
import com.monterdev.model.RisType;
import com.monterdev.model.RisTypeFields;
import com.monterdev.model.RisTypeNames;
import com.monterdev.repository.RisTypeNamesRepository;
import com.monterdev.repository.RisTypeRepository;
import com.monterdev.util.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.monterdev.configuration.GlobalConfiguration.*;
import static com.monterdev.constants.DataTypeConstants.*;
import static com.monterdev.constants.TextFieldValidatorConstants.*;

@Component
@FxmlView("CustomizeRis.fxml")
@Getter
public class CustomizeRequisitionIssueSlipController implements Initializable {

    @FXML
    private Label currentDate;
    @FXML
    private JFXTextField requestedBy;
    @FXML
    private JFXTextField division;
    @FXML
    private JFXTextField txtRisNumber;
    @FXML
    private JFXTextField txtDesignation;
    @FXML
    private JFXTextField unit;
    @FXML
    private JFXComboBox customerType;
    @FXML
    private JFXComboBox comboPurposes;
    @FXML
    private JFXButton btnAddRIS;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private HBox risContainer;

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private RequisitionIssueSlip requisitionIssueSlip;
    @Autowired
    private RisTypeRepository risTypeRepository;
    @Autowired
    private List<RisTypeFields> risTypeFieldsList;
    @Autowired
    private RisTypeNamesRepository risTypeNamesRepository;

    private static final String DATE_FORMAT = "dd-MMM-YYYY hh:mm:ss";
    private static final String RIS_TYPE_DATE_FORMAT = "dd-MMM-yyyy";
    private String DATE_PICKER_FORMAT = "dd/MMM/yyyy";

    private String customerName;
    private String generatedControlNumber;
    private int numberOfFields = 0;
    private int currentIndex = 0;
    private int successFields = 0;
    private List<String> fieldTypes;
    private List<RisTypeNames> risTemplates;

    private ControlNumberGenerator controlNumberGenerator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(ObjectUtils.isEmpty(risTemplates)){
            risTemplates = risTypeNamesRepository.findAllRisTypeNames();
        }
        if(ObjectUtils.isEmpty(controlNumberGenerator)){
            controlNumberGenerator = new ControlNumberGenerator();
        }


        getFieldTypes();
        setDate();
        comboRisTemplateOnload();
        customerTypeOnload();
        setRequestedBy();
        setDivision();
        txtRisNumberOnChange();
        txtDesignationOnChange();
        unitOnChange();
    }

    private void unitOnChange() {
        unit.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                requisitionIssueSlip.setUnit(newValue);
            }
        });
    }

    private void txtDesignationOnChange() {
        txtDesignation.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                requisitionIssueSlip.setDesignation(newValue);
            }
        });
    }

    private void txtRisNumberOnChange() {
        txtRisNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                requisitionIssueSlip.setRequisition_and_issue_slip_number(newValue);
            }
        });
    }

    private void getFieldTypes() {
        fieldTypes = Arrays.asList(getRisFieldTypes().split(","));
    }


    private void setDivision() {
        division.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                requisitionIssueSlip.setDivision(newValue);
            }
        });
    }


    private void setRequestedBy() {
        requestedBy.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                requisitionIssueSlip.setRequested_by(newValue);
            }
        });
    }

    private void setDate() {
        currentDate.setText(AppTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
    }

    public void customerTypeOnload() {
        String[] customerTypes = getCustomerTypes().split(",");
        Arrays.asList(customerTypes).stream().forEach(d -> this.customerType.getItems().add(d));
        customerType.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                String customerType = (String) newValue;
                if(customerType.equals(customerTypes[0])){
                    requisitionIssueSlip.setIs_customer_new(0);
                }else{
                    requisitionIssueSlip.setIs_customer_new(1);
                }
            }
        });
    }

    public void comboRisTemplateOnload() {
        risTemplates.stream().forEach(d -> this.comboPurposes.getItems().add(d.getName()));
        resetRisTemplateFields();
        comboPurposes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                risContainer.getChildren().clear();
                requisitionIssueSlip.setRequisition_and_issue_slip_number((String) newValue);
                requisitionIssueSlip.setPurpose((String) newValue);

                if (ObjectUtils.isEmpty(risTypeFieldsList)) {

                    //NEWLY CREATED REQUISITION ISSUE SLIP

                    List<RisType> risType = risTypeRepository.findByRisType((String) newValue);
                    numberOfFields = risType.size();

                    generatedControlNumber = controlNumberGenerator.generate();
                    requisitionIssueSlip.setControl_number(generatedControlNumber);
                    risType.stream().forEach(data -> {
                        if (data.getRisfieldtype().equalsIgnoreCase(TEXT) || data.getRisfieldtype().equalsIgnoreCase(MONEY) || data.getRisfieldtype().equalsIgnoreCase(NUMBER)) {
                            addTextFieldToContainer(data, createTextField(data.getRisfield(), "", data.getRisfieldtype()));
                        } else if (data.getRisfieldtype().equalsIgnoreCase(DATE)) {
                            addDatePickerToContainer(data, LocalDate.now().format(DateTimeFormatter.ofPattern(RIS_TYPE_DATE_FORMAT)));
                        }
                    });
                } else {
                    Optional<ButtonType> optionalButtonType = Prompt.confirm("You still have unreleased item/s on cart. Do you still want to view it? ");
                    if (optionalButtonType.isPresent()) {
                        if (optionalButtonType.get().getText().equalsIgnoreCase("OK")) {
                            //LOAD FILLED DATA HERE
                            numberOfFields = risTypeFieldsList.size();
                            risTypeFieldsList.stream().forEach(data -> {
                                if (data.getRis_field_type().equalsIgnoreCase(TEXT) || data.getRis_field_type().equalsIgnoreCase(NUMBER) || data.getRis_field_type().equalsIgnoreCase(MONEY)) {
                                    generatedControlNumber = data.getControl_number();
                                    RisType risType = new RisType();
                                    risType.setRisfieldtype(data.getRis_field_type());
                                    risType.setRisfield(data.getRis_field());
                                    addTextFieldToContainer(risType, createTextField(data.getRis_field(), data.getRis_value(), data.getRis_field_type()));
                                } else if (data.getRis_field_type().equalsIgnoreCase(DATE)) {
                                    generatedControlNumber = data.getControl_number();
                                    RisType risType = new RisType();
                                    risType.setRisfieldtype(data.getRis_field_type());
                                    addDatePickerToContainer(risType, data.getRis_value());
                                }
                            });
                        } else {

                            //CANCEL OPTION WAS SELECTED AND EXISTING CART WILL BE RESET
                            resetRisTemplateFields();
                        }
                    }
                }
            }
        });
    }

    private void resetRisTemplateFields() {
        numberOfFields = 0;
        currentIndex = 0;
        generatedControlNumber = null;
        customerName = null;
        risTypeFieldsList.clear();
        requisitionIssueSlip.setRequisition_and_issue_slip_number("");
        requisitionIssueSlip.setId(0);
        requisitionIssueSlip.setRistype("");
        requisitionIssueSlip.setControl_number("");
        requisitionIssueSlip.setRequested_by("");
        requisitionIssueSlip.setOffice("");
        requisitionIssueSlip.setDate_transacted("");
        requisitionIssueSlip.setIs_customer_new(0);
        requisitionIssueSlip.setResponsibility_center_code("");
        requisitionIssueSlip.setPurpose("");
        requisitionIssueSlip.setUnit("");
        requisitionIssueSlip.setDesignation("");
    }

    private void addDatePickerToContainer(RisType data, String stringDate) {
        JFXDatePicker dynamicDatePicker = createDateField(data.getRisfield(), currentIndex);
        dynamicDatePicker.setId(data.getRisfield());
        dynamicDatePicker.setAccessibleText(data.getRisfieldtype());
        dynamicDatePicker.setValue(LocalDate.parse(stringDate, DateTimeFormatter.ofPattern(RIS_TYPE_DATE_FORMAT)));
        risContainer.getChildren().add(dynamicDatePicker);
        HBox.setMargin(dynamicDatePicker, new Insets(0, 0, 20, 20));
        currentIndex++;
    }

    private JFXDatePicker createDateField(String promptText, int index) {
        JFXDatePicker datePicker = new JFXDatePicker();
        datePicker.setPromptText(promptText);
        return datePicker;
    }

    private JFXTextField createTextField(String promptText, String text, String fieldType) {
        JFXTextField jfxTextField = new JFXTextField();
        jfxTextField.setPromptText(promptText);
        jfxTextField.setLabelFloat(true);
        jfxTextField.setText(text);

        jfxTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (fieldType.equalsIgnoreCase(fieldTypes.get(1))) {
                //NUMBER FORMAT VALIDATION HERE
                if (NumberUtils.isParsable(jfxTextField.getText()) && !ObjectUtils.isEmpty(jfxTextField.getText())) {
                    //VALID TEXT FIELD

                } else if (ObjectUtils.isEmpty(jfxTextField.getText())) {
                    //EMPTY TEXT FIELD
                    jfxTextField.setText(jfxTextField.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));

                } else {
                    //INVALID TEXT FIELD
                    jfxTextField.setText(jfxTextField.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                    jfxTextField.setText(jfxTextField.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
                }
            } else if (fieldType.equalsIgnoreCase(fieldTypes.get(2))) {
                //MONEY FORMAT VALIDATION HERE
                if (oldValue != newValue) {
                    if (!ObjectUtils.isEmpty(jfxTextField.getText())) {
                        //VALID TEXT FIELD
                        jfxTextField.setText(jfxTextField.getText().replaceAll(FLOAT_NUMBERS_REGEX_EXCLUDE, ""));
                    }
                }
            }

            if (jfxTextField.getPromptText().equalsIgnoreCase("NAME") || jfxTextField.getPromptText().equalsIgnoreCase("CUSTOMER NAME") || jfxTextField.getPromptText().equalsIgnoreCase("CUSTOMER")) {
                customerName = newValue;
            }

        });
        return jfxTextField;
    }

    private void addTextFieldToContainer(RisType data, JFXTextField textField) {
        JFXTextField dynamicTextfield = textField;
        dynamicTextfield.setId(data.getRisfield());
        dynamicTextfield.setAccessibleText(data.getRisfieldtype());
        risContainer.getChildren().add(dynamicTextfield);
        HBox.setMargin(dynamicTextfield, new Insets(0, 0, 20, 20));
        currentIndex++;
    }


    public void addRIS(ActionEvent actionEvent) {
        try {
            requisitionIssueSlip.setRequisition_and_issue_slip_number(txtRisNumber.getText());
            requisitionIssueSlip.setRistype((String) comboPurposes.getValue());
            requisitionIssueSlip.setPurpose((String) comboPurposes.getValue());
            requisitionIssueSlip.setCustomer_name(customerName);
            requisitionIssueSlip.setDate_transacted(AppTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            iterateRisTemplate();
            if (successFields == numberOfFields && successFields!=0 && numberOfFields!=0) {
                //risContainer.getChildren().clear();
                Prompt.success("All RIS details were successfully saved!");
                currentIndex =0;
                successFields = 0;
                numberOfFields = 0;
                Stage stage = (Stage) btnAddRIS.getScene().getWindow();
                new StageLoader().loadTest(MainDashboardController.class, applicationContext, stage);
            } else {
                Prompt.failed("Please fill in all fields!");
            }

        } catch (NullPointerException nullPointerException) {
            Prompt.failed("Please select RIS Template!");
        }
    }

    public void iterateRisTemplate() {
        ObservableList<Node> risContainerChildren = risContainer.getChildren();
        for (Node child : risContainerChildren) {
            if (child instanceof JFXTextField) {
                if (!ObjectUtils.isEmpty(((JFXTextField) child).getText())) {
                    RisTypeFields risTypeFields = new RisTypeFields();
                    risTypeFields.setRistype((String) comboPurposes.getValue());
                    risTypeFields.setRis_field(child.getId());
                    risTypeFields.setRis_value(((JFXTextField) child).getText());
                    risTypeFields.setControl_number(generatedControlNumber);
                    risTypeFields.setRis_field_type(child.getAccessibleText());
                    risTypeFieldsList.add(successFields, risTypeFields);
                    successFields++;
                }

            } else if (child instanceof JFXDatePicker) {
                if (!ObjectUtils.isEmpty(((JFXDatePicker) child).getValue())) {
                    RisTypeFields risTypeFields = new RisTypeFields();
                    risTypeFields.setRistype((String) comboPurposes.getValue());
                    risTypeFields.setRis_field(child.getId());
                    risTypeFields.setRis_value(((JFXDatePicker) child).getValue().format(DateTimeFormatter.ofPattern(DATE_PICKER_FORMAT)));
                    risTypeFields.setControl_number(generatedControlNumber);
                    risTypeFields.setRis_field_type(child.getAccessibleText());
                    risTypeFieldsList.add(successFields, risTypeFields);
                    successFields++;
                }
            }

        }


    }

    private void exitStage() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        new StageLoader().loadTest(MainDashboardController.class, applicationContext, stage);
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnAddRIS.getScene().getWindow();
        resetRisTemplateFields();
        new StageLoader().loadTest(MainDashboardController.class, applicationContext, stage);
    }


}
