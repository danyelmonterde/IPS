package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.Item;
import com.monterdev.model.ItemCategory;
import com.monterdev.model.Qrcode;
import com.monterdev.model.Unit;
import com.monterdev.repository.CategoryRepository;
import com.monterdev.repository.ItemsRepository;
import com.monterdev.repository.QrcodeRepository;
import com.monterdev.util.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    private JFXTextField sku;

    @FXML
    private JFXComboBox comboUnit;

    @FXML
    private JFXTextField lowStock;

    @FXML
    private JFXTextField totalAmount;

    @FXML
    private JFXButton view;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXButton save;

    @FXML
    private JFXButton reset;


    private Iterable<ItemCategory> itemCategories;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private List<Unit> unitList;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private QrcodeRepository qrcodeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Item item;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(item == null){
            item = new Item();
        }

        loadUnits();
        loadItemCategories();
        itemNameOnChange();
        quantityOnChange();
        purchaseCostOnChange();
        totalAmountOnChange();
        lowStockOnChange();
        viewInventoryHistoryOnAction();
        resetOnAction();
        cancelOnAction();
        saveOnAction();


    }

    private void saveOnAction() {
        save.setOnAction(e->{
            if(!ObjectUtils.isEmpty(item.getItem_name()) && !ObjectUtils.isEmpty(item.getUnit())){
                try{
                    if(!ObjectUtils.isEmpty(itemsRepository.save(item))){
                        Qrcode qrcode = setQrCodeData(item);
                        qrcodeRepository.save(qrcode);
                        Prompt.success("Item was successfully added!");
                        Stage stage = (Stage) name.getScene().getWindow();
                        new StageLoader().load(MainDashboardController.class, applicationContext,stage);
                        try {
                            QrCodeUtil.saveQrCode(String.valueOf(item.getSku()),item.getSku() +"-"+item.getItem_name());
                        } catch (Exception ioException) {

                        }finally {
                            resetItem();
                        }
                    }else{
                        Prompt.failed("Item was not saved!");
                    }
                }catch (DataIntegrityViolationException dataIntegrityViolationException){
                    Prompt.failed("Item existed already!");
                }

            }else{
                Prompt.failed("Please fill in all fields!");
            }
        });

    }

    private Qrcode setQrCodeData(Item savedItem) {
        Qrcode qrcode = new Qrcode();
        qrcode.setQr_code_path(QrCodeUtil.filePath + "\\" +savedItem.getSku()+"-"+ savedItem.getItem_name() + ".jpg");
        qrcode.setSku(savedItem.getSku());
        qrcode.setDate_created(AppTime.now());
        return qrcode;
    }


    private void cancelOnAction() {
        cancel.setOnAction(e->{
            resetOnAction();
            Stage stage = (Stage) name.getScene().getWindow();
            new StageLoader().load(MainDashboardController.class, applicationContext,stage);
        });
    }

    private void resetOnAction() {
        reset.setOnAction(e->{
            resetItem();
        });
    }

    private void resetItem(){
        name.setText("");
        quantity.setText("0");
        sku.setText("0");
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

    private void viewInventoryHistoryOnAction() {
    }

    private void lowStockOnChange() {
        lowStock.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                try{
                    item.setLow_stock(Integer.parseInt(newValue));
                }catch (NumberFormatException e){
                    item.setLow_stock(1);
                }

            }
        });
    }

    private void totalAmountOnChange() {
        totalAmount.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){

            }
        });
    }

    private void purchaseCostOnChange() {
        purchaseCost.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                try{
                    double itemPurchaseCost = DataUtil.formatDouble(newValue);
                    double itemQuantity = Integer.parseInt(quantity.getText());
                    double itemTotalAmount = itemPurchaseCost * itemQuantity;

                    totalAmount.setText(String.valueOf(itemTotalAmount));
                    item.setCost(itemPurchaseCost);
                }catch (NumberFormatException exception){

                }

            }
        });
    }

    private void quantityOnChange() {
        quantity.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                try{
                    double itemPurchaseCost = DataUtil.formatDouble(purchaseCost.getText());
                    double itemQuantity = Integer.parseInt(newValue);
                    double itemTotalAmount = itemPurchaseCost * itemQuantity;

                    totalAmount.setText(String.valueOf(itemTotalAmount));
                    item.setQuantity(Integer.parseInt(newValue));
                    item.setIn_stock(Integer.parseInt(newValue));
                }catch (NumberFormatException exception){

                }

            }
        });
    }

    private void itemNameOnChange() {
        name.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                item.setItem_name(newValue);
            }
        });
    }

    private void loadItemCategories() {
        itemCategories = categoryRepository.findAll();
        itemCategories.forEach(category->{
            itemCategoryCombo.getItems().add(category.getCategory_name());
        });

        itemCategoryCombo.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                item.setItem_category((String) newValue);
            }
        });
    }

    private void loadUnits() {
        unitList.stream().forEach(unit -> {
            comboUnit.getItems().add(unit.getUnit());
        });
        comboUnit.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                item.setUnit((String) newValue);
            }
        });
    }
}
