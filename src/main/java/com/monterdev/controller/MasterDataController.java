package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
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

    //MASTER DATA FIELDS
    @FXML
    private AnchorPane masterDataAnchorpane;
    @FXML
    private TabPane tabPane;
    @FXML
    private JFXButton btnExit;
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
    @FXML
    private Label labelList;
    @FXML
    private TableView tableResult;
    @FXML
    private Tab tabUnit;
    @FXML
    private Tab tabSignatory;
    @FXML
    private Tab tabRisType;
    @FXML
    private Tab tabRisTypeNames;
    @FXML
    private Tab tabRisFields;
    @FXML
    private Tab tabReportNames;
    @FXML
    private Tab tabInventoryTypes;

    //MASTER DATA LISTS
    List<InventoryType> inventoryTypeList = new ArrayList<>();
    List<ReportNames> reportNamesList = new ArrayList<>();
    List<RisFields> risFieldsList = new ArrayList<>();
    List<RisTypeNames> risTypeNamesList = new ArrayList<>();
    List<RisType> risTypeList = new ArrayList<>();
    List<Signatory> signatoryList = new ArrayList<>();
    List<Unit> unitList = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelListOnChange();
        setTableView(0,InventoryType.class);
        initializeTableResult(inventoryTypeRepository.findAllInventoryType());
        setOnWindowClose();
        tabPaneOnAction();
        tab1BtnSaveInventoryTypeOnAction();
        tab1BtnRefreshInventoryTypesOnAction();
        tab2BtnSaveReportNameOnAction();
        tab3BtnSaveRisFieldOnAction();
        tab4BtnSaveRisTypeNameOnAction();
        tab5BtnSaveRisTypeOnAction();
        tab6BtnSaveSignatoryOnAction();
        tab7BtnSaveUnitOnAction();
    }

    private void labelListOnChange() {
        labelList.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(oldValue!=newValue){

            }
        }));
    }

    private void setTableView(int selectedTabPaneIndex, Class<?> aClass) {
        tableResult.getColumns().clear();
        tableResult.setPrefHeight(361.0);
        tableResult.setPrefWidth(157.0);
        tableResult.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Field[] fieldsArray= aClass.getDeclaredFields();
        Arrays.asList(fieldsArray).stream().forEach(e->{
            tableResult.getColumns().add(createTableColumn(selectedTabPaneIndex,e.getName()));
        });

    }

    private TableColumn createTableColumn(int tabPaneIndex,String databaseColumn){
        TableColumn tableColumn = null;
        if(tabPaneIndex==0){
            tableColumn = new TableColumn<InventoryType,String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }else if(tabPaneIndex==1){
            tableColumn = new TableColumn<ReportNames,String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }else if(tabPaneIndex==2){
            tableColumn = new TableColumn<RisFields,String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }else if(tabPaneIndex==3){
            tableColumn = new TableColumn<RisTypeNames,String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }else if(tabPaneIndex==4){
            tableColumn = new TableColumn<RisType,String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }else if(tabPaneIndex==5){
            tableColumn = new TableColumn<Signatory,String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }else if(tabPaneIndex==6){
            tableColumn = new TableColumn<Unit,String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }else{
            tableColumn = new TableColumn<InventoryType,String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }
        tableColumn.setText(databaseColumn);
        tableColumn.setPrefWidth(111.0);
        tableColumn.setEditable(false);
        return tableColumn;
    }

    private void initializeTableResult(List<?> list) {
        tableResult.getItems().setAll(FXCollections.observableArrayList(list));
    }

    private void clearList(){
        inventoryTypeList.clear();
        reportNamesList.clear();
        risFieldsList.clear();
        risTypeNamesList.clear();
        risTypeList.clear();
        signatoryList.clear();
        unitList.clear();
    }

    private void tabPaneOnAction() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                clearList();
                int selectedTabPaneIndex = (int) newValue;
                if(selectedTabPaneIndex == 0){
                    labelList.setText("LIST OF EXISTING INVENTORY TYPES");
                    inventoryTypeList = inventoryTypeRepository.findAllInventoryType();
                    setTableView(selectedTabPaneIndex,InventoryType.class);
                    initializeTableResult(inventoryTypeList);
                }else if(selectedTabPaneIndex == 1){
                    labelList.setText("LIST OF EXISTING REPORT NAMES");
                    reportNamesList = reportNamesRepository.findAllAvailableReports();
                    setTableView(selectedTabPaneIndex,ReportNames.class);
                    initializeTableResult(reportNamesList);
                }else if(selectedTabPaneIndex == 2){
                    labelList.setText("LIST OF EXISTING REQUISITION ISSUE SLIP FIELDS");
                    risFieldsList = risFieldsRepository.findAll();
                    setTableView(selectedTabPaneIndex,RisFields.class);
                    initializeTableResult(risFieldsList);
                }else if(selectedTabPaneIndex == 3){
                    labelList.setText("LIST OF EXISTING REQUISITION ISSUE SLIP TYPE NAMES");
                    risTypeNamesList = risTypeNamesRepository.findAllRisTypeNames();
                    setTableView(selectedTabPaneIndex,RisTypeNames.class);
                    initializeTableResult(risTypeNamesList);
                }else if(selectedTabPaneIndex == 4){
                    labelList.setText("LIST OF EXISTING REQUISITION ISSUE SLIP TYPES");
                    risTypeList = risTypeRepository.findAll();
                    setTableView(selectedTabPaneIndex,RisType.class);
                    initializeTableResult(risTypeList);
                }else if(selectedTabPaneIndex == 5){
                    labelList.setText("LIST OF EXISTING SIGNATORIES");
                    signatoryList = signatoryRepository.findAll();
                    setTableView(selectedTabPaneIndex,Signatory.class);
                    initializeTableResult(signatoryList);
                }else if(selectedTabPaneIndex == 6){
                    labelList.setText("LIST OF EXISTING UNITS");
                    unitList = unitRepository.findAllItemUnits();
                    setTableView(selectedTabPaneIndex,Unit.class);
                    initializeTableResult(unitList);
                }
            }
        });
    }

    private void tab1BtnRefreshInventoryTypesOnAction() {
    }

    private void tab1BtnSaveInventoryTypeOnAction() {
        tab1BtnSaveInventoryType.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab1TxtInventoryType.getText())) {
                InventoryType inventoryType = new InventoryType();
                inventoryType.setId(0);
                inventoryType.setInventory_type(tab1TxtInventoryType.getText());
                ItemCategory itemCategory = new ItemCategory();
                itemCategory.setId(0);
                itemCategory.setCategory_name(tab1TxtInventoryType.getText());
                if (!ObjectUtils.isEmpty(inventoryTypeRepository.save(inventoryType)) && !ObjectUtils.isEmpty(categoryRepository.save(itemCategory))) {
                    tab1TxtInventoryType.setText("");
                    Prompt.success("New Inventory Type has been added!");
                } else Prompt.failed("Inventory Type was not saved!");
            }
        });
    }

    private void tab2BtnSaveReportNameOnAction() {
        categoryRepository.findAll().forEach(e -> {
            tab2ComboInventoryType.getItems().add(e.getCategory_name());
        });
        tab2BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab2TxtReportName.getText()) && !ObjectUtils.isEmpty(tab2TxtDescription.getText())) {
                ReportNames reportNames = new ReportNames();
                reportNames.setId(0);
                reportNames.setName(tab2TxtReportName.getText());
                reportNames.setDescription(tab2TxtDescription.getText());
                reportNames.setInventorytype((String) tab2ComboInventoryType.getSelectionModel().getSelectedItem());
                if (!ObjectUtils.isEmpty(reportNamesRepository.save(reportNames))) {
                    Prompt.success("Report name was successfully added!");
                } else {
                    Prompt.failed("Report name was NOT added!");
                }
            }
        });

    }

    private void tab3BtnSaveRisFieldOnAction() {
        tab3BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab3RisField.getText())) {
                RisFields risField = new RisFields();
                risField.setId(0);
                risField.setRis_field(tab3RisField.getText());
                if (!ObjectUtils.isEmpty(risFieldsRepository.save(risField))) {
                    Prompt.success("Ris Field has been added successfully!");
                } else {
                    Prompt.failed("Ris field was NOT saved!");
                }
            }
        });
    }

    private void tab4BtnSaveRisTypeNameOnAction() {
        categoryRepository.findAll().forEach(e -> {
            tab4ComboInventoryType.getItems().add(e.getCategory_name());
        });
        tab4BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab4TxtRisName.getText()) && !ObjectUtils.isEmpty(tab4TxtRisTypeName.getText())) {
                RisTypeNames risTypeNames = new RisTypeNames();
                risTypeNames.setId(0);
                risTypeNames.setInventorytype((String) tab4ComboInventoryType.getSelectionModel().getSelectedItem());
                risTypeNames.setName(tab4TxtRisName.getText());
                risTypeNames.setRisname(tab4TxtRisTypeName.getText());
                if (!ObjectUtils.isEmpty(risTypeNamesRepository.save(risTypeNames))) {
                    Prompt.success("Ris Type name has been added successfully!");
                } else {
                    Prompt.failed("Error saving RIS Type name!");
                }
            }
        });
    }

    private void tab5BtnSaveRisTypeOnAction() {
        categoryRepository.findAll().forEach(e -> {
            tab5ComboInventoryType.getItems().add(e.getCategory_name());
        });
        risTypeNamesRepository.findAll().forEach(e -> {
            tab5ComboRisType.getItems().add(e.getName());
        });
        risFieldsRepository.findAll().forEach(e -> {
            tab5ComboRisField.getItems().add(e.getRis_field());
        });
        List<String> risFieldTypeFormatList = Arrays.asList(getRisFieldTypes().split(","));
        risFieldTypeFormatList.stream().forEach(e -> {
            tab5ComboRisFieldType.getItems().add(e);
        });
        tab5BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab5TxtRisName.getText())) {
                RisType risType = new RisType();
                risType.setInventory_type((String) tab5ComboInventoryType.getSelectionModel().getSelectedItem());
                risType.setRistype((String) tab5ComboRisType.getSelectionModel().getSelectedItem());
                risType.setRisfield((String) tab5ComboRisField.getSelectionModel().getSelectedItem());
                risType.setRisfieldtype((String) tab5ComboRisFieldType.getSelectionModel().getSelectedItem());
                risType.setRisname(tab5TxtRisName.getText());
                if (!ObjectUtils.isEmpty(risTypeRepository.save(risType))) {
                    Prompt.success("RIS Type has been added successfully!");
                } else Prompt.failed("RIS Type was NOT saved!");
            }
        });
    }

    private void tab6BtnSaveSignatoryOnAction() {
        List<String> signatoryRole = Arrays.asList(getSignatoryRole().split(","));
        signatoryRole.stream().forEach(e -> {
            tab6ComboRole.getItems().add(e);
        });
        reportNamesRepository.findAll().forEach(e -> {
            tab6ComboReportName.getItems().add(e.getId() + "-" + e.getName() + "-" + e.getDescription());
        });
        tab6BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab6TxtSignatoryName.getText()) && !ObjectUtils.isEmpty(tab6TxtPosition.getText())) {
                Signatory signatory = new Signatory();
                signatory.setId(0);
                signatory.setSignatory(tab6TxtSignatoryName.getText());
                signatory.setPosition(tab6TxtPosition.getText());
                String reportName = (String) tab6ComboReportName.getSelectionModel().getSelectedItem();
                signatory.setReportid(Integer.parseInt(reportName.split("-")[0]));
                signatory.setRole((String) tab6ComboRole.getSelectionModel().getSelectedItem());
                if (!ObjectUtils.isEmpty(signatoryRepository.save(signatory))) {
                    Prompt.success("Signatory has been added successfully!");
                } else {
                    Prompt.failed("Error saving signatory!");
                }
            }
        });
    }

    private void tab7BtnSaveUnitOnAction() {
        tab7BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab7TxtUnit.getText())) {
                Unit unit = new Unit();
                unit.setUnit(tab7TxtUnit.getText());
                unit.setId(0);
                if (!ObjectUtils.isEmpty(unitRepository.save(unit))) {
                    Prompt.success("Unit has been added successfully!");
                } else {
                    Prompt.failed("Unit was NOT saved!");
                }
            }
        });
    }

    private void setOnWindowClose() {

        btnExit.setOnAction(e -> {
            Stage currentStage = (Stage) btnExit.getScene().getWindow();
            new StageLoader().load(MainDashboardController.class, applicationContext, currentStage);

        });
    }
}
