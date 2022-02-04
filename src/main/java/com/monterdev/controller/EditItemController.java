package com.monterdev.controller;

import com.jfoenix.controls.*;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

import static com.monterdev.configuration.ItemsUIConfiguration.getItemsTopHboxSpacing;
import static com.monterdev.constants.HistoryConstants.PURCHASED_ITEM;
import static com.monterdev.util.ComponentCreator.createSearchBox;
import static com.monterdev.util.ControlNumberGenerator.generateControlNumber;

@Component
@FxmlView("EditItem.fxml")
@Getter
@Setter
public class EditItemController implements Initializable {

    @FXML
    private AnchorPane EditItemAnchorpane;

    @FXML
    private VBox searchGroupMainContainer;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField inStock;

    @FXML
    private JFXTextField lowStock;

    @FXML
    private HBox trackStockHbox;

    @FXML
    private JFXComboBox itemCategoryCombo;

    @FXML
    private JFXComboBox comboUnit;

    @FXML
    private JFXTextField quantity;

    @FXML
    private JFXTextField purchaseCost;

    @FXML
    private JFXTextField averageCost;

    @FXML
    private JFXTextField sku;

    @FXML
    private JFXToggleButton trackStock = new JFXToggleButton();

    @FXML
    private JFXButton delete;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXButton save;

    @FXML
    private JFXButton view;

    @FXML
    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private JFXTextField supplierGroup;

    @FXML
    private JFXTextField receiptNumber;

    @FXML
    private JFXTextField purchaseOrderNumber;

    private VBox vBox;

    @Autowired
    private Item selectedItem;

    @Autowired
    private ConfigurableApplicationContext applicationContext;


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    private Iterable<ItemCategory> categoryPreLoaded;

    @Autowired
    private DeletedItemsRepository deletedItemsRepository;

    @Autowired
    @Qualifier("itemLists")
    private List<Item> itemLists;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private QrcodeRepository qrcodeRepository;

    @Autowired
    private SearchUtil searchUtil;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private HistoryRepository historyRepository;

    private List<Item> resultsList = new ArrayList<>();

    private List<String> responseList = new ArrayList<>();

    private ScheduledExecutorService timer;

    private static String NO_CATEGORY_ON_MASTER_DATA = "You don't have any Category / Inventory type yet! Please add it to Master data. ";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectedItem = applicationContext.getBean(Item.class);
        name.setText(selectedItem.getItem_name());
        quantity.setText("0");
        purchaseCost.setText("0");
        averageCost.setText(Double.toString(selectedItem.getCost()));
        sku.setText(Integer.toString(selectedItem.getSku()));
        inStock.setText(Integer.toString(selectedItem.getIn_stock()));
        lowStock.setText(Integer.toString(selectedItem.getLow_stock()));
        comboUnit.setValue(selectedItem.getUnit());
        checkIfItemNameExists();

        ItemCategory itemCategory = null;
        if (ObjectUtils.isEmpty(selectedItem.getItem_category())) {
            Iterator<ItemCategory> itemCategoryIterator = categoryRepository.findAll().iterator();
            if (itemCategoryIterator.hasNext()) {
                itemCategory = itemCategoryIterator.next();
            } else {
                Prompt.failed(NO_CATEGORY_ON_MASTER_DATA);
            }

        }

        List<Unit> itemUnits = unitRepository.findAllItemUnits();

        loadCategories(itemCategory, itemUnits);

        itemNameonChange();

        quantityOnChange();

        lowStockOnChange();

        categoryHeaderOnChange();

        purchaseCostOnChange();

        comboUnitOnChange();

        trackStockHbox.setVisible(false);

        checkItemIfUpdated();

    }

    private void checkItemIfUpdated() {
        Optional<Item> optionalUpdatedItem = itemsRepository.findById(selectedItem.getSku());
        if (optionalUpdatedItem.isPresent()) {
            Item updatedItem = optionalUpdatedItem.get();
            if (!updatedItem.getItem_name().equalsIgnoreCase(selectedItem.getItem_name()) ||
                    !Double.toString(updatedItem.getCost()).equalsIgnoreCase(String.valueOf(selectedItem.getCost())) ||
                    !Integer.toString(updatedItem.getIn_stock()).equalsIgnoreCase(String.valueOf(selectedItem.getIn_stock())) ||
                    !Integer.toString(updatedItem.getLow_stock()).equalsIgnoreCase(String.valueOf(selectedItem.getLow_stock())) ||
                    !updatedItem.getItem_category().equalsIgnoreCase(selectedItem.getItem_category())
            ) {
                Optional<ButtonType> optionalButtonType = Prompt.confirm("Item changed. Item will be updated before saving.");
                if (optionalButtonType.isPresent()) {
                    if (optionalButtonType.get().getText().equalsIgnoreCase("OK")) {
                        name.setText(updatedItem.getItem_name());
                        averageCost.setText(Double.toString(updatedItem.getCost()));
                        inStock.setText(Integer.toString(updatedItem.getIn_stock()));
                        lowStock.setText(Integer.toString(updatedItem.getLow_stock()));
                        comboUnit.setValue(updatedItem.getUnit());
                    }
                }

            }
        }
    }

    private void comboUnitOnChange() {
        this.comboUnit.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                selectedItem.setUnit(newValue.toString());
            }
        });
    }

    private void categoryHeaderOnChange() {
        this.itemCategoryCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                selectedItem.setItem_category(newValue.toString());
            }
        });
    }


    private void checkIfItemNameExists() {
        if (!ObjectUtils.isEmpty(name)) {
            if (name.getText().equalsIgnoreCase("")) {
                delete.setText("CLEAR");
            } else {
                delete.setText("DELETE");
            }
        } else {
            name = new JFXTextField();
            delete = new JFXButton();
            delete.setText("CLEAR");
        }
    }


    public void create() {
        selectedItem = applicationContext.getBean(Item.class);
        EditItemAnchorpane = new AnchorPane();

        vBox = new VBox();

        //INITIAL HBOX CONFIGURATION
        HBox topHbox = new HBox();
        topHbox.setAlignment(Pos.TOP_CENTER);
        topHbox.setSpacing(DataUtil.formatDouble(getItemsTopHboxSpacing()));

        HBox midHbox = new HBox();
        midHbox.setAlignment(Pos.TOP_LEFT);
        midHbox.setSpacing(DataUtil.formatDouble(getItemsTopHboxSpacing()));


        HBox botHbox = new HBox();
        botHbox.setAlignment(Pos.TOP_LEFT);
        botHbox.setSpacing(DataUtil.formatDouble(getItemsTopHboxSpacing()));
        Stage stage = (Stage) cancel.getScene().getWindow();
        new StageLoader().load(EditItemController.class, applicationContext, stage);
    }


    private void loadCategories(ItemCategory itemCategory, List<Unit> unitList) {
        clearComboBoxes();
        categoryPreLoaded = categoryRepository.findAll();
        categoryPreLoaded.forEach(itemCategory1 -> {
            this.itemCategoryCombo.getItems().add(itemCategory1.getCategory_name());
        });

        unitList.stream().forEach(e -> {
            comboUnit.getItems().add(e.getUnit());
        });

        this.itemCategoryCombo.setValue(selectedItem.getItem_category());
    }

    private void clearComboBoxes() {

        this.itemCategoryCombo.getItems().clear();
    }

    private void itemNameonChange() {
        name.textProperty().addListener((observable, oldvalue, newvalue) -> {
            if (oldvalue != newvalue && !ObjectUtils.isEmpty(newvalue)) {
                selectedItem.setItem_name(newvalue);
                try {
                    ObservableList<Node> nodeStream = searchGroupMainContainer.getChildren();
                    for (Node node : nodeStream) {
                        if (node instanceof AnchorPane) {
                            searchUtil.searchItem(newvalue, itemLists, responseList, "NAME");
                            VBox vbox = (VBox) ((AnchorPane) node).getChildren().get(0);
                            JFXListView listView = (JFXListView) vbox.getChildren().get(0);
                            listView.getItems().clear();
                            listView.getItems().addAll(responseList);
                            listView.setOnMouseClicked(e -> {
                                if (e.getClickCount() == 2) {
                                    setFields(listView.getSelectionModel().getSelectedItem().toString());
                                }
                            });

                        } else if (node instanceof JFXTextField && searchGroupMainContainer.getChildren().size() < 3) {
                            AnchorPane searchBox = createSearchBox();
                            searchGroupMainContainer.getChildren().add(searchBox);
                            VBox.setMargin(searchBox, new Insets(0.0, 0.0, 20.0, 20.0));
                        }
                    }
                } catch (ConcurrentModificationException e) {

                }

            } else if (ObjectUtils.isEmpty(newvalue)) {
                if (searchGroupMainContainer.getChildren().size() > 2) {
                    searchGroupMainContainer.getChildren().remove(2);
                }
                resetFields();
                responseList.clear();
            }

        });
    }

    private void purchaseCostOnChange() {
        try {
            purchaseCost.textProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue != newValue) {
                    selectedItem.setCost(DataUtil.formatDouble(newValue));
                }
            });
        } catch (NumberFormatException numberFormatException) {
            purchaseCost.setText("0");
        }

    }

    private void setFields(String itemName) {
        itemLists = itemsRepository.findAll();
        Optional<Item> item = itemLists.stream().filter(e -> e.getItem_name().equalsIgnoreCase(itemName))
                .findFirst();
        if (item.isPresent()) {
            name.setText(itemName);
            inStock.setText(Integer.toString(item.get().getIn_stock()));
            lowStock.setText(Integer.toString(item.get().getLow_stock()));
            sku.setText(Integer.toString(item.get().getSku()));
            averageCost.setText(Double.toString(item.get().getCost()));
            itemCategoryCombo.setValue(item.get().getItem_category());
            comboUnit.setValue(item.get().getUnit());
        }
    }


    private void quantityOnChange() {
        quantity.textProperty().addListener((observable, oldvalue, newvalue) -> {
            if (oldvalue != newvalue && StringUtils.isNumeric(newvalue)) {
                selectedItem.setQuantity(Integer.parseInt(newvalue));
            } else {
                quantity.setText("0");
            }
        });
    }

    private void lowStockOnChange() {
        lowStock.textProperty().addListener((obs, old, newv) -> {
            if (old != newv && StringUtils.isNumeric(newv)) {
                selectedItem.setLow_stock(Integer.parseInt(newv));
            } else {
                lowStock.setText("1");
            }
        });
    }


    public void viewHistory(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Stage currentStage = (Stage) save.getScene().getWindow();
        new StageLoader().load(InventoryHistoryController.class, applicationContext, stage);
        currentStage.close();
    }

    public void delete(ActionEvent actionEvent) {
        if (delete.getText().equalsIgnoreCase("DELETE")) {
            Optional<ButtonType> buttonType = Prompt.confirm("Are you sure you want to delete " + selectedItem.getItem_name() + " ?");
            if (buttonType.isPresent()) {
                if (!buttonType.get().getButtonData().isCancelButton()) {
                    DeletedItems deletedItems = new DeletedItems().builder()
                            .cost(selectedItem.getCost())
                            .quantity(Integer.parseInt(quantity.getText()))
                            .item_name(selectedItem.getItem_name())
                            .item_category(selectedItem.getItem_category())
                            .unit(selectedItem.getUnit())
                            .in_stock(selectedItem.getIn_stock())
                            .low_stock(selectedItem.getLow_stock())
                            .sku(selectedItem.getSku())
                            .tag(selectedItem.getTag())
                            .build();
                    if (!ObjectUtils.isEmpty(deletedItemsRepository.save(deletedItems))) {
                        itemsRepository.delete(selectedItem);
                        History history = setHistory(selectedItem, "DELETED ITEM", false);
                        historyRepository.save(history);
                        cancel(new ActionEvent());
                    }
                }

            }
        } else {
            resetFields();
        }

    }

    private void resetFields() {
        resetSelectedItem();
        name.setText("");
        lowStock.setText("1");
        quantity.setText("0");
        purchaseCost.setText("0");
        averageCost.setText("0");
        inStock.setText("0");
        sku.setText("0");
        supplierGroup.setText("");
        receiptNumber.setText("");
        purchaseOrderNumber.setText("");
        Iterator<Unit> unitIterator = unitRepository.findAll().iterator();
        if (unitIterator.hasNext()) {
            comboUnit.setValue(unitIterator.next().getUnit());
        } else {
            Prompt.failed(NO_CATEGORY_ON_MASTER_DATA);
        }

        Iterator<ItemCategory> itemCategoryIterator = categoryRepository.findAll().iterator();
        if (itemCategoryIterator.hasNext()) {
            itemCategoryCombo.setValue(itemCategoryIterator.next().getCategory_name());
        } else {
            Prompt.failed(NO_CATEGORY_ON_MASTER_DATA);
        }

    }

    private void resetSelectedItem() {
        selectedItem.setTag(null);
        selectedItem.setItem_name("");
        selectedItem.setLow_stock(1);
        selectedItem.setQuantity(0);
        selectedItem.setSku(0);
        selectedItem.setCost(0.0);
        selectedItem.setIn_stock(0);
        selectedItem.setItem_category("");
        selectedItem.setUnit("");
    }

    public void cancel(ActionEvent actionEvent) {
        selectedItem = applicationContext.getBean(Item.class);
        resetSelectedItem();
        Stage stage = (Stage) cancel.getScene().getWindow();
        new StageLoader().load(InventoryListController.class, applicationContext, stage);
    }

    public void save(ActionEvent actionEvent) {
        if (!ObjectUtils.isEmpty(name.getText())) {
            try {
                checkItemIfUpdated();
                selectedItem.setTag(null);
                if (!sku.getText().equalsIgnoreCase("0")) {
                    //EXISTING ITEM
                    //get previous quantity,cost and product of pv q and  pv cost
                    int previousQuantity = Integer.parseInt(inStock.getText());
                    double previousItemCostPerUnit = DataUtil.formatDouble(averageCost.getText());
                    double previousTotalAmount = previousQuantity * previousItemCostPerUnit;

                    int currentQuantity = Integer.parseInt(quantity.getText());
                    double currentItemCostPerUnit = DataUtil.formatDouble(purchaseCost.getText());
                    double currentTotalAmount = currentQuantity * currentItemCostPerUnit;

                    double finalAverageCost = (previousTotalAmount + currentTotalAmount) / (previousQuantity + currentQuantity);
                    if (Double.isNaN(finalAverageCost)) {
                        finalAverageCost = 0;
                    }
                    selectedItem.setCost(DataUtil.formatToDouble(finalAverageCost));
                    selectedItem.setIn_stock(currentQuantity + previousQuantity);
                    selectedItem.setSku(Integer.parseInt(sku.getText()));
                    selectedItem.setUnit((String) comboUnit.getSelectionModel().getSelectedItem());
                    if (selectedItem.getQuantity() == 0) {
                        selectedItem.setQuantity(selectedItem.getIn_stock());
                    }
                }

                Item savedItem = itemsRepository.save(selectedItem);
                if (!ObjectUtils.isEmpty(savedItem) && (Integer.parseInt(quantity.getText()) != 0) && (DataUtil.formatDouble(purchaseCost.getText()) > 0)) {

                    PurchaseOrder purchaseOrder = setPurchaseOrder(savedItem);
                    purchaseOrderRepository.save(purchaseOrder);
                    SupplierGroup supplierGroup = setSupplierGroup(savedItem);
                    supplierRepository.save(supplierGroup);
                    History history = setHistory(savedItem);
                    historyRepository.save(history);

                    try {
                        QrCodeUtil.saveQrCode(String.valueOf(savedItem.getSku()), savedItem.getSku() + "-" + savedItem.getItem_name());
                    } catch (Exception ioException) {
                        Prompt.failed("Error in Saving QR Code!");
                    }

                    Prompt.success("Purchased Item was saved!");
                    resetSelectedItem();
                    Stage stage = (Stage) save.getScene().getWindow();
                    new StageLoader().load(MainDashboardController.class, applicationContext, stage);
                } else if (!ObjectUtils.isEmpty(savedItem)) {
                    Prompt.success("Item updated successfully!");
                    resetSelectedItem();
                    Stage stage = (Stage) save.getScene().getWindow();
                    new StageLoader().load(MainDashboardController.class, applicationContext, stage);
                } else {
                    Prompt.failed("Transaction Failed! Please Try again!");
                }
            } catch (NumberFormatException e) {
                Prompt.failed("Transaction Failed! Please Try again! Reason is: \n" + e.getMessage());
            } catch (NullPointerException e) {
                Prompt.failed("Transaction Failed! Please Try again! Reason is: \n" + e.getMessage());
            } catch (Exception e) {
                Prompt.failed("Transaction Failed! Please Try again! Reason is: \n" + e.getMessage());
            }

        }

    }

    private History setHistory(Item savedItem) {
        History history = new History();
        history.setDate(AppTime.now());
        history.setStock_after(savedItem.getIn_stock());
        history.setReason(PURCHASED_ITEM);
        history.setAdjustment(savedItem.getQuantity());
        history.setItem_category(savedItem.getItem_category());
        history.setItem_name(savedItem.getItem_name());

        return history;
    }

    private History setHistory(Item savedItem, String reason, boolean isPositive) {
        History history = new History();
        history.setDate(AppTime.now());
        history.setReason(reason);
        if (isPositive) {
            history.setAdjustment(savedItem.getQuantity());
            history.setStock_after(savedItem.getQuantity());
        } else {
            history.setStock_after(0);
            history.setAdjustment(savedItem.getIn_stock() * -1);
        }

        history.setItem_category(savedItem.getItem_category());
        history.setItem_name(savedItem.getItem_name());
        return history;
    }

    private SupplierGroup setSupplierGroup(Item savedItem) {
        String controlNumber = generateControlNumber();
        SupplierGroup supplierDatum = new SupplierGroup();
        supplierDatum.setSupplier(ObjectUtils.isEmpty(supplierGroup.getText()) ? controlNumber : supplierGroup.getText());
        supplierDatum.setReceipt_number(ObjectUtils.isEmpty(receiptNumber.getText()) ? controlNumber : receiptNumber.getText());
        supplierDatum.setPurchase_order_number(ObjectUtils.isEmpty(purchaseOrderNumber.getText()) ? controlNumber : purchaseOrderNumber.getText());
        supplierDatum.setDate_created(AppTime.now());
        supplierDatum.setSku(savedItem.getSku());
        return supplierDatum;
    }

    private PurchaseOrder setPurchaseOrder(Item item) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setDatecreated(AppTime.now());
        purchaseOrder.setPurchase_cost(DataUtil.formatDouble(purchaseCost.getText()));
        purchaseOrder.setItem_name(name.getText());
        purchaseOrder.setQuantity(Integer.parseInt(quantity.getText()));
        purchaseOrder.setSku(item.getSku());
        purchaseOrder.setIn_stock(item.getIn_stock());
        purchaseOrder.setItem_category(selectedItem.getItem_category());
        purchaseOrder.setAmount(Integer.parseInt(quantity.getText()) * DataUtil.formatDouble(purchaseCost.getText()));
        return purchaseOrder;
    }

    public void showTrackStock(ActionEvent actionEvent) {
        if (trackStock.isSelected()) {
            trackStockHbox.setVisible(true);
        } else {
            trackStockHbox.setVisible(false);
        }
    }


}
