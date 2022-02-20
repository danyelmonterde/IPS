package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.app.MainJavaFXApplication;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.monterdev.constants.FileTypeConstants.JPEG_FORMAT;
import static com.monterdev.constants.InventoryTypeConstants.ALL_CATEGORIES;
import static com.monterdev.constants.TextFieldValidatorConstants.*;

@Component
@FxmlView("AddItem.fxml")
@Getter
public class AddItemController implements Initializable {

    @FXML
    private JFXTextField name;
    @FXML
    private JFXComboBox itemCategoryCombo;
    @FXML
    private JFXTextField quantity;
    @FXML
    private JFXTextField purchaseCost;
    @FXML
    private JFXComboBox comboUnit;
    @FXML
    private JFXTextField lowStock;
    @FXML
    private JFXTextField totalAmount;
    @FXML
    private JFXButton cancel;
    @FXML
    private JFXButton save;
    @FXML
    private JFXButton reset;

    private Iterable<ItemCategory> itemCategories;

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    private ItemsRepository itemsRepository;
    private QrcodeRepository qrcodeRepository;
    private CategoryRepository categoryRepository;
    private BalanceRepository balanceRepository;
    private UnitRepository unitRepository;
    private Item item;
    private List<Unit> unitList;

    private static final Logger LOGGER = LoggerFactory.getLogger(MainJavaFXApplication.class);

    @Autowired
    public AddItemController(ItemsRepository itemsRepository, QrcodeRepository qrcodeRepository, CategoryRepository categoryRepository, BalanceRepository balanceRepository, UnitRepository unitRepository) {
        this.itemsRepository = itemsRepository;
        this.qrcodeRepository = qrcodeRepository;
        this.categoryRepository = categoryRepository;
        this.balanceRepository = balanceRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (item == null) {
            item = new Item();
        }

        loadUnits();
        loadItemCategories();
        itemNameOnChange();
        quantityOnChange();
        purchaseCostOnChange();
        totalAmountOnChange();
        lowStockOnChange();
        resetOnAction();
        cancelOnAction();
        saveOnAction();

    }

    private void saveOnAction() {
        save.setOnAction(e -> {
            if (!ObjectUtils.isEmpty(item.getItem_name()) && !ObjectUtils.isEmpty(item.getUnit()) && !ObjectUtils.isEmpty(item.getItem_category())) {
                //Item name, Item unit and category are  required
                try {
                    Item savedItem = itemsRepository.save(item);
                    if (!ObjectUtils.isEmpty(savedItem)) {
                        Balance balance = new Balance();
                            //Balance is being adjusted every new item being added 
                            Balance bal = balanceRepository.getBalanceOfCurrentInventoryMonth(AppTime.getMonth(), String.valueOf(AppTime.getYear()), savedItem.getItem_category());
                            if (!(savedItem.getItem_category().equalsIgnoreCase(ALL_CATEGORIES))) {
                                if (!ObjectUtils.isEmpty(bal)) {
                                    balance.setYear(String.valueOf(AppTime.getYear()));
                                    balance.setMonth(AppTime.getMonth());
                                    balance.setDatecreated(String.valueOf(AppTime.now()));
                                    balance.setId(bal.getId());
                                    balance.setBeginbalance(bal.getBeginbalance());
                                    balance.setCategory(bal.getCategory());
                                    String x = itemsRepository.getEndBalanceByInventoryType(savedItem.getItem_category());
                                    balance.setEndbalance(ObjectUtils.isEmpty(x) ? 0 : DataUtil.formatDouble(x));
                                    balanceRepository.save(balance);//lagay sa loop
                                } else {
                                    balance.setYear(String.valueOf(AppTime.getYear()));
                                    balance.setMonth(AppTime.getMonth());
                                    balance.setDatecreated(String.valueOf(AppTime.now()));
                                    balance.setId(0);
                                    String b = itemsRepository.getEndBalanceByInventoryType(savedItem.getItem_category());
                                    balance.setBeginbalance(ObjectUtils.isEmpty(b) ? 0 : DataUtil.formatDouble(b));
                                    balance.setCategory(savedItem.getItem_category());
                                    balance.setEndbalance(ObjectUtils.isEmpty(b) ? 0 : DataUtil.formatDouble(b));
                                    balanceRepository.save(balance);//lagay sa loop
                                }
                            }



                        Barcode barcode = setBarcodeData(item);
                        qrcodeRepository.save(barcode);
                        Prompt.success("Item was added successfully!");
                        Stage stage = (Stage) name.getScene().getWindow();
                        new StageLoader().loadTest(MainDashboardController.class, applicationContext, stage);
                        try {
                            BarCodeUtil.saveBarCode(String.valueOf(savedItem.getSku()), String.valueOf(savedItem.getSku()));
                        } catch (Exception ioException) {

                        } finally {
                            resetItem();
                        }
                    } else {
                        Prompt.failed("Item was not saved!");
                    }
                } catch (DataIntegrityViolationException dataIntegrityViolationException) {
                    Prompt.failed("Item existed already!");
                } catch (Exception exception){
                    Prompt.failed("An error occurred while saving item!");
                }

            } else {
                Prompt.failed("Please fill in all fields!");
            }
        });

    }

    private Barcode setBarcodeData(Item savedItem) {
        Barcode barcode = new Barcode();
        barcode.setBar_code_path(BarCodeUtil.filePath + "\\" + savedItem.getSku() + JPEG_FORMAT);
        barcode.setSku(savedItem.getSku());
        barcode.setDate_created(AppTime.now());
        return barcode;
    }


    private void cancelOnAction() {
        cancel.setOnAction(e -> {
            resetItem();
            Stage stage = (Stage) name.getScene().getWindow();
            new StageLoader().loadTest(MainDashboardController.class, applicationContext, stage);
        });
    }

    private void resetOnAction() {
        reset.setOnAction(e -> {
            resetItem();
        });
    }

    private void resetItem() {
        name.setText("");
        quantity.setText("0");
        purchaseCost.setText("0");
        totalAmount.setText("0");
        lowStock.setText("1");

        item.setItem_name("");
        item.setItem_category("");
        item.setLow_stock(1);
        item.setTag(null);
        item.setQuantity(0);
        item.setCost(0);
        item.setIn_stock(0);
        item.setUnit("");
        item.setSku(0);
    }

    private void lowStockOnChange() {
        lowStock.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                if (NumberUtils.isParsable(lowStock.getText()) && !ObjectUtils.isEmpty(lowStock.getText())) {
                    //VALID TEXT FIELD
                    lowStock.setText(lowStock.getText().replaceAll(NEGATIVE_WHOLE_NUMBERS, ""));
                    item.setLow_stock(Math.abs(Integer.parseInt(lowStock.getText())));
                } else if (ObjectUtils.isEmpty(lowStock.getText())) {
                    //EMPTY TEXT FIELD
                    item.setLow_stock(0);
                    lowStock.setText(lowStock.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                } else {
                    //INVALID TEXT FIELD
                    lowStock.setText(lowStock.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                    lowStock.setText(lowStock.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
                }
            } catch (IllegalArgumentException exception) {
                quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                quantity.setText(quantity.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
            }

        });
    }

    private void totalAmountOnChange() {
        totalAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {

            }
        });
    }


    private void purchaseCostOnChange() {

        purchaseCost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                try {
                    if (!ObjectUtils.isEmpty(purchaseCost.getText())) {
                        //VALID TEXT FIELD
                        purchaseCost.setText(purchaseCost.getText().replaceAll(FLOAT_NUMBERS_REGEX_EXCLUDE, ""));
                        purchaseCost.setText(purchaseCost.getText().replaceAll(NEGATIVE_WHOLE_NUMBERS, ""));
                        double itemPurchaseCost = Math.abs(Double.parseDouble(newValue));
                        double itemQuantity = Math.abs(Integer.parseInt(quantity.getText()));
                        double itemTotalAmount = itemPurchaseCost * itemQuantity;

                        totalAmount.setText(String.valueOf(itemTotalAmount));
                        item.setCost(itemPurchaseCost);

                    } else {
                        //EMPTY TEXT FIELD
                        item.setCost(0);
                        totalAmount.setText("0");
                    }

                } catch (IllegalArgumentException exception) {
                    purchaseCost.setText(purchaseCost.getText().replaceAll(FLOAT_NUMBERS_REGEX_EXCLUDE, ""));
                    purchaseCost.setText(purchaseCost.getText().replaceAll(NEGATIVE_WHOLE_NUMBERS, ""));
                }

            }
        });
    }

    private void quantityOnChange() {
        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (NumberUtils.isParsable(quantity.getText()) && !ObjectUtils.isEmpty(quantity.getText())) {
                    //VALID TEXT FIELD
                    quantity.setText(quantity.getText().replaceAll(NEGATIVE_WHOLE_NUMBERS, ""));
                    double itemPurchaseCost = Math.abs(DataUtil.formatDouble(purchaseCost.getText()));
                    int itemQuantity = Math.abs(Integer.parseInt(quantity.getText()));
                    double itemTotalAmount = itemPurchaseCost * itemQuantity;

                    totalAmount.setText(String.valueOf(itemTotalAmount));
                    item.setQuantity(itemQuantity);
                    item.setIn_stock(itemQuantity);

                } else if (ObjectUtils.isEmpty(quantity.getText())) {
                    //EMPTY TEXT FIELD
                    quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                    item.setQuantity(0);
                    item.setIn_stock(0);
                    totalAmount.setText("0.0");
                } else {
                    //INVALID TEXT FIELD
                    quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                    quantity.setText(quantity.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
                }
            } catch (NumberFormatException exception) {
                quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                quantity.setText(quantity.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
            } catch (IllegalArgumentException exception) {
                quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                quantity.setText(quantity.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
            }

        });
    }

    private void itemNameOnChange() {
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                item.setItem_name(newValue);
            }
        });
    }

    private void loadItemCategories() {
        itemCategories = categoryRepository.findAll();
        itemCategories.forEach(category -> {
            itemCategoryCombo.getItems().add(category.getCategory_name());
        });

        itemCategoryCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                item.setItem_category((String) newValue);
            }
        });

        if (itemCategories.iterator().hasNext()) {
            itemCategoryCombo.setValue(itemCategories.iterator().next().getCategory_name());
        }


    }

    private void loadUnits() {
        if(ObjectUtils.isEmpty(unitList)){
            unitList = unitRepository.findAllItemUnits();
        }
        unitList.stream().forEach(unit -> {
            comboUnit.getItems().add(unit.getUnit());
        });
        comboUnit.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                item.setUnit((String) newValue);
            }
        });
    }
}
