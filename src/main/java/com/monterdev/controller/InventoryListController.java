package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.constants.GlobalConfiguration;
import com.monterdev.model.Item;
import com.monterdev.model.ItemCategory;
import com.monterdev.repository.CategoryRepository;
import com.monterdev.repository.ItemsRepository;
import com.monterdev.util.StageLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

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
        loadTableViewWithPaginationAndItemName((currentPage - 1), 10, "");//Initial number of Rows is 10 on the first page
        currentPageTextField.setText(String.valueOf(currentPage));
    }

    private void loadTableViewWithPaginationAndItemName(int startingPage, int numberOfRowsPerPage, String itemName) {
        Pageable initialItemPageList = PageRequest.of(startingPage, numberOfRowsPerPage);
        Page<Item> initialItemPage = itemsRepository.findAll(where(itemNameContains(itemName)), initialItemPageList);
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

    private Specification<Item> itemNameContains(String itemName) {
        return (item, cq, cb) -> cb.like(item.get("item_name"), "%" + itemName + "%");
    }

    private void rowsPerPageOnChange() {
        rowsPerPageCombo.valueProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                numberOfRowsPerPage = (int) nv;
                loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, searchItemTextField.getText());
            }
        });
    }

    private void currentPageOnChange() {
        currentPageTextField.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                if (StringUtils.isNumeric(nv)) {
                    currentPage = Integer.parseInt(nv);
                    if (currentPage <= maximumPage) {
                        loadTableViewWithPaginationAndItemName((currentPage - 1), numberOfRowsPerPage, searchItemTextField.getText());
                    }
                }
            }
        });
    }

    private void nextPageOnClick() {
        nextPageButton.setOnAction(onClick -> {
            if(!((currentPage+1)>maximumPage)){
                currentPage++;
                currentPageTextField.setText(String.valueOf(currentPage));
            }
        });
    }

    private void previousPageOnClick() {
        previousPageButton.setOnAction(onClick -> {
            if(!((currentPage-1)==0)){
                currentPage--;
                currentPageTextField.setText(String.valueOf(currentPage));
            }
        });
    }

    private void searchItemOnType() {
        searchItemTextField.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                loadTableViewWithPaginationAndItemName(currentPage - 1, numberOfRowsPerPage, nv);
            }
        });
    }

    private void stockAlertsOnClick() {
    }

    private void itemCategoryOnClick() {
    }

    private void exportItemsOnClick() {
    }

    private void importItemOnClick() {
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
            new StageLoader().load(AddItemController.class,applicationContext);
        });
    }


}
