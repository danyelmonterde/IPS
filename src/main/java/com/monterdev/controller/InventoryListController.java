package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.constants.InventoryTypeConstants;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.*;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.monterdev.configuration.GlobalConfiguration.*;
import static com.monterdev.constants.InventoryTypeConstants.ALL_CATEGORIES;
import static com.monterdev.configuration.ItemsUIConfiguration.stockAlerts;
import static com.monterdev.util.QrCodeUtil.saveQrCode;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
@FxmlView("InventoryList.fxml")
@Getter
@Setter
public class InventoryListController implements Initializable {

    /////////////////////////////////////////////
    /////////////////////////////////////////////
    //UI COMPONENTS STARTS HERE
    @FXML
    private TableView<Item> inventoryListTableView;
    @FXML
    private Label ofLabel;
    @FXML
    private Label currentDateLabel;
    @FXML
    private Label labelUsername;
    @FXML
    private Label lowStockIndicator;
    @FXML
    private Label outOfStockIndicator;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label outOfStockLabel;
    @FXML
    private JFXTextField searchItemTextField;
    @FXML
    private JFXTextField currentPageTextField;
    @FXML
    private JFXComboBox stockAlertsCombo;
    @FXML
    private JFXComboBox rowsPerPageCombo;
    @FXML
    private JFXComboBox itemCategoriesCombo;
    @FXML
    private JFXButton previousPageButton;
    @FXML
    private JFXButton nextPageButton;
    @FXML
    private JFXButton importItemButton;
    @FXML
    private JFXButton exportItemButton;
    @FXML
    private JFXButton addItemButton;
    @FXML
    private TableColumn<Item, String> sku;
    @FXML
    private TableColumn<Item, String> itemName;
    @FXML
    private TableColumn<Item, String> itemCategory;
    @FXML
    private TableColumn<Item, String> itemCost;
    @FXML
    private TableColumn<Item, String> inStock;
    @FXML
    private JFXButton btnRefreshTable;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnHistory;
    @FXML
    private JFXButton btnStockAdjustment;
    @FXML
    private JFXButton btnOverview;
    @FXML
    private Circle lowStockCircle;
    @FXML
    private Circle outOfStockCircle;

    ////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////
    //START OF AUTOWIRED DEPENDENCIES
    @Autowired
    private ItemsRepository itemsRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EditItemController editItemController;
    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private DeletedItemsRepository deletedItemsRepository;
    @Autowired
    private QrcodeRepository qrcodeRepository;
    @Autowired
    private InventoryListUtil inventoryListUtil;
    @Autowired
    private User user;
    @Autowired
    private BalanceRepository balanceRepository;
    //END OF AUTOWIRED DEPENDENCIES
    ////////////////////////////////////////////////////


    //LOCAL VARIABLES BELOW HERE
    private ObservableList<Item> itemObservableList;
    private ObservableList<ItemCategory> categoryObservableList;
    private List<Item> itemList;
    private int numberOfRowsPerPage = 10; //Initial number of rows per page
    private int currentPage = 1;
    private int maximumPage = 1;
    private Item selectedItem;
    private static String CURRENT_SELECTED_CATEGORY = "";
    private static String CURRENT_SELECTED_STOCK_ALERT = "";
    private List<String> stockAlertLists = new ArrayList<>();


    private ScheduledExecutorService currentTimeUpdater;
    private ScheduledExecutorService lowStockAndOutOfStockNotifier;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeStockAlerts();
        createComboBoxStringConverters();
        loadAllInventoryItems();
        initializeNumberOfRowsPerPage();
        initializeItemCategories();
        addItemOnClick();
        importItemOnClick();
        exportItemsOnClick();
        itemCategoryOnClick();
        stockAlertsOnClick();
        searchItemOnType();
        previousPageOnClick();
        nextPageOnClick();
        currentPageOnChange();
        rowsPerPageOnChange();
        ofLabelOnChange();
        btnRefreshTableOnClick();
        btnCloseOnClick();
        initializeDateLabel();
        initializeUsername();
        btnHistoryOnClick();
        btnAdjustmentOnClick();
        btnOverviewOnClick();
        progressBarOnLoad();
        indicatorsOnLoad();
        btnStockAdjustmentOnAction();
    }

    private void btnStockAdjustmentOnAction() {
        btnStockAdjustment.setOnAction(e->{
            Stage newStage = new Stage();
            new StageLoader().load(StockAdjustmentController.class, applicationContext, newStage);
        });
    }

    private void indicatorsOnLoad() {
        lowStockCircle.visibleProperty().addListener((observable, oldValue, newValue) -> {

        });
        outOfStockCircle.visibleProperty().addListener((observable, oldValue, newValue) -> {

        });
        lowStockLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {

        });
        outOfStockLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {

        });
        lowStockIndicator.visibleProperty().addListener((observable, oldValue, newValue) -> {

        });
        outOfStockIndicator.visibleProperty().addListener((observable, oldValue, newValue) -> {

        });
        lowStockCircle.setVisible(false);
        outOfStockCircle.setVisible(false);
        lowStockLabel.setVisible(false);
        outOfStockLabel.setVisible(false);
    }

    private void progressBarOnLoad() {
    }

    private void btnOverviewOnClick() {
    }

    private void btnAdjustmentOnClick() {
    }

    private void btnHistoryOnClick() {
        btnHistory.setOnAction(e->{
            Stage newStage = new Stage();
            Stage stage2 = (Stage) btnHistory.getScene().getWindow();
            new StageLoader().load(InventoryHistoryController.class, applicationContext, newStage);
            stage2.close();
        });
    }

    private void initializeUsername() {
    }

    private void createComboBoxStringConverters() {
        itemCategoriesCombo.setConverter(new StringConverter<ItemCategory>() {

            @Override
            public String toString(ItemCategory object) {
                return !ObjectUtils.isEmpty(object.getCategory_name()) ? object.getCategory_name() : null;
            }

            @Override
            public ItemCategory fromString(String string) {
                return null;
            }
        });
    }

    private void btnRefreshTableOnClick() {
        btnRefreshTable.setOnAction(e -> {
            loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
        });
    }

    private void btnCloseOnClick() {
        btnClose.setOnAction(e -> {
            resetTable();
            Stage currentStage = (Stage) btnClose.getScene().getWindow();
            new StageLoader().load(MainDashboardController.class, applicationContext, currentStage);
        });
    }

    private void resetTable() {
        stockAlertsCombo.setValue(stockAlertLists.get(0));
        CURRENT_SELECTED_CATEGORY = "";
        numberOfRowsPerPage = 10;
        searchItemTextField.setText("");
        currentPage = 1;
        maximumPage = 1;
    }

    private void initializeDateLabel() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyy");
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("hh:mm a");
        currentDateLabel.textProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != newValue) {

            }
        });

        Runnable timeUpdater = () -> Platform.runLater(() -> {
            LocalDateTime localDateTime = AppTime.now();
            currentDateLabel.setText(localDateTime.format(formatter) + " " + localDateTime.format(formatter1));
            loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
        });

        Runnable stockNotifier = () -> Platform.runLater(() -> {
            int numberOfLowStocks = itemsRepository.getLowStockItems();
            int numberOfOutOfStock = itemsRepository.getOutOfStockItems();
            if (numberOfLowStocks > 0) {
                lowStockIndicator.setText(String.valueOf(numberOfLowStocks));
                lowStockIndicator.setVisible(true);
                lowStockLabel.setVisible(true);
                lowStockCircle.setVisible(true);
            } else {
                lowStockIndicator.setText("0");
                lowStockIndicator.setVisible(false);
                lowStockLabel.setVisible(false);
                lowStockCircle.setVisible(false);
            }

            if (numberOfOutOfStock > 0) {
                outOfStockIndicator.setText(String.valueOf(numberOfOutOfStock));
                outOfStockLabel.setVisible(true);
                outOfStockIndicator.setVisible(true);
                outOfStockCircle.setVisible(true);
            } else {
                outOfStockIndicator.setText("0");
                outOfStockLabel.setVisible(false);
                outOfStockIndicator.setVisible(false);
                outOfStockCircle.setVisible(false);
            }
        });

        this.currentTimeUpdater = Executors.newSingleThreadScheduledExecutor();
        this.currentTimeUpdater.scheduleAtFixedRate(timeUpdater, 0, 60, TimeUnit.SECONDS);

        this.lowStockAndOutOfStockNotifier = Executors.newSingleThreadScheduledExecutor();
        this.lowStockAndOutOfStockNotifier.scheduleAtFixedRate(stockNotifier, 0, 1, TimeUnit.SECONDS);

    }

    private void initializeStockAlerts() {
        labelUsername.setText(user.getUsername());
        stockAlertLists = stockAlerts();
        stockAlertLists.stream().forEach(e -> {
            stockAlertsCombo.getItems().add(e);
        });

        stockAlertsCombo.setValue(stockAlertLists.get(0));
        CURRENT_SELECTED_STOCK_ALERT = stockAlertLists.get(0);
    }

    private void initializeItemCategories() {
        List<ItemCategory> itemCategories = categoryRepository.findAll();
        categoryObservableList = FXCollections.observableArrayList(itemCategories);
        itemCategoriesCombo.getItems().setAll(categoryObservableList);
    }

    private void initializeNumberOfRowsPerPage() {
        rowsPerPageCombo.getItems().add(10);
        rowsPerPageCombo.getItems().add(25);
        rowsPerPageCombo.getItems().add(50);
        rowsPerPageCombo.getItems().add(100);
        rowsPerPageCombo.getItems().add(1000);
        rowsPerPageCombo.getSelectionModel().select(0);
    }

    private void ofLabelOnChange() {
        ofLabel.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                if (StringUtils.isNumeric(nv)) {

                }
            }
        });
    }

    private void loadAllInventoryItems() {
        //Initial Loading of All Inventory Items
        currentPage = !ObjectUtils.isEmpty(currentPageTextField.getText()) ? Integer.parseInt(currentPageTextField.getText()) : currentPage;
        setTableColumnNames();
        loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
        currentPageTextField.setText(String.valueOf(currentPage));
    }

    private void loadTableViewWithPaginationAndItemName(int startingPage, int numberOfRowsPerPage, Specification specification) {
        Pageable initialItemPageList = PageRequest.of(startingPage, numberOfRowsPerPage);
        Page<Item> initialItemPage = itemsRepository.findAll(specification, initialItemPageList);
        itemList = initialItemPage.getContent();
        itemObservableList = FXCollections.observableArrayList(itemList);
        inventoryListTableView.getItems().setAll(itemObservableList);
        maximumPage = initialItemPage.getTotalPages();
        ofLabel.setText(String.valueOf(maximumPage));
        tableRowOnClick();
    }


    private void tableRowOnClick() {
        ObservableList<Item> cellData = FXCollections.observableArrayList();
        inventoryListTableView.setOnMouseClicked(cell -> {
            if (cell.getClickCount() == 2) {
                cellData.clear();
                cellData.add(inventoryListTableView.getSelectionModel().getSelectedItem());
                selectedItem = applicationContext.getBean(Item.class);
                selectedItem.setTag(String.valueOf(1));
                selectedItem.setCost(cellData.get(0).getCost());
                selectedItem.setItem_name(cellData.get(0).getItem_name());
                selectedItem.setLow_stock(cellData.get(0).getLow_stock());
                selectedItem.setIn_stock(cellData.get(0).getIn_stock());
                selectedItem.setSku(cellData.get(0).getSku());
                selectedItem.setIn_stock(cellData.get(0).getIn_stock());
                selectedItem.setItem_category(cellData.get(0).getItem_category());
                selectedItem.setUnit(cellData.get(0).getUnit());
                resetTable();
                Stage stage = (Stage) exportItemButton.getScene().getWindow();
                new StageLoader().load(EditItemController.class, cell, applicationContext, stage);
            }
        });
    }


    private void setTableColumnNames() {
        sku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        itemName.setCellValueFactory(new PropertyValueFactory<>("item_name"));
        itemCategory.setCellValueFactory(new PropertyValueFactory<>("item_category"));
        itemCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        inStock.setCellValueFactory(new PropertyValueFactory<>("in_stock"));
    }


    private Specification<Item> hasItemNameLike(String itemName) {
        return (item, cq, cb) -> cb.like(item.get("item_name"), "%" + itemName + "%");
    }

    private Specification<Item> lowStockEqualsToInStock() {
        return (item, cq, cb) -> cb.equal(item.get("low_stock"), item.get("in_stock"));
    }

    private Specification<Item> hasCategoryLike(String itemCategory) {
        return (item, cq, cb) -> cb.like(item.get("item_category"), "%" + itemCategory + "%");
    }

    private Specification<Item> outOfStockItems() {
        return (item, cq, cb) -> cb.equal(item.get("in_stock"), 0);
    }

    private void rowsPerPageOnChange() {
        rowsPerPageCombo.valueProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                numberOfRowsPerPage = (int) nv;
                loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
            }
        });
    }

    private void currentPageOnChange() {
        currentPageTextField.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                if (StringUtils.isNumeric(nv)) {
                    currentPage = Integer.parseInt(nv);
                    if (currentPage <= maximumPage) {
                        loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
                    }
                }
            }
        });
    }

    private void nextPageOnClick() {
        nextPageButton.setOnAction(onClick -> {
            if (!((currentPage + 1) > maximumPage)) {
                currentPage++;
                currentPageTextField.setText(String.valueOf(currentPage));
                loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
            }
        });
    }

    private void previousPageOnClick() {
        previousPageButton.setOnAction(onClick -> {
            if (!((currentPage - 1) == 0)) {
                currentPage--;
                currentPageTextField.setText(String.valueOf(currentPage));
                loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
            }
        });
    }

    private void searchItemOnType() {
        searchItemTextField.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
            }
        });
    }

    private void stockAlertsOnClick() {
        stockAlertsCombo.valueProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                CURRENT_SELECTED_STOCK_ALERT = (String) nv;
                loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
            }
        });
    }

    private void loadTableViewFilteredByStockAlertsAndCategoryAndItemName(String selectedStockAlert, String itemToSearch) {
        if (selectedStockAlert.equalsIgnoreCase(stockAlertLists.get(0))) {
            loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike(itemToSearch).and(hasCategoryLike(CURRENT_SELECTED_CATEGORY))));
        } else if (selectedStockAlert.equalsIgnoreCase(stockAlertLists.get(1))) {
            loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike(itemToSearch).and(hasCategoryLike(CURRENT_SELECTED_CATEGORY)).and(lowStockEqualsToInStock())));
        } else if (selectedStockAlert.equalsIgnoreCase(stockAlertLists.get(2))) {
            loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike(itemToSearch).and(hasCategoryLike(CURRENT_SELECTED_CATEGORY)).and(outOfStockItems())));
        }
    }

    private void itemCategoryOnClick() {
        itemCategoriesCombo.valueProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                ItemCategory category = (ItemCategory) nv;
                if (!ObjectUtils.isEmpty(category)) {
                    if (category.getCategory_name().equalsIgnoreCase(ALL_CATEGORIES)) {
                        CURRENT_SELECTED_CATEGORY = "";
                        loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
                    } else {
                        CURRENT_SELECTED_CATEGORY = category.getCategory_name();
                        loadTableViewFilteredByStockAlertsAndCategoryAndItemName(CURRENT_SELECTED_STOCK_ALERT, searchItemTextField.getText());
                    }
                }

            }
        });
    }

    private void exportItemsOnClick() {
        exportItemButton.setOnAction(onClick -> {
            if (Prompt.confirm("Are you sure you want to export all list of items?").get().getText().equalsIgnoreCase("OK")) {
                Stage stage = (Stage) exportItemButton.getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Comma-Separated Values (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extensionFilter);
                fileChooser.setTitle("Export Item List to CSV File");
                File file = fileChooser.showSaveDialog(stage);
                List<Item> itemList = itemsRepository.findAll();
                if (file != null) {
                    try {
                        Writer writer = new FileWriter(file.getAbsolutePath());
                        StatefulBeanToCsv statefulBeanToCsv = new StatefulBeanToCsvBuilder(writer)
                                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                                .build();
                        statefulBeanToCsv.write(itemList);
                        writer.close();
                        Prompt.success("Success! Items backup was saved to " + file.getAbsolutePath());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (CsvRequiredFieldEmptyException e) {
                        e.printStackTrace();
                    } catch (CsvDataTypeMismatchException e) {
                        e.printStackTrace();
                    } finally {
                    }

                }
            }
        });
    }

    @Transactional
    private void importItemOnClick() {
        importItemButton.setOnAction(onClick -> {
            Stage stage = (Stage) importItemButton.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import CSV to Database");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    List<Item> importedItems = new CsvToBeanBuilder(new FileReader(file))
                            .withType(Item.class)
                            .build().parse();
                    if (!ObjectUtils.isEmpty(importedItems)) {
                        if (Prompt.confirm("Are you sure you want to import this file? Existing items will be deleted.").get().getText().equalsIgnoreCase("OK")) {
                            deletedItemsRepository.truncateDeletedItems();
                            List<Item> existingItems = itemsRepository.findAll();
                            existingItems.stream().forEach(existingItem -> {
                                DeletedItems deletedItems = new DeletedItems();
                                deletedItems.setItem_category(existingItem.getItem_category());
                                deletedItems.setCost(existingItem.getCost());
                                deletedItems.setQuantity(existingItem.getQuantity());
                                deletedItems.setSku(existingItem.getSku());
                                deletedItems.setIn_stock(existingItem.getIn_stock());
                                deletedItems.setLow_stock(existingItem.getLow_stock());
                                deletedItems.setTag(existingItem.getTag());
                                deletedItems.setItem_name(existingItem.getItem_name());
                                deletedItems.setUnit(existingItem.getUnit());
                                deletedItemsRepository.save(deletedItems);
                            });
                            itemsRepository.truncateItems();
                          //  List<Item> newlyAddedItemList = itemsRepository.saveAll(importedItems);
                              List<Item> newlyAddedItemList = itemsRepository.saveAll(DataUtil.convertIQWDInventoryToItemList());
                            Balance balance = new Balance();
                            categoryObservableList.stream().forEach(e->{
                                Balance bal = balanceRepository.getBalanceIdOfCurrentInventoryMonth(AppTime.getMonth(),String.valueOf(AppTime.getYear()),e.getCategory_name());
                                if(!(e.getCategory_name().equalsIgnoreCase(ALL_CATEGORIES))){
                                    if(!ObjectUtils.isEmpty(bal)){
                                        balance.setYear(String.valueOf(AppTime.getYear()));
                                        balance.setMonth(AppTime.getMonth());
                                        balance.setDatecreated(String.valueOf(AppTime.now()));
                                        balance.setId(bal.getId());
                                        balance.setBeginbalance(bal.getBeginbalance());
                                        balance.setCategory(bal.getCategory());
                                        String x = itemsRepository.getEndBalanceByInventoryType(e.getCategory_name());
                                        balance.setEndbalance(ObjectUtils.isEmpty(x)?0:DataUtil.formatDouble(x));
                                        balanceRepository.save(balance);//lagay sa loop
                                    }else{
                                        balance.setYear(String.valueOf(AppTime.getYear()));
                                        balance.setMonth(AppTime.getMonth());
                                        balance.setDatecreated(String.valueOf(AppTime.now()));
                                        balance.setId(0);
                                        String b = itemsRepository.getEndBalanceByInventoryType(e.getCategory_name());
                                        balance.setBeginbalance(ObjectUtils.isEmpty(b)?0: DataUtil.formatDouble(b));
                                        balance.setCategory(e.getCategory_name());
                                        balance.setEndbalance(ObjectUtils.isEmpty(b)?0: DataUtil.formatDouble(b));
                                        balanceRepository.save(balance);//lagay sa loop
                                    }
                                }

                            });



                            if (!ObjectUtils.isEmpty(newlyAddedItemList)) {
                                qrcodeRepository.truncateQrCodes();
                                newlyAddedItemList.stream().forEach(e -> {
                                    Qrcode qrcode = new Qrcode();
                                    qrcode.setSku(e.getSku());
                                    qrcode.setQr_code_path(getConfigValue(generatedQrCodeDirectory) + "\\" + e.getSku());
                                    qrcode.setDate_created(AppTime.now());
                                    qrcodeRepository.save(qrcode);
                                    try {
                                        saveQrCode(String.valueOf(e.getSku()), String.valueOf(e.getSku()));
                                    } catch (Exception ex) {

                                    }
                                });

                            } else {
                                Prompt.failed("An error occurred while importing data!");
                            }

                        } else {
                            System.out.println("no");
                        }
                    }

                } catch (IOException exception) {
                    System.out.println(exception.getMessage());
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
        });
    }

    private void addItemOnClick() {
        addItemButton.setOnAction(onClick -> {
            selectedItem = applicationContext.getBean(Item.class);
            selectedItem.setTag(null);
            selectedItem.setItem_name("");
            selectedItem.setLow_stock(0);
            selectedItem.setQuantity(0);
            selectedItem.setSku(0);
            selectedItem.setCost(0.0);
            selectedItem.setIn_stock(0);
            selectedItem.setItem_category(defaultItemCategory);
            Stage stage = (Stage) exportItemButton.getScene().getWindow();
            new StageLoader().load(AddItemController.class, applicationContext, stage);
        });
    }


}
