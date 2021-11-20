package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.constants.GlobalConfiguration;
import com.monterdev.model.InventoryType;
import com.monterdev.model.RisType;
import com.monterdev.model.RisTypeNames;
import com.monterdev.model.SelectedRisTemplate;
import com.monterdev.repository.RisTypeRepository;
import com.monterdev.util.Prompt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("CreateRisTemplate.fxml")
@Getter
public class CreateRequisitionTemplateController implements Initializable {

    @FXML
    private JFXButton addTemplateBtn;
    @FXML
    private JFXButton deleteAllBtn;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private VBox risTemplateContainer;

    @Autowired
    private RisTypeRepository risTypeRepository;

    @Autowired
    private List<InventoryType> inventoryTypesList;

    @Autowired
    private List<RisTypeNames> risTemplates;

    @Autowired
    private SelectedRisTemplate selectedRisTemplate;

    private List<RisType> risTypeList;

    private String[] risFieldTypes = null;

    private int counter = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        risFieldTypes = GlobalConfiguration.getRisFieldTypes().split(",");
        risTypeList = new ArrayList<>();

    }


    public void addTemplate(ActionEvent actionEvent) {
        RisType risTypeModel = new RisType();

        HBox hBox = new HBox();

        JFXComboBox inventoryTypesCombo = new JFXComboBox();
        inventoryTypesCombo.setAccessibleText("INVENTORY_TYPE");
        inventoryTypesList.stream().forEach(d -> {
            inventoryTypesCombo.getItems().add(d.getInventory_type());
        });
        inventoryTypesCombo.setPromptText("SELECT INVENTORY TYPE");
        inventoryTypesCombo.setId(String.valueOf(counter));
        inventoryTypesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                risTypeModel.setInventory_type(String.valueOf(newValue));
            }
        });

        JFXComboBox risTypesCombo = new JFXComboBox();
        risTypesCombo.setAccessibleText("RIS_TYPE");
        risTemplates.stream().forEach(d -> {
            risTypesCombo.getItems().add(d.getName());
        });
        risTypesCombo.setPromptText("SELECT RIS TYPE");
        risTypesCombo.setId(String.valueOf(counter));
        risTypesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                risTypeModel.setRistype(String.valueOf(newValue));
            }
        });

        JFXTextField risField = new JFXTextField();
        risField.setAccessibleText("RIS_FIELD_NAME");
        risField.setPromptText("RIS FIELD NAME");
        risField.setId(String.valueOf(counter));
        risField.setLabelFloat(true);
        risField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                risTypeModel.setRisfield(newValue);
            }
        });


        JFXComboBox risFieldTypesCombo = new JFXComboBox();
        risFieldTypesCombo.setAccessibleText("FIELD_TYPE");
        Arrays.asList(risFieldTypes).stream().forEach(d -> {
            risFieldTypesCombo.getItems().add(d);
        });
        risFieldTypesCombo.setPromptText("SELECT FIELD TYPE");
        risFieldTypesCombo.setId(String.valueOf(counter));
        risFieldTypesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                risTypeModel.setRisfieldtype((String) newValue);
            }
        });

        hBox.getChildren().addAll(inventoryTypesCombo, risTypesCombo, risField, risFieldTypesCombo);
        HBox.setMargin(inventoryTypesCombo, new Insets(20, 0, 20, 20));
        HBox.setMargin(risTypesCombo, new Insets(20, 0, 20, 20));
        HBox.setMargin(risField, new Insets(20, 0, 20, 20));
        HBox.setMargin(risFieldTypesCombo, new Insets(20, 0, 20, 20));

        risTemplateContainer.getChildren().add(hBox);
        VBox.setMargin(hBox, new Insets(20, 0, 20, 20));
        risTypeList.add(counter, risTypeModel);
        counter++;
    }

    public void deleteAll(ActionEvent actionEvent) {
        risTemplateContainer.getChildren().clear();
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) risTemplateContainer.getScene().getWindow();
        stage.close();
    }

    public void save(ActionEvent actionEvent) {
        for (int ctr = 0; ctr < risTypeList.size(); ctr++) {
            if (!ObjectUtils.isEmpty(risTypeList.get(ctr))) {
                if (ctr == risTypeList.size() - 1) {
                    risTypeRepository.save(risTypeList.get(ctr));
                    Prompt.success("Newly Created Template was saved!");
                } else {
                    risTypeRepository.save(risTypeList.get(ctr));
                }
            } else {
                Prompt.failed("Pls fill in all details!");
            }
        }

    }

}
