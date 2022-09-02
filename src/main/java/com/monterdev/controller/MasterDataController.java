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
        setReportNamesComboBoxStringConverter(reportNamesList, tab6ComboReportName);
        setInventoryTypesComboBoxStringConverter(inventoryTypeList, tab4ComboInventoryType);
        setInventoryTypesComboBoxStringConverter(inventoryTypeList, tab2ComboInventoryType);
        setInventoryTypesComboBoxStringConverter(inventoryTypeList, tab5ComboInventoryType);
        setRisTypesNamesComboBoxStringConverter(risTypeNamesList, tab2ComboRisTypeName);
        setRisFieldsComboBoxStringConverter(risFieldsList, tab5ComboRisField);
        setRisTypesNamesComboBoxStringConverter(risTypeNamesList, tab5ComboRisType);

    }


    private void refreshMasterDataList() {
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
                    tab2ComboRisTypeName.setValue(cellData.get(0).getRis_type_name());
                    tab2JrxmlReportFileName.setText(cellData.get(0).getJrxml_report_file_name());
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
                    tab4TxtRisTypeName.setText(cellData.get(0).getRisname());
                    tab4ComboInventoryType.setValue(inventoryTypeList.stream().filter(e->e.getInventory_type().equalsIgnoreCase(cellData.get(0).getInventorytype())).findFirst().get());
                    tab4TxtRisName.setText(cellData.get(0).getName());
                    sku = cellData.get(0).getId();
                }
            });
        } else if (selectedTabPaneIndex == 4) {
            ObservableList<RisType> cellData = FXCollections.observableArrayList();
            tableResult.setOnMouseClicked(cell -> {
                if (cell.getClickCount() == 2) {
                    cellData.clear();
                    cellData.add((RisType) tableResult.getSelectionModel().getSelectedItem());
                    tab5ComboInventoryType.setValue(inventoryTypeList.stream().filter(e->e.getInventory_type().equalsIgnoreCase(cellData.get(0).getInventory_type())).findFirst().get());
                    tab5ComboRisType.setValue(risTypeList.stream().filter(x->x.getRistype().equalsIgnoreCase(cellData.get(0).getRistype())).findFirst().get());
                    tab5ComboRisField.setValue(risFieldsList.stream().filter(z->z.getRis_field().equalsIgnoreCase(cellData.get(0).getRisfield())).findFirst().get());
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
                    tab6ComboReportName.setValue(reportNamesList.stream().filter(e->e.getId()==cellData.get(0).getReportid()).findFirst().get());
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
        tableColumn.setStyle("-fx-alignment: center; -fx-font-size: 14px;");
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
                    tab2ComboInventoryType.setItems(FXCollections.observableArrayList(inventoryTypeList));
                    tab2ComboRisTypeName.setItems(FXCollections.observableArrayList(risTypeNamesList));
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
                    tab4ComboInventoryType.setItems(FXCollections.observableArrayList(inventoryTypeList));
                    labelList.setText("LIST OF EXISTING REQUISITION ISSUE SLIP TYPE NAMES");
                    setTableView(selectedTabPaneIndex, RisTypeNames.class);
                    refreshTableResult(risTypeNamesList);
                } else if (selectedTabPaneIndex == 4) {
                    refreshMasterDataList();
                    tab5ComboInventoryType.setItems(FXCollections.observableArrayList(inventoryTypeList));
                    tab5ComboRisType.setItems(FXCollections.observableArrayList(risTypeNamesList));
                    tab5ComboRisField.setItems(FXCollections.observableArrayList(risFieldsList));
                    labelList.setText("LIST OF EXISTING REQUISITION ISSUE SLIP TYPES");
                    setTableView(selectedTabPaneIndex, RisType.class);
                    refreshTableResult(risTypeList);
                } else if (selectedTabPaneIndex == 5) {
                    refreshMasterDataList();
                    tab6ComboReportName.setItems(FXCollections.observableArrayList(reportNamesList));
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
        tab2BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab2TxtReportName.getText()) && !ObjectUtils.isEmpty(tab2TxtDescription.getText())) {
                ReportNames reportNames = new ReportNames();
                reportNames.setId(sku);
                reportNames.setRis_type_name(((RisTypeNames) tab2ComboRisTypeName.getValue()).getName());
                reportNames.setJrxml_report_file_name(tab2JrxmlReportFileName.getText());
                reportNames.setName(tab2TxtReportName.getText());
                reportNames.setDescription(tab2TxtDescription.getText());
                reportNames.setInventorytype(((InventoryType) tab2ComboInventoryType.getValue()).getInventory_type());
                if (!ObjectUtils.isEmpty(reportNamesRepository.save(reportNames))) {
                    Prompt.success("Report name was successfully added!");
                    refreshMasterDataList();
                    refreshTableResult(reportNamesList);
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
                    refreshMasterDataList();
                    refreshTableResult(risFieldsList);
                } else {
                    Prompt.failed("Ris field was NOT saved!");
                }
            }
        });
    }

    private void tab4BtnSaveRisTypeNameOnAction() {
        tab4BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab4TxtRisName.getText()) && !ObjectUtils.isEmpty(tab4TxtRisTypeName.getText())) {
                RisTypeNames risTypeNames = new RisTypeNames();
                risTypeNames.setId(sku);
                risTypeNames.setInventorytype(((InventoryType) tab4ComboInventoryType.getValue()).getInventory_type());
                risTypeNames.setName(tab4TxtRisName.getText());
                risTypeNames.setRisname(tab4TxtRisTypeName.getText());
                if (!ObjectUtils.isEmpty(risTypeNamesRepository.save(risTypeNames))) {
                    Prompt.success("Ris Type name has been added successfully!");
                    refreshMasterDataList();
                    refreshTableResult(risTypeNamesList);
                } else {
                    Prompt.failed("Error saving RIS Type name!");
                }
            }
        });
    }

    private void tab5BtnSaveRisTypeOnAction() {
        List<String> risFieldTypeFormatList = Arrays.asList(getRisFieldTypes().split(","));
        risFieldTypeFormatList.stream().forEach(e -> {
            tab5ComboRisFieldType.getItems().add(e);
        });
        tab5BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab5TxtRisName.getText())) {
                RisType risType = new RisType();
                risType.setId(sku);
                risType.setInventory_type(((InventoryType) tab5ComboInventoryType.getValue()).getInventory_type());
                risType.setRistype(((RisTypeNames) tab5ComboRisType.getValue()).getName());
                risType.setRisfield(((RisFields) tab5ComboRisField.getValue()).getRis_field());
                risType.setRisfieldtype((String) tab5ComboRisFieldType.getSelectionModel().getSelectedItem());
                risType.setRisname(tab5TxtRisName.getText());
                if (!ObjectUtils.isEmpty(risTypeRepository.save(risType))) {
                    refreshMasterDataList();
                    refreshTableResult(risTypeList);
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

        tab6BtnSave.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(tab6TxtSignatoryName.getText()) && !ObjectUtils.isEmpty(tab6TxtPosition.getText())) {
                Signatory signatory = new Signatory();
                signatory.setId(sku);
                signatory.setSignatory(tab6TxtSignatoryName.getText());
                signatory.setPosition(tab6TxtPosition.getText());
                String reportName = ((ReportNames) tab6ComboReportName.getValue()).getId() + "-" + ((ReportNames) tab6ComboReportName.getValue()).getName() + "-" + ((ReportNames) tab6ComboReportName.getValue()).getDescription();
                signatory.setReportid(Integer.parseInt(reportName.split("-")[0]));
                signatory.setRole((String) tab6ComboRole.getSelectionModel().getSelectedItem());
                if (!ObjectUtils.isEmpty(signatoryRepository.save(signatory))) {
                    refreshMasterDataList();
                    refreshTableResult(signatoryList);
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
                    refreshMasterDataList();
                    refreshTableResult(unitList);
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

    private void setReportNamesComboBoxStringConverter(List<ReportNames> reportNamesList, JFXComboBox jfxComboBox) {
        ObservableList<ReportNames> observableList = FXCollections.observableArrayList(reportNamesList);
        jfxComboBox.setItems(observableList);
        StringConverter<ReportNames> stringConverter = getReportNamesStringConverter(observableList);
        jfxComboBox.setConverter(stringConverter);
    }

    private void setRisFieldsComboBoxStringConverter(List<RisFields> risFieldsList, JFXComboBox jfxComboBox) {
        ObservableList<RisFields> observableList = FXCollections.observableArrayList(risFieldsList);
        jfxComboBox.setItems(observableList);
        StringConverter<RisFields> stringConverter = getRisFieldsStringConverter(observableList);
        jfxComboBox.setConverter(stringConverter);
    }

    private void setInventoryTypesComboBoxStringConverter(List<InventoryType> inventoryTypeList, JFXComboBox jfxComboBox) {
        ObservableList<InventoryType> observableList = FXCollections.observableArrayList(inventoryTypeList);
        jfxComboBox.setItems(observableList);
        StringConverter<InventoryType> stringConverter = getInventoryTypeStringConverter(observableList);
        jfxComboBox.setConverter(stringConverter);
    }

    private void setRisTypesComboBoxStringConverter(List<RisType> risTypeList, JFXComboBox jfxComboBox) {
        ObservableList<RisType> observableList = FXCollections.observableArrayList(risTypeList);
        jfxComboBox.setItems(observableList);
        StringConverter<RisType> stringConverter = getRisTypeStringConverter(observableList);
        jfxComboBox.setConverter(stringConverter);
    }

    private void setRisTypesNamesComboBoxStringConverter(List<RisTypeNames> risTypeNamesList, JFXComboBox jfxComboBox) {
        ObservableList<RisTypeNames> observableList = FXCollections.observableArrayList(risTypeNamesList);
        jfxComboBox.setItems(observableList);
        StringConverter<RisTypeNames> stringConverter = getRisTypeNamesStringConverter(observableList);
        jfxComboBox.setConverter(stringConverter);
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
