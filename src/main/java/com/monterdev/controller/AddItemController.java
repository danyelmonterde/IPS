package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.Item;
import com.monterdev.model.ItemCategory;
import com.monterdev.model.Unit;
import com.monterdev.repository.ItemsRepository;
import com.monterdev.util.Prompt;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private Iterable<ItemCategory> itemCategories;

    @Autowired
    private List<Unit> unitList;

    @Autowired
    private ItemsRepository itemsRepository;

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
            if(!ObjectUtils.isEmpty(item)){
                if(!ObjectUtils.isEmpty(itemsRepository.save(item))){
                    Prompt.success("Item was successfully added!");
                    Stage stage = (Stage) name.getScene().getWindow();
                    stage.close();
                }else{
                    Prompt.failed("Item was not saved!");
                }
            }else{
                Prompt.failed("Please fill in all fields!");
            }
        });

    }

    private void cancelOnAction() {
        cancel.setOnAction(e->{
            resetOnAction();
            Stage stage = (Stage) name.getScene().getWindow();
            stage.close();
        });
    }

    private void resetOnAction() {
        reset.setOnAction(e->{
            name.setText("");
            quantity.setText("0");
            sku.setText("0");
            purchaseCost.setText("0");
            totalAmount.setText("0");
            lowStock.setText("0");

            item.setItem_name("");
            item.setItem_category("");
            item.setLow_stock(0);
            item.setTag(null);
            item.setQuantity(0);
            item.setCost(0);
            item.setIn_stock(0);
            item.setUnit("");
            item.setSku(0);
        });
    }

    private void viewInventoryHistoryOnAction() {
    }

    private void lowStockOnChange() {
        lowStock.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                item.setLow_stock(Integer.parseInt(newValue));
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
                    double itemPurchaseCost = Double.parseDouble(newValue);
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
                    double itemPurchaseCost = Double.parseDouble(purchaseCost.getText());
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
