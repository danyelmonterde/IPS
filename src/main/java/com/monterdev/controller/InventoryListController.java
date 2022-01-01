package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.DeletedItems;
import com.monterdev.model.Item;
import com.monterdev.model.ItemCategory;
import com.monterdev.repository.CategoryRepository;
import com.monterdev.repository.DeletedItemsRepository;
import com.monterdev.repository.ItemsRepository;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.monterdev.constants.GlobalConfiguration.defaultItemCategory;
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
    //END OF AUTOWIRED DEPENDENCIES
    ////////////////////////////////////////////////////


    //LOCAL VARIABLES BELOW HERE
    private ObservableList<Item> itemObservableList;
    private List<Item> itemList;
    private int numberOfRowsPerPage = 10; //Initial number of rows per page
    private int currentPage = 1;
    private int maximumPage = 1;
    private static final String ALL_STOCKS = "All STOCKS";
    private static final String LOW_STOCK_ALERT = "LOW STOCK";
    private static final String OUT_OF_STOCK_ALERT = "OUT OF STOCK";
    private Item selectedItem;
    private static String CURRENT_SELECTED_CATEGORY = "";
    private static String CURRENT_SELECTED_STOCK_ALERT = "";
    private static final String ALL_CATEGORIES = "ALL CATEGORIES";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAllInventoryItems();
        initializeNumberOfRowsPerPage();
        initializeItemCategories();
        initializeStockAlerts();
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
    }

    private void initializeStockAlerts() {
        stockAlertsCombo.getItems().add(ALL_STOCKS);
        stockAlertsCombo.getItems().add(LOW_STOCK_ALERT);
        stockAlertsCombo.getItems().add(OUT_OF_STOCK_ALERT);
    }

    private void initializeItemCategories() {
        Iterable<ItemCategory> itemCategories = categoryRepository.findAll();
        itemCategories.forEach(category -> {
            itemCategoriesCombo.getItems().add(category.getCategory_name());
        });
    }

    private void initializeNumberOfRowsPerPage() {
        rowsPerPageCombo.getItems().add(10);
        rowsPerPageCombo.getItems().add(25);
        rowsPerPageCombo.getItems().add(50);
        rowsPerPageCombo.getItems().add(100);
        rowsPerPageCombo.getItems().add(1000);
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
        currentPage = 1;
        setTableColumnNames();
        loadTableViewWithPaginationAndItemName((currentPage - 1), 10, where(hasItemNameLike("")));
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
                selectedItem.setTag(cellData.get(0).getTag());
                selectedItem.setCost(cellData.get(0).getCost());
                selectedItem.setItem_name(cellData.get(0).getItem_name());
                selectedItem.setLow_stock(cellData.get(0).getLow_stock());
                selectedItem.setIn_stock(cellData.get(0).getIn_stock());
                selectedItem.setSku(cellData.get(0).getSku());
                selectedItem.setIn_stock(cellData.get(0).getIn_stock());
                selectedItem.setItem_category(cellData.get(0).getItem_category());
                selectedItem.setUnit(cellData.get(0).getUnit());
                new StageLoader().load(EditItemController.class, cell, applicationContext, "");
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

//    private Specification<Item> hasItemName(String itemName) {
//        return (item, cq, cb) -> cb.equal(item.get("item_name"), itemName);
//    }

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
                loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike("")));
            }
        });
    }

    private void currentPageOnChange() {
        currentPageTextField.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                if (StringUtils.isNumeric(nv)) {
                    currentPage = Integer.parseInt(nv);
                    if (currentPage <= maximumPage) {
                        loadTableViewWithPaginationAndItemName((currentPage - 1), numberOfRowsPerPage, where(hasItemNameLike("")));
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
            }
        });
    }

    private void previousPageOnClick() {
        previousPageButton.setOnAction(onClick -> {
            if (!((currentPage - 1) == 0)) {
                currentPage--;
                currentPageTextField.setText(String.valueOf(currentPage));
            }
        });
    }

    private void searchItemOnType() {
        searchItemTextField.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike(nv)));
            }
        });
    }

    private void stockAlertsOnClick() {
        stockAlertsCombo.valueProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                CURRENT_SELECTED_STOCK_ALERT = (String) nv;
                loadTableViewFilteredByStockAlerts(CURRENT_SELECTED_STOCK_ALERT);
            }
        });
    }

    private void loadTableViewFilteredByStockAlerts(String selectedStockAlert) {
        if (selectedStockAlert.equalsIgnoreCase(ALL_STOCKS)) {
            loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike("").and(hasCategoryLike(CURRENT_SELECTED_CATEGORY))));
        } else if (selectedStockAlert.equalsIgnoreCase(LOW_STOCK_ALERT)) {
            loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike("").and(hasCategoryLike(CURRENT_SELECTED_CATEGORY)).and(lowStockEqualsToInStock())));
        } else if (selectedStockAlert.equalsIgnoreCase(OUT_OF_STOCK_ALERT)) {
            loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike("").and(hasCategoryLike(CURRENT_SELECTED_CATEGORY)).and(outOfStockItems())));
        }
    }

    private void itemCategoryOnClick() {
        itemCategoriesCombo.valueProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                String category = (String) nv;
                if (category.equalsIgnoreCase(ALL_CATEGORIES)) {
                    CURRENT_SELECTED_CATEGORY = "";
                    loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasItemNameLike("")));
                } else {
                    CURRENT_SELECTED_CATEGORY = category;
                    loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, where(hasCategoryLike(CURRENT_SELECTED_CATEGORY)));
                }
            }
        });
    }

    private void exportItemsOnClick() {
        exportItemButton.setOnAction(onClick -> {
            if (Prompt.confirm("Are you sure you want to export all list of items?").get().getText().equalsIgnoreCase("OK")) {
                Stage stage = (Stage) exportItemButton.getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Comma-Separated Values (*.csv)","*.csv");
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
                        Prompt.success("Success! Items backup was saved to "+file.getAbsolutePath());

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
                            itemsRepository.deleteAll();
                            if (!ObjectUtils.isEmpty(itemsRepository.saveAll(importedItems))) {
                                Prompt.success("Imported items we're successfully added!");
                            } else {
                                Prompt.failed("An error occurred while importing data!");
                            }

                        } else {
                            System.out.println("no");
                        }
                    }

                } catch (IOException exception) {

                } catch (Exception exception) {

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
            new StageLoader().load(AddItemController.class, applicationContext);
        });
    }


}
