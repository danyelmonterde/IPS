package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.constants.GlobalConfiguration;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
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
import java.util.ResourceBundle;

import static com.monterdev.constants.GlobalConfiguration.getRisFieldTypes;
import static com.monterdev.constants.GlobalConfiguration.getSignatoryRole;

@Component
@FxmlView("MasterData.fxml")
@Getter
public class MasterDataController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private InventoryTypeRepository inventoryTypeRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ReportNamesRepository reportNamesRepository;
    @Autowired
    private RisFieldsRepository risFieldsRepository;
    @Autowired
    private RisTypeNamesRepository risTypeNamesRepository;
    @Autowired
    private RisTypeRepository risTypeRepository;
    @Autowired
    private SignatoryRepository signatoryRepository;
    @Autowired
    private UnitRepository unitRepository;
    @FXML
    private AnchorPane masterDataAnchorpane;
    @FXML
    private TabPane tabPane;
    @FXML
    private JFXButton btnExit;

    //MASTER DATA FIELDS
    @FXML
    private JFXTextField tab1TxtInventoryType;
    @FXML
    private JFXTextField tab2TxtReportName;
    @FXML
    private JFXTextField tab2TxtDescription;
    @FXML
    private JFXTextField tab3RisField;
    @FXML
    private JFXTextField tab4TxtRisTypeName;
    @FXML
    private JFXTextField tab4TxtRisName;
    @FXML
    private JFXTextField tab5TxtRisName;
    @FXML
    private JFXTextField tab6TxtSignatoryName;
    @FXML
    private JFXTextField tab6TxtPosition;
    @FXML
    private JFXTextField tab7TxtUnit;
    @FXML
    private JFXComboBox tab2ComboInventoryType;
    @FXML
    private JFXComboBox tab4ComboInventoryType;
    @FXML
    private JFXComboBox tab5ComboInventoryType;
    @FXML
    private JFXComboBox tab5ComboRisType;
    @FXML
    private JFXComboBox tab5ComboRisField;
    @FXML
    private JFXComboBox tab5ComboRisFieldType;
    @FXML
    private JFXComboBox tab6ComboRole;
    @FXML
    private JFXComboBox tab6ComboReportName;
    @FXML
    private JFXButton tab1BtnSaveInventoryType;
    @FXML
    private JFXButton tab2BtnSave;
    @FXML
    private JFXButton tab3BtnSave;
    @FXML
    private JFXButton tab4BtnSave;
    @FXML
    private JFXButton tab5BtnSave;
    @FXML
    private JFXButton tab6BtnSave;
    @FXML
    private JFXButton tab7BtnSave;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOnWindowClose();
        tab1BtnSaveInventoryTypeOnAction();
        tab2BtnSaveReportNameOnAction();
        tab3BtnSaveRisFieldOnAction();
        tab4BtnSaveRisTypeNameOnAction();
        tab5BtnSaveRisTypeOnAction();
        tab6BtnSaveSignatoryOnAction();
        tab7BtnSaveUnitOnAction();
    }

    private void tab1BtnSaveInventoryTypeOnAction() {
        tab1BtnSaveInventoryType.setOnAction(e->{
            if(!ObjectUtils.isEmpty(tab1TxtInventoryType.getText())){
                InventoryType inventoryType = new InventoryType();
                inventoryType.setId(0);
                inventoryType.setInventory_type(tab1TxtInventoryType.getText());
                ItemCategory itemCategory = new ItemCategory();
                itemCategory.setId(0);
                itemCategory.setCategory_name(tab1TxtInventoryType.getText());
                if(!ObjectUtils.isEmpty(inventoryTypeRepository.save(inventoryType)) && !ObjectUtils.isEmpty(categoryRepository.save(itemCategory))){
                    tab1TxtInventoryType.setText("");
                    Prompt.success("New Inventory Type has been added!");
                }else Prompt.failed("Inventory Type was not saved!");
            }
        });
    }

    private void tab2BtnSaveReportNameOnAction(){
        categoryRepository.findAll().forEach(e->{
            tab2ComboInventoryType.getItems().add(e.getCategory_name());
        });
        tab2BtnSave.setOnAction(e->{
            if(!ObjectUtils.isEmpty(tab2TxtReportName.getText()) && !ObjectUtils.isEmpty(tab2TxtDescription.getText())){
                ReportNames reportNames = new ReportNames();
                reportNames.setId(0);
                reportNames.setName(tab2TxtReportName.getText());
                reportNames.setDescription(tab2TxtDescription.getText());
                reportNames.setInventorytype((String)tab2ComboInventoryType.getSelectionModel().getSelectedItem());
                if(!ObjectUtils.isEmpty(reportNamesRepository.save(reportNames))){
                    Prompt.success("Report name was successfully added!");
                }else{
                    Prompt.failed("Report name was NOT added!");
                }
            }
        });

    }

    private void tab3BtnSaveRisFieldOnAction(){
        tab3BtnSave.setOnAction(e->{
            if(!ObjectUtils.isEmpty(tab3RisField.getText())){
                RisFields risField = new RisFields();
                risField.setId(0);
                risField.setRis_field(tab3RisField.getText());
                if(!ObjectUtils.isEmpty(risFieldsRepository.save(risField))){
                    Prompt.success("Ris Field has been added successfully!");
                }else{
                    Prompt.failed("Ris field was NOT saved!");
                }
            }
        });
    }

    private void tab4BtnSaveRisTypeNameOnAction(){
        categoryRepository.findAll().forEach(e->{
            tab4ComboInventoryType.getItems().add(e.getCategory_name());
        });
        tab4BtnSave.setOnAction(e->{
            if(!ObjectUtils.isEmpty(tab4TxtRisName.getText()) && !ObjectUtils.isEmpty(tab4TxtRisTypeName.getText())){
                RisTypeNames risTypeNames = new RisTypeNames();
                risTypeNames.setId(0);
                risTypeNames.setInventorytype((String)tab4ComboInventoryType.getSelectionModel().getSelectedItem());
                risTypeNames.setName(tab4TxtRisName.getText());
                risTypeNames.setRisname(tab4TxtRisTypeName.getText());
                if(!ObjectUtils.isEmpty(risTypeNamesRepository.save(risTypeNames))){
                    Prompt.success("Ris Type name has been added successfully!");
                }else{
                    Prompt.failed("Error saving RIS Type name!");
                }
            }
        });
    }

    private void tab5BtnSaveRisTypeOnAction(){
        categoryRepository.findAll().forEach(e->{
            tab5ComboInventoryType.getItems().add(e.getCategory_name());
        });
        risTypeNamesRepository.findAll().forEach(e->{
            tab5ComboRisType.getItems().add(e.getName());
        });
        risFieldsRepository.findAll().forEach(e->{
            tab5ComboRisField.getItems().add(e.getRis_field());
        });
        List<String> risFieldTypeFormatList = Arrays.asList(getRisFieldTypes().split(","));
        risFieldTypeFormatList.stream().forEach(e->{
            tab5ComboRisFieldType.getItems().add(e);
        });
        tab5BtnSave.setOnAction(e->{
            if(!ObjectUtils.isEmpty(tab5TxtRisName.getText())){
                RisType risType = new RisType();
                risType.setInventory_type((String)tab5ComboInventoryType.getSelectionModel().getSelectedItem());
                risType.setRistype((String) tab5ComboRisType.getSelectionModel().getSelectedItem());
                risType.setRisfield((String) tab5ComboRisField.getSelectionModel().getSelectedItem());
                risType.setRisfieldtype((String)tab5ComboRisFieldType.getSelectionModel().getSelectedItem());
                risType.setRisname(tab5TxtRisName.getText());
                if(!ObjectUtils.isEmpty(risTypeRepository.save(risType))){
                    Prompt.success("RIS Type has been added successfully!");
                }else Prompt.failed("RIS Type was NOT saved!");
            }
        });
    }

    private void tab6BtnSaveSignatoryOnAction(){
        List<String> signatoryRole = Arrays.asList(getSignatoryRole().split(","));
        signatoryRole.stream().forEach(e->{
            tab6ComboRole.getItems().add(e);
        });
        reportNamesRepository.findAll().forEach(e->{
            tab6ComboReportName.getItems().add(e.getId()+"-"+e.getName()+"-"+e.getDescription());
        });
        tab6BtnSave.setOnAction(e->{
            if(!ObjectUtils.isEmpty(tab6TxtSignatoryName.getText())&&!ObjectUtils.isEmpty(tab6TxtPosition.getText())){
                Signatory signatory = new Signatory();
                signatory.setId(0);
                signatory.setSignatory(tab6TxtSignatoryName.getText());
                signatory.setPosition(tab6TxtPosition.getText());
                String reportName = (String)tab6ComboReportName.getSelectionModel().getSelectedItem();
                signatory.setReportid(Integer.parseInt(reportName.split("-")[0]));
                signatory.setRole((String) tab6ComboRole.getSelectionModel().getSelectedItem());
                if(!ObjectUtils.isEmpty(signatoryRepository.save(signatory))){
                    Prompt.success("Signatory has been added successfully!");
                }else{
                    Prompt.failed("Error saving signatory!");
                }
            }
        });
    }

    private void tab7BtnSaveUnitOnAction(){
        tab7BtnSave.setOnAction(e->{
            if(!ObjectUtils.isEmpty(tab7TxtUnit.getText())){
                Unit unit = new Unit();
                unit.setUnit(tab7TxtUnit.getText());
                unit.setId(0);
                if(!ObjectUtils.isEmpty(unitRepository.save(unit))){
                    Prompt.success("Unit has been added successfully!");
                }else{
                    Prompt.failed("Unit was NOT saved!");
                }
            }
        });
    }
    private void setOnWindowClose() {

        btnExit.setOnAction(e->{
            Stage currentStage = (Stage) btnExit.getScene().getWindow();
            new StageLoader().load(MainDashboardController.class,applicationContext,currentStage);
            
        });
    }
}
