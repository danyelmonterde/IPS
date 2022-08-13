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
import javafx.util.StringConverter;
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
import java.util.stream.Collectors;

import static com.monterdev.configuration.GlobalConfiguration.getRisFieldTypes;
import static com.monterdev.configuration.GlobalConfiguration.getSignatoryRole;

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
    private JFXTextField tab2JrxmlReportFileName;
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
    private JFXComboBox tab2ComboRisTypeName;
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
    private int sku = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelListOnChange();
        setTableView(0, InventoryType.class);
        refreshTableResult(inventoryTypeRepository.findAllInventoryType());
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


    private void refreshMasterDataList(){
        inventoryTypeList = inventoryTypeRepository.findAllInventoryType();
        reportNamesList = reportNamesRepository.findAllAvailableReports();
        risFieldsList = risFieldsRepository.findAll();
        risTypeNamesList = risTypeNamesRepository.findAllRisTypeNames();
        risTypeList = risTypeRepository.findAll();
        signatoryList = signatoryRepository.findAll();
        unitList = unitRepository.findAll();
    }

    private void labelListOnChange() {
        labelList.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {

            }
        }));
    }

    private void setTableView(int selectedTabPaneIndex, Class<?> aClass) {
        tableResult.getColumns().clear();
        tableResult.setPrefHeight(361.0);
        tableResult.setPrefWidth(157.0);
        tableResult.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Field[] fieldsArray = aClass.getDeclaredFields();
        Arrays.asList(fieldsArray).stream().forEach(e -> {
            tableResult.getColumns().add(createTableColumn(selectedTabPaneIndex, e.getName()));
        });
        tableRowOnClick(selectedTabPaneIndex);
    }


    private void tableRowOnClick(int selectedTabPaneIndex) {
        if (selectedTabPaneIndex == 0) {
            ObservableList<InventoryType> cellData = FXCollections.observableArrayList();
            tableResult.setOnMouseClicked(cell -> {
                if (cell.getClickCount() == 2) {
                    cellData.clear();
                    cellData.add((InventoryType) tableResult.getSelectionModel().getSelectedItem());
                    tab1TxtInventoryType.setText(cellData.get(0).getInventory_type());
                    sku = cellData.get(0).getId();
                }
            });
        } else if (selectedTabPaneIndex == 1) {
            ObservableList<ReportNames> cellData = FXCollections.observableArrayList();
            tableResult.setOnMouseClicked(cell -> {
                if (cell.getClickCount() == 2) {
                    cellData.clear();
                    cellData.add((ReportNames) tableResult.getSelectionModel().getSelectedItem());
                    tab2TxtReportName.setText(cellData.get(0).getName());
                    tab2ComboInventoryType.setValue(cellData.get(0).getInventorytype());
                    tab2TxtDescription.setText(cellData.get(0).getDescription());
                    tab2ComboRisTypeName.setValue(cellData.get(0).getRisTypeName());
                    tab2JrxmlReportFileName.setText(cellData.get(0).getJrxmlReportFileName());
                    sku = cellData.get(0).getId();
                }
            });
        } else if (selectedTabPaneIndex == 2) {
            ObservableList<RisFields> cellData = FXCollections.observableArrayList();
            tableResult.setOnMouseClicked(cell -> {
                if (cell.getClickCount() == 2) {
                    cellData.clear();
                    cellData.add((RisFields) tableResult.getSelectionModel().getSelectedItem());
                    tab3RisField.setText(cellData.get(0).getRis_field());
                    sku = cellData.get(0).getId();
                }
            });
        } else if (selectedTabPaneIndex == 3) {
            ObservableList<RisTypeNames> cellData = FXCollections.observableArrayList();
            tableResult.setOnMouseClicked(cell -> {
                if (cell.getClickCount() == 2) {
                    cellData.clear();
                    cellData.add((RisTypeNames) tableResult.getSelectionModel().getSelectedItem());
                    tab4TxtRisTypeName.setText(cellData.get(0).getName());
                    tab4ComboInventoryType.setValue(cellData.get(0).getInventorytype());
                    tab4TxtRisName.setText(cellData.get(0).getRisname());
                    sku = cellData.get(0).getId();
                }
            });
        } else if (selectedTabPaneIndex == 4) {
            ObservableList<RisType> cellData = FXCollections.observableArrayList();
            tableResult.setOnMouseClicked(cell -> {
                if (cell.getClickCount() == 2) {
                    cellData.clear();
                    cellData.add((RisType) tableResult.getSelectionModel().getSelectedItem());
                    tab5ComboInventoryType.setValue(cellData.get(0).getInventory_type());
                    tab5ComboRisType.setValue(cellData.get(0).getRistype());
                    tab5ComboRisField.setValue(cellData.get(0).getRisfield());
                    tab5ComboRisFieldType.setValue(cellData.get(0).getRisfieldtype());
                    tab5TxtRisName.setText(cellData.get(0).getRisname());
                    sku = cellData.get(0).getId();
                }
            });
        } else if (selectedTabPaneIndex == 5) {
            ObservableList<Signatory> cellData = FXCollections.observableArrayList();
            tableResult.setOnMouseClicked(cell -> {
                if (cell.getClickCount() == 2) {
                    cellData.clear();
                    cellData.add((Signatory) tableResult.getSelectionModel().getSelectedItem());
                    tab6TxtSignatoryName.setText(cellData.get(0).getSignatory());
                    tab6TxtPosition.setText(cellData.get(0).getPosition());
                    tab6ComboRole.setValue(cellData.get(0).getRole());
                    tab6ComboReportName.setValue(cellData.get(0).getReportid() + "-" + reportNamesRepository.findReportNameById(cellData.get(0).getReportid()).getName() + "-" + reportNamesRepository.findReportNameById(cellData.get(0).getReportid()).getDescription());
                    sku = cellData.get(0).getId();
                }
            });
        } else if (selectedTabPaneIndex == 6) {
            ObservableList<Unit> cellData = FXCollections.observableArrayList();
            tableResult.setOnMouseClicked(cell -> {
                if (cell.getClickCount() == 2) {
                    cellData.clear();
                    cellData.add((Unit) tableResult.getSelectionModel().getSelectedItem());
                    tab7TxtUnit.setText(cellData.get(0).getUnit());
                    sku = cellData.get(0).getId();
                }
            });
        }


    }

    private TableColumn createTableColumn(int tabPaneIndex, String databaseColumn) {
        TableColumn tableColumn = null;
        if (tabPaneIndex == 0) {
            tableColumn = new TableColumn<InventoryType, String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        } else if (tabPaneIndex == 1) {
            tableColumn = new TableColumn<ReportNames, String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        } else if (tabPaneIndex == 2) {
            tableColumn = new TableColumn<RisFields, String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        } else if (tabPaneIndex == 3) {
            tableColumn = new TableColumn<RisTypeNames, String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        } else if (tabPaneIndex == 4) {
            tableColumn = new TableColumn<RisType, String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        } else if (tabPaneIndex == 5) {
            tableColumn = new TableColumn<Signatory, String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        } else if (tabPaneIndex == 6) {
            tableColumn = new TableColumn<Unit, String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        } else {
            tableColumn = new TableColumn<InventoryType, String>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(databaseColumn));
        }
        tableColumn.setText(databaseColumn);
        tableColumn.setPrefWidth(111.0);
        tableColumn.setEditable(false);
        return tableColumn;
    }

    private void refreshTableResult(List<?> list) {
        tableResult.getItems().setAll(FXCollections.observableArrayList(list));
    }

    private void clearList() {
        inventoryTypeList.clear();
        reportNamesList.clear();
        risFieldsList.clear();
        risTypeNamesList.clear();
        risTypeList.clear();
        signatoryList.clear();
        unitList.clear();
    }

    private void clearFields() {
        tab1TxtInventoryType.setText("");

        tab2TxtReportName.setText("");

        tab2TxtDescription.setText("");

        tab2JrxmlReportFileName.setText("");

        tab3RisField.setText("");

        tab4TxtRisTypeName.setText("");

        tab4TxtRisName.setText("");

        tab5TxtRisName.setText("");

        tab6TxtSignatoryName.setText("");

        tab6TxtPosition.setText("");

        tab7TxtUnit.setText("");

//        tab2ComboInventoryType.setValue("");
//
//        tab2ComboRisTypeName.setValue("");
//
//        tab4ComboInventoryType.setValue("");
//
//        tab5ComboInventoryType.setValue("");
//
//        tab5ComboRisType.setValue("");
//
//        tab5ComboRisField.setValue("");
//
//        tab5ComboRisFieldType.setValue("");
//
//        tab6ComboRole.setValue("");
//
//        tab6ComboReportName.setValue("");

    }

    private void tabPaneOnAction() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                sku = 0;
                clearFields();
                clearList();
                int selectedTabPaneIndex = (int) newValue;
                if (selectedTabPaneIndex == 0) {
                    refreshMasterDataList();
                    labelList.setText("LIST OF EXISTING INVENTORY TYPES");
                    setTableView(selectedTabPaneIndex, InventoryType.class);
                    refreshTableResult(inventoryTypeList);
                } else if (selectedTabPaneIndex == 1) {
                    refreshMasterDataList();

                    ObservableList<InventoryType> inventoryTypeObservableList = FXCollections.observableArrayList(inventoryTypeList);
                    tab2ComboInventoryType.setItems(inventoryTypeObservableList);
                    StringConverter<InventoryType> inventoryTypeStringConverter = getInventoryTypeStringConverter(inventoryTypeObservableList);
                    tab2ComboInventoryType.setConverter(inventoryTypeStringConverter);

                    ObservableList<RisTypeNames> risTypeNamesObservableList = FXCollections.observableArrayList(risTypeNamesList);
                    StringConverter<RisTypeNames> risTypeNamesStringConverter = getRisTypeNamesStringConverter(risTypeNamesObservableList);
                    tab2ComboRisTypeName.setConverter(risTypeNamesStringConverter);

                    labelList.setText("LIST OF EXISTING REPORT NAMES");
                    setTableView(selectedTabPaneIndex, ReportNames.class);
                    refreshTableResult(reportNamesList);
                } else if (selectedTabPaneIndex == 2) {
                    refreshMasterDataList();
                    labelList.setText("LIST OF EXISTING REQUISITION ISSUE SLIP FIELDS");
                    setTableView(selectedTabPaneIndex, RisFields.class);
                    refreshTableResult(risFieldsList);
                } else if (selectedTabPaneIndex == 3) {
                    refreshMasterDataList();

                    ObservableList<InventoryType> inventoryTypeObservableList = FXCollections.observableArrayList(inventoryTypeList);
                    tab4ComboInventoryType.setItems(inventoryTypeObservableList);
                    StringConverter<InventoryType> inventoryTypeStringConverter = getInventoryTypeStringConverter(inventoryTypeObservableList);
                    tab4ComboInventoryType.setConverter(inventoryTypeStringConverter);

                    labelList.setText("LIST OF EXISTING REQUISITION ISSUE SLIP TYPE NAMES");
                    setTableView(selectedTabPaneIndex, RisTypeNames.class);
                    refreshTableResult(risTypeNamesList);
                } else if (selectedTabPaneIndex == 4) {
                    refreshMasterDataList();

                    ObservableList<InventoryType> inventoryTypeObservableList = FXCollections.observableArrayList(inventoryTypeList);
                    tab5ComboInventoryType.setItems(inventoryTypeObservableList);
                    StringConverter<InventoryType> inventoryTypeStringConverter = getInventoryTypeStringConverter(inventoryTypeObservableList);
                    tab5ComboInventoryType.setConverter(inventoryTypeStringConverter);

                    ObservableList<RisType> risTypeObservableList = FXCollections.observableArrayList(risTypeList);
                    tab5ComboRisType.setItems(risTypeObservableList);
                    StringConverter<RisType> risTypeStringConverter = getRisTypeStringConverter(risTypeObservableList);
                    tab5ComboRisType.setConverter(risTypeStringConverter);

                    ObservableList<RisFields> risFieldsObservableList  = FXCollections.observableArrayList(risFieldsList);
                    tab5ComboRisField.setItems(risFieldsObservableList);
                    StringConverter<RisFields> risFieldsStringConverter = getRisFieldsStringConverter(risFieldsObservableList);
                    tab5ComboRisField.setConverter(risFieldsStringConverter);

                    labelList.setText("LIST OF EXISTING REQUISITION ISSUE SLIP TYPES");
                    setTableView(selectedTabPaneIndex, RisType.class);
                    refreshTableResult(risTypeList);
                } else if (selectedTabPaneIndex == 5) {
                    refreshMasterDataList();

                    ObservableList<ReportNames> reportNamesObservableList = FXCollections.observableArrayList(reportNamesList);
                    tab6ComboReportName.setItems(reportNamesObservableList);
                    StringConverter<ReportNames> reportNamesStringConverter = getReportNamesStringConverter(reportNamesObservableList);
                    tab6ComboReportName.setConverter(reportNamesStringConverter);

                    labelList.setText("LIST OF EXISTING SIGNATORIES");
                    setTableView(selectedTabPaneIndex, Signatory.class);
                    refreshTableResult(signatoryList);
                } else if (selectedTabPaneIndex == 6) {
                    refreshMasterDataList();
                    labelList.setText("LIST OF EXISTING UNITS");
                    setTableView(selectedTabPaneIndex, Unit.class);
                    refreshTableResult(unitList);
                }
            }
        });
    }

    private void tab1BtnRefreshInventoryTypesOnAction() {
        refreshMasterDataList();
    }

    private void tab1BtnSaveInventoryTypeOnAction() {
        tab1BtnSaveInventoryType.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab1TxtInventoryType.getText())) {
                InventoryType inventoryType = new InventoryType();
                inventoryType.setId(sku);
                inventoryType.setInventory_type(tab1TxtInventoryType.getText());
                ItemCategory itemCategory = new ItemCategory();
                itemCategory.setId(sku);
                itemCategory.setCategory_name(tab1TxtInventoryType.getText());
                if (!ObjectUtils.isEmpty(inventoryTypeRepository.save(inventoryType)) && !ObjectUtils.isEmpty(categoryRepository.save(itemCategory))) {
                    tab1TxtInventoryType.setText("");
                    Prompt.success("New Inventory Type has been added!");
                    refreshMasterDataList();
                    refreshTableResult(inventoryTypeList);
                } else Prompt.failed("Inventory Type was not saved!");
            }
        });
    }

    private void tab2BtnSaveReportNameOnAction() {
        categoryRepository.findAll().forEach(e -> {
            tab2ComboInventoryType.getItems().add(e.getCategory_name());
        });
        risTypeNamesRepository.findAllRisTypeNames().stream().forEach(e -> {
            tab2ComboRisTypeName.getItems().add(e.getName());
        });
        tab2BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab2TxtReportName.getText()) && !ObjectUtils.isEmpty(tab2TxtDescription.getText())) {
                ReportNames reportNames = new ReportNames();
                reportNames.setId(sku);
                reportNames.setRisTypeName((String) tab2ComboRisTypeName.getValue());
                reportNames.setJrxmlReportFileName(tab2JrxmlReportFileName.getText());
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
                risField.setId(sku);
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
                risTypeNames.setId(sku);
                risTypeNames.setInventorytype( tab4ComboInventoryType.getValue().toString());
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
                risType.setId(sku);
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
                signatory.setId(sku);
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
                unit.setId(sku);
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

    private StringConverter<InventoryType> getInventoryTypeStringConverter(ObservableList<InventoryType> inventoryTypeObservableList) {
        StringConverter<InventoryType> inventoryTypeStringConverter = new StringConverter<InventoryType>() {
            @Override
            public String toString(InventoryType object) {
                return object.getInventory_type();
            }

            @Override
            public InventoryType fromString(String id) {
                return inventoryTypeObservableList.stream()
                        .filter(item -> item.getInventory_type().equals(id))
                        .collect(Collectors.toList()).get(0);
            }
        };
        return inventoryTypeStringConverter;
    }

    private StringConverter<ReportNames> getReportNamesStringConverter(ObservableList<ReportNames> reportNamesObservableList) {
        StringConverter<ReportNames> reportNamesStringConverter = new StringConverter<ReportNames>() {
            @Override
            public String toString(ReportNames object) {
                return object.getName();
            }

            @Override
            public ReportNames fromString(String id) {
                return reportNamesObservableList.stream()
                        .filter(item -> item.getName().equals(id))
                        .collect(Collectors.toList()).get(0);
            }
        };
        return reportNamesStringConverter;
    }

    private StringConverter<RisFields> getRisFieldsStringConverter(ObservableList<RisFields> risFieldsObservableList) {
        StringConverter<RisFields> risFieldsStringConverter = new StringConverter<RisFields>() {
            @Override
            public String toString(RisFields object) {
                return object.getRis_field();
            }

            @Override
            public RisFields fromString(String id) {
                return risFieldsObservableList.stream()
                        .filter(item -> item.getRis_field().equals(id))
                        .collect(Collectors.toList()).get(0);
            }
        };
        return risFieldsStringConverter;
    }

    private StringConverter<RisTypeNames> getRisTypeNamesStringConverter(ObservableList<RisTypeNames> risTypeNamesObservableList) {
        StringConverter<RisTypeNames> risFieldsStringConverter = new StringConverter<RisTypeNames>() {
            @Override
            public String toString(RisTypeNames object) {
                return object.getName();
            }

            @Override
            public RisTypeNames fromString(String id) {
                return risTypeNamesObservableList.stream()
                        .filter(item -> item.getName().equals(id))
                        .collect(Collectors.toList()).get(0);
            }
        };
        return risFieldsStringConverter;
    }

    private StringConverter<RisType> getRisTypeStringConverter(ObservableList<RisType> risTypeObservableList) {
        StringConverter<RisType> risTypeStringConverter = new StringConverter<RisType>() {
            @Override
            public String toString(RisType object) {
                return object.getRisname();
            }

            @Override
            public RisType fromString(String id) {
                return risTypeObservableList.stream()
                        .filter(item -> item.getRisname().equals(id))
                        .collect(Collectors.toList()).get(0);
            }
        };
        return risTypeStringConverter;
    }
}
