package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.DeletedItems;
import com.monterdev.model.Item;
import com.monterdev.util.StageLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.monterdev.constants.ItemsUIConfiguration.*;
import static com.monterdev.util.ComponentCreator.*;

public class ItemListController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vBox;

    private static int row = 1;

    private  TableView<Item> tableView = new TableView<>();

    private  ObservableList<Item> itemObservableList;

    private static int maxSku = 0;

    private static int rowsPerPageCombo = 10;

    private static int dataSize = 0;

    private static int footerOffValue = 1;

    private static int currentPageValue = 1;


    @Autowired
    @Qualifier("itemLists")
    private List<Item> itemLists;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private EditItemController editItemController;

    private Label ofPageLabel = new Label();

    private Item selectedItem;

    public AnchorPane createItemList() {

        anchorPane = new AnchorPane();

        vBox = new VBox();

        //INITIAL HBOX CONFIGURATION
        HBox topHbox = new HBox();
        topHbox.setAlignment(Pos.TOP_CENTER);
        topHbox.setSpacing(Double.parseDouble(getItemsTopHboxSpacing()));

        HBox midHbox = new HBox();
        midHbox.setAlignment(Pos.TOP_LEFT);
        midHbox.setSpacing(Double.parseDouble(getItemsTopHboxSpacing()));


        HBox botHbox = new HBox();
        botHbox.setAlignment(Pos.TOP_LEFT);
        botHbox.setSpacing(Double.parseDouble(getItemsTopHboxSpacing()));

        //TOP HBOX

        JFXButton addItem = addJFXButton(ITEMS_ADD_ITEM_BUTTON_NAME, JFXButton.ButtonType.RAISED);
        JFXButton importButton = addJFXButton(ITEMS_IMPORT_BUTTON_NAME, JFXButton.ButtonType.FLAT);
        JFXButton export = addJFXButton(ITEMS_EXPORT_BUTTON_NAME, JFXButton.ButtonType.FLAT);
        JFXButton settings = addJFXButton(ITEMS_SETTINGS_BUTTON_NAME, JFXButton.ButtonType.FLAT);

        addItemFunction(addItem);

        Label categoryLabel = addLabel(ITEMS_LABEL_CATEGORY_NAME);
        Label stockAlertLabel = addLabel(ITEMS_LABEL_STOCKALERT_NAME);
        JFXComboBox categoryCombobox = addCombobox(ITEMS_DROP_DOWN_CLASS, fillStockAlert());
        JFXComboBox stockAlertCombobox = addCombobox(ITEMS_DROP_DOWN_CLASS, fillStockAlert());
        VBox categoryVbox = addVbox(categoryLabel, categoryCombobox);
        VBox stockAlertVbox = addVbox(stockAlertLabel, stockAlertCombobox);


        //MID HBOX
        createTableView();

        midHbox.getChildren().addAll(this.tableView);
        HBox.setHgrow(tableView, Priority.ALWAYS);

        //BOT HBOX
        JFXButton previousButton = addJFXButton(ITEMS_PREV_BUTTON_NAME, JFXButton.ButtonType.FLAT);
        JFXButton nextButton = addJFXButton(ITEMS_NEXT_BUTTON_NAME, JFXButton.ButtonType.FLAT);

        Label pageLabel = addLabel(ITEMS_DATABLE_FOOTER_LABELS, ITEMS_BOT_HBOX_FOOTER_PAGE_LABEL);
        JFXTextField currentPage = addTextFieldWithPadding("", Pos.CENTER);
        currentPage.setText(Integer.toString(currentPageValue));
        setOfPageLabel();
        ofPageLabel = addLabel(ITEMS_DATABLE_FOOTER_LABELS, "of " + footerOffValue);


        previousButtonAction(previousButton, currentPage);

        nextButtonAction(nextButton, currentPage);

        currentPageAction(currentPage);


        Label rowsPerPage = addLabel(ITEMS_DATABLE_FOOTER_LABELS, ITEMS_BOT_HBOX_FOOTER_ROWS_PER_PAGE_LABEL);
        JFXComboBox rowsPerPageComboBox = addCombobox("", fillRowsperPage());
        rowsPerPageAction(currentPage, rowsPerPageComboBox);
        botHbox.getChildren().addAll(previousButton, nextButton, pageLabel, currentPage, ofPageLabel, rowsPerPage, rowsPerPageComboBox);


        anchorPane.getChildren().add(vBox);
        topHbox.getChildren().addAll(addItem, importButton, export, settings, categoryVbox, stockAlertVbox);
        vBox.getChildren().addAll(topHbox, midHbox, botHbox);

        setAlignment(topHbox, midHbox, botHbox);

        return anchorPane;
    }

    private void addItemFunction(JFXButton addItem) {
        addItem.setOnAction(e -> {
            selectedItem = applicationContext.getBean(Item.class);
            selectedItem.setTag(null);
            selectedItem.setItem_name("");
            selectedItem.setLow_stock(0);
            selectedItem.setQuantity(0);
            selectedItem.setSku(0);
            selectedItem.setMargin(0.0);
            selectedItem.setCost(0.0);
            selectedItem.setIn_stock(0);
            selectedItem.setSub_category_detail(getConfigValue(defaultCategoryData));
            editItemController.create();
        });
    }

    private void setOfPageLabel() {
        ofPageLabel.textProperty().addListener((observableValue, oldValue, newValue) -> {

        });
    }

    private void rowsPerPageAction(JFXTextField currentPage, JFXComboBox rowsPerPageComboBox) {
        rowsPerPageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            int oldRowsperPage = Integer.parseInt(oldValue.toString());
            int newRowsperPage = Integer.parseInt(newValue.toString());
            boolean isNewRowsperPageValid = ((newRowsperPage * currentPageValue) <= dataSize) ? true : false;
            if ((oldRowsperPage != newRowsperPage) && (footerOffValue > currentPageValue) && (newRowsperPage > oldRowsperPage) && isNewRowsperPageValid) {
                rowsPerPageCombo = Integer.parseInt(newValue.toString());
                try {
                    refreshTableAndIsLastPage(false);
                    setFooterOffValue();
                    ofPageLabel.setText("of " + footerOffValue);
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                } catch (NumberFormatException numberFormatException) {

                }

            } else if ((footerOffValue == currentPageValue) && isNewRowsperPageValid) {
                rowsPerPageCombo = Integer.parseInt(newValue.toString());
                currentPage.setText("1");
                currentPageValue = 1;
                itemObservableList = FXCollections.observableArrayList(itemLists.subList(0, (1 * rowsPerPageCombo)));
                setFooterOffValue();
                ofPageLabel.setText("of " + footerOffValue);
                tableView.getItems().setAll(itemObservableList);
            } else if (currentPageValue < footerOffValue && oldRowsperPage > newRowsperPage && isNewRowsperPageValid) {
                rowsPerPageCombo = Integer.parseInt(newValue.toString());
                itemObservableList = FXCollections.observableArrayList(itemLists.subList(currentPageValue * rowsPerPageCombo - rowsPerPageCombo, (currentPageValue * rowsPerPageCombo)));
                setFooterOffValue();
                ofPageLabel.setText("of " + footerOffValue);
                tableView.getItems().setAll(itemObservableList);
            }
        });
    }

    private void currentPageAction(JFXTextField currentPage) {
        currentPage.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentPageValue = Integer.parseInt(newValue);
                if (oldValue != newValue && (Integer.parseInt(newValue) > 1) && footerOffValue > currentPageValue) {
                    try {
                        itemObservableList = FXCollections.observableArrayList(itemLists.subList(currentPageValue * rowsPerPageCombo - rowsPerPageCombo, (currentPageValue * rowsPerPageCombo)));
                        tableView.getItems().setAll(itemObservableList);
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                    }

                } else if (oldValue != newValue && (Integer.parseInt(newValue) == 1)) {
                    currentPageValue = Integer.parseInt(newValue);
                    itemObservableList = FXCollections.observableArrayList(itemLists.subList(0, rowsPerPageCombo));
                    tableView.getItems().setAll(itemObservableList);
                } else if ((oldValue != newValue) && (footerOffValue == currentPageValue)) {
                    try {
                        itemObservableList = FXCollections.observableArrayList(itemLists.subList(dataSize - rowsPerPageCombo, dataSize));
                        tableView.getItems().setAll(itemObservableList);
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                    }
                } else if (currentPageValue > footerOffValue) {
                    currentPage.setText(Integer.toString(footerOffValue));
                    currentPageValue = footerOffValue;
                    setFooterOffValue();
                }

            } catch (NumberFormatException numberFormatException) {

            }
        });
    }

    private void previousButtonAction(JFXButton previousButton, JFXTextField currentPage) {
        previousButton.setOnAction(e -> {
            try {
                int page = Integer.parseInt(currentPage.getText());
                if (page > 1) {
                    page--;
                    currentPage.setText(Integer.toString(page));
                    currentPageValue = page;
                }
            } catch (NumberFormatException numberFormatException) {

            }

        });
    }

    private void nextButtonAction(JFXButton nextButton, JFXTextField currentPage) {
        nextButton.setOnAction(r -> {
            try {
                int page = Integer.parseInt(currentPage.getText());
                if (page <= footerOffValue) {
                    page++;
                    currentPage.setText(Integer.toString(page));
                    currentPageValue = page;
                }
            } catch (NumberFormatException numberFormatException) {

            }

        });
    }

    private void setAlignment(HBox topHbox, HBox midHbox, HBox botHbox) {
        VBox.setMargin(topHbox, new Insets(20.0, 0.0, 0.0, 20.0));
        VBox.setMargin(midHbox, new Insets(20.0, 20.0, 0.0, 20.0));
        VBox.setMargin(botHbox, new Insets(20.0, 0.0, 20.0, 20.0));

        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
    }

    private void setFooterOffValue() {
        if (dataSize <= rowsPerPageCombo) {
            footerOffValue = 1;
        } else {
            int remainder = dataSize % rowsPerPageCombo;
            if (remainder == 0) {
                footerOffValue = dataSize / rowsPerPageCombo;
            } else {
                footerOffValue = dataSize / rowsPerPageCombo + 1;
            }
        }
    }

    private void createTableView() {
        String centerAlignment = "-fx-alignment: CENTER;".intern();
        this.tableView = new TableView<Item>();
        this.tableView.setPrefHeight(275);
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Item, String> sku = new TableColumn<>("SKU");
        sku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        TableColumn<Item, String> itemName = new TableColumn<>("Item Name");
        itemName.setCellValueFactory(new PropertyValueFactory<>("item_name"));
        TableColumn<Item, String> category = new TableColumn<>("Category");
        category.setCellValueFactory(new PropertyValueFactory<>("sub_category_detail"));
        TableColumn<Item, String> quantity = new TableColumn<>("Quantity");
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Item, String> cost = new TableColumn<>("Cost");
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        TableColumn<Item, String> inStock = new TableColumn<>("In Stock");
        inStock.setCellValueFactory(new PropertyValueFactory<>("in_stock"));

        sku.setStyle(centerAlignment);
        itemName.setStyle(centerAlignment);
        category.setStyle(centerAlignment);
        cost.setStyle(centerAlignment);
        inStock.setStyle(centerAlignment);

        setTableColumns(sku, itemName, category, cost, inStock);


        dataSize = itemLists.size();
        maxSku = itemLists.get(dataSize - 1).getSku();
        tableView.getItems().clear();
        if(dataSize<10){
            itemObservableList = FXCollections.observableArrayList(itemLists.subList(currentPageValue * rowsPerPageCombo - rowsPerPageCombo, dataSize));
        }else{
            itemObservableList = FXCollections.observableArrayList(itemLists.subList(currentPageValue * rowsPerPageCombo - rowsPerPageCombo, (currentPageValue * rowsPerPageCombo)));
        }

        setFooterOffValue();
        tableView.getItems().setAll(itemObservableList);
        ObservableList<Item> cellData = FXCollections.observableArrayList();
        tableView.setOnMouseClicked(x -> {
            if (x.getClickCount() == 2) {
                cellData.clear();
                cellData.add(tableView.getSelectionModel().getSelectedItem());
                selectedItem = applicationContext.getBean(Item.class);
                selectedItem.setTag(cellData.get(0).getTag());
                selectedItem.setCost(cellData.get(0).getCost());
                selectedItem.setItem_name(cellData.get(0).getItem_name());
                selectedItem.setLow_stock(cellData.get(0).getLow_stock());
                selectedItem.setSub_category_detail(cellData.get(0).getSub_category_detail());
                selectedItem.setIn_stock(cellData.get(0).getIn_stock());
                selectedItem.setSku(cellData.get(0).getSku());
                selectedItem.setIn_stock(cellData.get(0).getIn_stock());
                selectedItem.setItem_category_header(cellData.get(0).getItem_category_header());
                selectedItem.setItem_category(cellData.get(0).getItem_category());
                selectedItem.setUnit(cellData.get(0).getUnit());
                new StageLoader().load(EditItemController.class, x, applicationContext, "");
            }
        });
    }

    private void setTableColumns(TableColumn<Item, String> sku, TableColumn<Item, String> itemName, TableColumn<Item, String> category, TableColumn<Item, String> cost, TableColumn<Item, String> inStock) {
        tableView.getColumns().add(sku);
        tableView.getColumns().add(itemName);
        tableView.getColumns().add(category);
        tableView.getColumns().add(cost);
        tableView.getColumns().add(inStock);
    }


    @KafkaListener(topics = "new-item-1-second-refresh", groupId = "myGroup", containerFactory = "itemListener")
    public void latestItem(Message<List<Item>> message) {
        boolean hasDataExceeded = (dataSize > (currentPageValue * rowsPerPageCombo)) ? true : false;
        if (!ObjectUtils.isEmpty(this.tableView.getItems()) && currentPageValue >= 1 && currentPageValue < footerOffValue && currentPageValue < rowsPerPageCombo && message.getPayload().size() > 0 && !hasDataExceeded) {
            setLatestItems(message);
            refreshTableAndIsLastPage(false);

        } else if ((footerOffValue == currentPageValue) && (!ObjectUtils.isEmpty(this.tableView.getItems())) && !hasDataExceeded) {
            setLatestItems(message);
            refreshTableAndIsLastPage(true);
        }
    }

    private void setLatestItems(Message<List<Item>> message) {
        List<Item> latestItem = message.getPayload();//Always the last  item --> always 1 item
        if (maxSku < latestItem.get(0).getSku()) {
            itemLists.add(latestItem.get(0));
            maxSku = latestItem.get(0).getSku();
            dataSize++;
            setFooterOffValue();
            ofPageLabel.setText("of " + footerOffValue);
        }
    }

    @KafkaListener(topics = "updated-item-1-second-refresh", groupId = "myGroup", containerFactory = "itemListener")
    public void updateTableWithUpdatedItem(Message<List<Item>> message) {
        if (!ObjectUtils.isEmpty(this.tableView.getItems()) && currentPageValue >= 1 && currentPageValue < footerOffValue && currentPageValue < rowsPerPageCombo && message.getPayload().size() > 0) {
            setUpdatedItems(message);
            refreshTableAndIsLastPage(false);

        } else if ((footerOffValue == currentPageValue) && !ObjectUtils.isEmpty(this.tableView.getItems())) {
            setUpdatedItems(message);
            refreshTableAndIsLastPage(true);
        }
    }

    private void setUpdatedItems(Message<List<Item>> message) {
        List<Item> updatedItemList = message.getPayload();
        updatedItemList.stream().forEach(updatedItem -> {
            itemLists.stream().forEach(previousitem -> {
                if (updatedItem.getSku() == previousitem.getSku()) {
                    previousitem.setItem_name(updatedItem.getItem_name());
                    previousitem.setCost(updatedItem.getCost());

                    previousitem.setSub_category_detail(updatedItem.getSub_category_detail());
                    //previousitem.setTag("1"); //if THERE IS A POSSIBILITY OF MULTIPLE USER UPDATE ON SAME ITEM, do not set this to 1
                    previousitem.setLow_stock(updatedItem.getLow_stock());
                    previousitem.setIn_stock(updatedItem.getIn_stock());
                }
            });
        });
    }

    @KafkaListener(topics = "deleted-item-list-1-second-refresh", groupId = "myGroup", containerFactory = "deletedItemListener")
    public void deletedItems(Message<List<DeletedItems>> message) {
        if (!ObjectUtils.isEmpty(this.tableView.getItems()) && currentPageValue >= 1 && currentPageValue < footerOffValue && currentPageValue < rowsPerPageCombo && message.getPayload().size() > 0) {
            setDeletedItems(message);
            refreshTableAndIsLastPage(false);

        } else if ((footerOffValue == currentPageValue) && (!ObjectUtils.isEmpty(this.tableView.getItems()))) {
            setDeletedItems(message);
            refreshTableAndIsLastPage(true);
        }
    }

    private void setDeletedItems(Message<List<DeletedItems>> message) {
        List<DeletedItems> deletedItemsList = message.getPayload();
        deletedItemsList.stream().forEach(deletedItem -> {
            itemLists.stream().forEach(existingItem -> {
                if (deletedItem.getSku() == existingItem.getSku()) {
                    itemLists.remove(existingItem);
                    dataSize--;
                }
            });
        });
    }

    private void refreshTableAndIsLastPage(boolean isLastPage) {
        if (isLastPage) {
            if (!(dataSize % rowsPerPageCombo == 0)) {
                itemObservableList = FXCollections.observableArrayList(itemLists.subList(dataSize - rowsPerPageCombo, dataSize));
                tableView.getItems().setAll(itemObservableList);
            } else {
                setObservableListNonLastpage(currentPageValue * rowsPerPageCombo - rowsPerPageCombo, currentPageValue * rowsPerPageCombo);
            }

        } else {
            setObservableListNonLastpage(currentPageValue * rowsPerPageCombo - rowsPerPageCombo, currentPageValue * rowsPerPageCombo);
        }
    }

    private void setObservableListNonLastpage(int i, int i2) {
        itemObservableList = FXCollections.observableArrayList(itemLists.subList(i, (i2)));
        tableView.getItems().setAll(itemObservableList);
    }

    private List<String> fillRowsperPage() {
        List<String> rowsPerPage = rowsPerPage();
        return rowsPerPage;
    }


    private List<String> fillStockAlert() {
        List<String> stockAlerts = stockAlerts();
        return stockAlerts;
    }


}
