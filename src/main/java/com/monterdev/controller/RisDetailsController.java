package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.RequisitionIssueSlip;
import com.monterdev.model.RisType;
import com.monterdev.model.RisTypeFields;
import com.monterdev.model.SelectedRisTemplate;
import com.monterdev.repository.RisTypeRepository;
import com.monterdev.util.ControlNumberGenerator;
import com.monterdev.util.Prompt;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import static com.monterdev.constants.DataTypeConstants.*;

@Component
@FxmlView("RisTemplate.fxml")
@Getter
public class RisDetailsController implements Initializable {

    @FXML
    private Label risTemplateLabel;

    @FXML
    private HBox risContainer;

    @FXML
    private JFXButton btnCancel;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXTextField risNumber;

    @FXML
    private JFXTextField risPurpose;

    @Autowired
    private SelectedRisTemplate selectedRisTemplate;

    @Autowired
    private RisTypeRepository risTypeRepository;

    @Autowired
    private ControlNumberGenerator controlNumberGenerator;

    private String generatedControlNumber = null;

    @Autowired
    private RequisitionIssueSlip requisitionIssueSlip;

    @Autowired
    private List<RisTypeFields> risTypeFieldsList;

    private int currentIndex = 0;

    private int numberOfFields = 0;

    int successFields = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (ObjectUtils.isEmpty(risTypeFieldsList)) {
            generatedControlNumber = controlNumberGenerator.generate();
            List<RisType> risType = risTypeRepository.findByRisType(selectedRisTemplate.getSelectedRisTemplate());
            numberOfFields = risType.size();
            risTemplateLabel.setText(selectedRisTemplate.getSelectedRisTemplate() + " - " + generatedControlNumber);
            risType.stream().forEach(data -> {
                if (data.getRisfieldtype().equalsIgnoreCase(TEXT) || data.getRisfieldtype().equalsIgnoreCase(MONEY) || data.getRisfieldtype().equalsIgnoreCase(NUMBER)) {
                    addTextFieldToContainer(data, createTextField(data.getRisfield(), ""));
                } else if (data.getRisfieldtype().equalsIgnoreCase(DATE)) {
                    addDatePickerToContainer(data,LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")));
                }
            });
        } else {
            currentIndex=0;
            successFields =0;
            numberOfFields = risTypeFieldsList.size();
            risContainer.getChildren().clear();
            risNumber.setText(requisitionIssueSlip.getRequisition_and_issue_slip_number());
            risPurpose.setText(requisitionIssueSlip.getPurpose());
            risTypeFieldsList.stream().forEach(data -> {
                if (data.getRis_field_type().equalsIgnoreCase(TEXT) || data.getRis_field_type().equalsIgnoreCase(NUMBER) || data.getRis_field_type().equalsIgnoreCase(MONEY)) {
                    generatedControlNumber = data.getControl_number();
                    RisType risType = new RisType();
                    risType.setRisfieldtype(data.getRis_field_type());
                    risType.setRisfield(data.getRis_field());
                    addTextFieldToContainer(risType, createTextField(data.getRis_field(), data.getRis_value()));
                } else if (data.getRis_field_type().equalsIgnoreCase(DATE)) {
                    generatedControlNumber = data.getControl_number();
                    RisType risType = new RisType();
                    risType.setRisfieldtype(data.getRis_field_type());
                    addDatePickerToContainer(risType,data.getRis_value());
                }
            });
        }

    }

    private void addDatePickerToContainer(RisType data, String stringDate) {
        JFXDatePicker dynamicDatePicker = createDateField(data.getRisfield(), currentIndex);
        dynamicDatePicker.setId(data.getRisfield());
        dynamicDatePicker.setAccessibleText(data.getRisfieldtype());
        dynamicDatePicker.setValue(LocalDate.parse(stringDate,DateTimeFormatter.ofPattern("dd/MMM/yyyy")));
        risContainer.getChildren().add(dynamicDatePicker);
        HBox.setMargin(dynamicDatePicker, new Insets(20, 0, 0, 20));
        currentIndex++;
    }

    private void addTextFieldToContainer(RisType data, JFXTextField textField) {
        JFXTextField dynamicTextfield = textField;
        dynamicTextfield.setId(data.getRisfield());
        dynamicTextfield.setAccessibleText(data.getRisfieldtype());
        risContainer.getChildren().add(dynamicTextfield);
        HBox.setMargin(dynamicTextfield, new Insets(20, 0, 0, 20));
        currentIndex++;
    }

    private JFXTextField createTextField(String promptText, String text) {
        JFXTextField jfxTextField = new JFXTextField();
        jfxTextField.setPromptText(promptText);
        jfxTextField.setLabelFloat(true);
        jfxTextField.setText(text);
        return jfxTextField;
    }

    private JFXDatePicker createDateField(String promptText, int index) {
        JFXDatePicker datePicker = new JFXDatePicker();
        datePicker.setPromptText(promptText);
        return datePicker;
    }

    public void cancel(ActionEvent actionEvent) {
        resetRisTemplateFields();
        exitStage();
    }

    private void exitStage() {
        currentIndex =0;
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void resetRisTemplateFields() {
        numberOfFields = 0;
        currentIndex = 0;
        generatedControlNumber = null;
        risTypeFieldsList.clear();
    }

    public void save(ActionEvent actionEvent) {
        requisitionIssueSlip.setRequisition_and_issue_slip_number(risNumber.getText());
        requisitionIssueSlip.setPurpose(risPurpose.getText());
        requisitionIssueSlip.setControl_number(generatedControlNumber);
        risTypeFieldsList.clear();
        successFields =0;
        ObservableList<Node> risContainerChildren = risContainer.getChildren();
        for (Node child : risContainerChildren) {
            if (child instanceof JFXTextField) {
                if (!ObjectUtils.isEmpty(((JFXTextField) child).getText())) {
                    RisTypeFields risTypeFields = new RisTypeFields();
                    risTypeFields.setRistype(selectedRisTemplate.getSelectedRisTemplate());
                    risTypeFields.setRis_field(child.getId());
                    risTypeFields.setRis_value(((JFXTextField) child).getText());
                    risTypeFields.setControl_number(generatedControlNumber);
                    risTypeFields.setRis_field_type(child.getAccessibleText());
                    risTypeFieldsList.add(successFields,risTypeFields);
                    successFields++;
                }

            }
            else if (child instanceof JFXDatePicker) {
                if (!ObjectUtils.isEmpty(((JFXDatePicker) child).getValue())) {
                    RisTypeFields risTypeFields = new RisTypeFields();
                    risTypeFields.setRistype(selectedRisTemplate.getSelectedRisTemplate());
                    risTypeFields.setRis_field(child.getId());
                    risTypeFields.setRis_value(((JFXDatePicker) child).getValue().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")));
                    risTypeFields.setControl_number(generatedControlNumber);
                    risTypeFields.setRis_field_type(child.getAccessibleText());
                    risTypeFieldsList.add(successFields,risTypeFields);
                    successFields++;
                }
            }

        }
        if (successFields == numberOfFields) {
            risContainer.getChildren().clear();
            Prompt.success("All RIS details were successfully saved!");
            exitStage();
        } else {
            Prompt.failed("Please fill in all fields!");
        }
    }
}
