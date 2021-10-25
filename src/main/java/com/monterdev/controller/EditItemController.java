package com.monterdev.controller;

import com.jfoenix.controls.*;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.monterdev.constants.ItemsUIConfiguration.*;
import static com.monterdev.util.ComponentCreator.createSearchBox;

@Component
@FxmlView("EditItem.fxml")
@Getter
@Setter
public class EditItemController {

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
    private JFXComboBox category;

    @FXML
    private JFXComboBox subCategoryHeader;

    @FXML
    private JFXComboBox subCategoryDetail;

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
    private SubCategoryDetailsRepository subCategoryDetailsRepository;

    @Autowired
    private SubCategoryHeaderRepository subCategoryHeaderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private Iterable<SubCategoryDetail> subCategoryDetailPreLoaded;

    @Autowired
    private Iterable<SubCategoryHeader> subCategoryHeaderPreLoaded;

    @Autowired
    private Iterable<Category> categoryPreLoaded;

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

    private List<Item> resultsList = new ArrayList<>();

    private List<String> responseList = new ArrayList<>();


    public void initialize() {

        selectedItem = applicationContext.getBean(Item.class);
        name.setText(selectedItem.getItem_name());
        quantity.setText("0");
        purchaseCost.setText("0");
        averageCost.setText(Double.toString(selectedItem.getCost()));
        sku.setText(Integer.toString(selectedItem.getSku()));
        inStock.setText(Integer.toString(selectedItem.getIn_stock()));
        lowStock.setText(Integer.toString(selectedItem.getLow_stock()));
        checkIfItemNameExists();


        SubCategoryDetail subCategoryDetail = subCategoryDetailsRepository.findBySubCategoryDetail(selectedItem.getSub_category_detail());
        SubCategoryHeader subCategoryHeader = subCategoryHeaderRepository.findBySubCategoryHeader(subCategoryDetail.getSub_category_header());
        Category category = categoryRepository.findByCategory(subCategoryHeader.getCategory());

        loadCategories(subCategoryDetail, subCategoryHeader, category);

        itemNameonChange();

        quantityOnChange();

        lowStockOnChange();

        subCategoryDetailOnChange();

        purchaseCostOnChange();

        trackStockHbox.setVisible(false);
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
        topHbox.setSpacing(Double.parseDouble(getItemsTopHboxSpacing()));

        HBox midHbox = new HBox();
        midHbox.setAlignment(Pos.TOP_LEFT);
        midHbox.setSpacing(Double.parseDouble(getItemsTopHboxSpacing()));


        HBox botHbox = new HBox();
        botHbox.setAlignment(Pos.TOP_LEFT);
        botHbox.setSpacing(Double.parseDouble(getItemsTopHboxSpacing()));

        new StageLoader().load(EditItemController.class, applicationContext);
    }


    private void loadCategories(SubCategoryDetail subCategoryDetail, SubCategoryHeader subCategoryHeader, Category category) {
        clearComboBoxes();
        subCategoryDetailPreLoaded.forEach(subCategoryDetail1 -> {
            this.subCategoryDetail.getItems().add(subCategoryDetail1.getSub_category_detail());
        });

        subCategoryHeaderPreLoaded.forEach(subCategoryHeader1 -> {
            this.subCategoryHeader.getItems().add(subCategoryHeader1.getSub_category_header());
        });

        categoryPreLoaded.forEach(category1 -> {
            this.category.getItems().add(category1.getCategory_name());
        });
        this.subCategoryDetail.setValue(subCategoryDetail.getSub_category_detail());
        this.subCategoryHeader.setValue(subCategoryHeader.getSub_category_header());
        this.category.setValue(category.getCategory_name());
    }

    private void clearComboBoxes() {
        this.subCategoryDetail.getItems().clear();
        this.subCategoryHeader.getItems().clear();
        this.category.getItems().clear();
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
                                if (e.getClickCount() == 1) {
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
                searchGroupMainContainer.getChildren().remove(2);
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
        Optional<Item> item = itemLists.stream().filter(e -> e.getItem_name().equalsIgnoreCase(itemName))
                .findFirst();
        if (item.isPresent()) {
            name.setText(itemName);
            inStock.setText(Integer.toString(item.get().getIn_stock()));
            lowStock.setText(Integer.toString(item.get().getLow_stock()));
            sku.setText(Integer.toString(item.get().getSku()));
            averageCost.setText(Double.toString(item.get().getCost()));
            subCategoryDetail.setValue(item.get().getSub_category_detail());
        }
    }


    private void quantityOnChange() {
        try {
            quantity.textProperty().addListener((observable, oldvalue, newvalue) -> {
                if (oldvalue != newvalue) {
                    selectedItem.setQuantity(Integer.parseInt(newvalue));
                }

            });
        } catch (NumberFormatException numberFormatException) {
            quantity.setText("0");
        }

    }

    private void lowStockOnChange() {
        lowStock.textProperty().addListener((obs, old, newv) -> {
            if (old != newv) {
                selectedItem.setLow_stock(Integer.parseInt(newv));
            }
        });
    }

    private void subCategoryDetailOnChange() {
        this.subCategoryDetail.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                selectedItem.setSub_category_detail(newValue.toString());
            }
        });
    }


    public void updateSubCategoryHeader(ActionEvent actionEvent) {
        try {
            List<SubCategoryHeader> subCategories = StreamSupport.stream(this.subCategoryHeaderPreLoaded.spliterator(), false)
                    .filter(e -> e.getCategory().equalsIgnoreCase(this.category.getValue().toString())).collect(Collectors.toList());
            this.subCategoryHeader.getItems().clear();
            this.subCategoryDetail.getItems().clear();
            subCategories.stream().forEach(s -> {
                this.subCategoryHeader.getItems().add(s.getSub_category_header());
            });
        } catch (NullPointerException nullPointerException) {

        }

    }

    public void updateSubCategoryDetail(ActionEvent actionEvent) {
        try {
            List<SubCategoryDetail> subCategoryDetailList = StreamSupport.stream(this.subCategoryDetailPreLoaded.spliterator(), false)
                    .filter(e -> e.getSub_category_header().equalsIgnoreCase(this.subCategoryHeader.getValue().toString())).collect(Collectors.toList());
            this.subCategoryDetail.getItems().clear();
            subCategoryDetailList.stream().forEach(d -> {
                this.subCategoryDetail.getItems().add(d.getSub_category_detail());
            });
        } catch (NullPointerException nullPointerException) {

        }
    }

    public void viewHistory(ActionEvent actionEvent) {
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
                            .in_stock(selectedItem.getIn_stock())
                            .low_stock(selectedItem.getLow_stock())
                            .margin(selectedItem.getMargin())
                            .sku(selectedItem.getSku())
                            .sub_category_detail(selectedItem.getSub_category_detail())
                            .tag(selectedItem.getTag())
                            .build();
                    if (!ObjectUtils.isEmpty(deletedItemsRepository.save(deletedItems))) {
                        itemsRepository.delete(selectedItem);
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
        lowStock.setText("0");
        quantity.setText("0");
        purchaseCost.setText("0");
        averageCost.setText("0");
        inStock.setText("0");
        sku.setText("0");
        supplierGroup.setText("");
        receiptNumber.setText("");
        purchaseOrderNumber.setText("");
        subCategoryDetail.setValue(getConfigValue(defaultCategoryData));
    }

    private void resetSelectedItem() {
        selectedItem.setTag(null);
        selectedItem.setItem_name("");
        selectedItem.setLow_stock(0);
        selectedItem.setQuantity(0);
        selectedItem.setSku(0);
        selectedItem.setMargin(0.0);
        selectedItem.setCost(0.0);
        selectedItem.setIn_stock(0);
        selectedItem.setSub_category_detail(getConfigValue(defaultCategoryData));
    }

    public void cancel(ActionEvent actionEvent) {
        selectedItem = applicationContext.getBean(Item.class);
        resetSelectedItem();
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    public void save(ActionEvent actionEvent) {
        if(!ObjectUtils.isEmpty(name.getText())){
            selectedItem.setTag(null);
            if (!sku.getText().equalsIgnoreCase("0")) {
                //EXISTING ITEM

                double realAverageCost = Double.parseDouble(averageCost.getText());
                double realPurchaseCost = Double.parseDouble(purchaseCost.getText());
                int realQuantity = Integer.parseInt(quantity.getText());
                int realInstock = Integer.parseInt(inStock.getText());
                double totalQuantity = realInstock + realQuantity;
                double productOfQuantityandPurchaseCost = realQuantity * realPurchaseCost;
                double totalCost = realAverageCost + productOfQuantityandPurchaseCost;
                double finalAverageCost = totalCost / totalQuantity;
                selectedItem.setCost(DataUtil.formatToDouble(finalAverageCost));
                selectedItem.setIn_stock(realInstock + realQuantity);
            } else {
                selectedItem.setIn_stock(Integer.parseInt(quantity.getText()));
            }
            Item savedItem = itemsRepository.save(selectedItem);
            if (!ObjectUtils.isEmpty(savedItem)) {

                PurchaseOrder purchaseOrder = setPurchaseOrder(savedItem);
                purchaseOrderRepository.save(purchaseOrder);
                SupplierGroup supplierGroup = setSupplierGroup(savedItem);
                supplierRepository.save(supplierGroup);
                Qrcode qrcode = setQrCodeData(savedItem);
                qrcodeRepository.save(qrcode);

                try {
                    QrCodeUtil.saveQrCode(Integer.toString(savedItem.getSku()));
                } catch (Exception ioException) {

                }


                Prompt.success("Data saved!");
                resetSelectedItem();
                Stage stage = (Stage) save.getScene().getWindow();
                stage.close();
            } else {
                Prompt.failed("Data did NOT save successfully!");
            }
        }

    }

    private Qrcode setQrCodeData(Item savedItem) {
        Qrcode qrcode = new Qrcode();
        qrcode.setQrcodepath(QrCodeUtil.filePath + "\\" + savedItem.getItem_name() + ".jpg");
        qrcode.setSku(savedItem.getSku());
        qrcode.setDatecreated(AppTime.now());
        return qrcode;
    }

    private SupplierGroup setSupplierGroup(Item savedItem) {
        SupplierGroup supplierdata = new SupplierGroup();
        supplierdata.setSupplier(ObjectUtils.isEmpty(supplierGroup.getText()) ? "NONE" : supplierGroup.getText());
        supplierdata.setReceipt_number(ObjectUtils.isEmpty(receiptNumber.getText()) ? "NONE" : receiptNumber.getText());
        supplierdata.setPurchase_order_number(ObjectUtils.isEmpty(purchaseOrderNumber.getText()) ? "NONE" : purchaseOrderNumber.getText());
        return supplierdata;
    }

    private void createQrCode(Item item) {

    }

    private PurchaseOrder setPurchaseOrder(Item item) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setDatecreated(AppTime.now());
        purchaseOrder.setPurchase_cost(Double.parseDouble(purchaseCost.getText()));
        purchaseOrder.setItem_name(name.getText());
        purchaseOrder.setQuantity(Integer.parseInt(quantity.getText()));
        purchaseOrder.setSku(item.getSku());
        purchaseOrder.setIn_stock(item.getIn_stock());
        purchaseOrder.setAmount(Integer.parseInt(quantity.getText()) * Double.parseDouble(purchaseCost.getText()));
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
