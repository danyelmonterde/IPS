package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.CartItem;
import com.monterdev.model.Item;
import com.monterdev.repository.ItemsRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("PointOfSale.fxml")
@Getter
public class PointOfSaleController implements Initializable {

    @FXML
    private JFXTextField txtInputField;

    @FXML
    private TableView<CartItem> cart;

    @FXML
    private TableColumn<CartItem, Integer> itemNumber;

    @FXML
    private TableColumn<CartItem, Integer> sku;

    @FXML
    private TableColumn<CartItem, String> itemName;

    @FXML
    private TableColumn<CartItem, String> quantity;

    @FXML
    private TableColumn<CartItem, Double> averageCost;

    @FXML
    private TableColumn<CartItem, JFXButton> action;

    @FXML
    private JFXTextField grandTotal;

    @FXML
    private JFXButton buttonBuy;

    @FXML
    private JFXButton buttonExit;

    @FXML
    private JFXButton btnClear;

    @Autowired
    private ItemsRepository itemsRepository;

    public static ObservableList<CartItem> data_table;
    public static TableView<CartItem> table_info_app;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeScanField();
        initializeGrandTotal();
        table_info_app = cart;
        itemNumber.setCellValueFactory(new PropertyValueFactory<>("itemNumber"));
        sku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        averageCost.setCellValueFactory(new PropertyValueFactory<>("averageCost"));
        action.setCellValueFactory(new PropertyValueFactory<>("action"));

        data_table = FXCollections.observableArrayList();

        quantity.setCellFactory(TextFieldTableCell.forTableColumn());
        quantity.setOnEditCommit(e ->
                e.getTableView().getItems().get(e.getTablePosition().getRow())
                        .setQuantity(e.getNewValue())
        );


        cart.setEditable(true);
        cart.setItems(data_table);
    }

    private void initializeGrandTotal() {
        grandTotal.textProperty().addListener((event,oldValue,newValue)->{
            if(oldValue!=newValue){

            }
        });
    }

    private void initializeScanField() {
        txtInputField.textProperty().addListener((event, oldValue, newValue) -> {
            if (oldValue != newValue) {
                int sku = Integer.parseInt(newValue);
                if(sku==9999){
                    //if sku is 9999, recompute total amount without adding item to cart
                    computeTotalAmount();
                }else{
                    Optional<Item> optionalItem = itemsRepository.findById(sku);
                    if (optionalItem.isPresent()) {
                        Item item = optionalItem.get();
                        CartItem cartItem = new CartItem();
                        cartItem.setItemName(item.getItem_name());
                        cartItem.setSku(item.getSku());
                        cartItem.setAverageCost(item.getCost());
                        cartItem.setQuantity(String.valueOf(1));
                        JFXButton removeButton = new JFXButton("Remove");
                        removeButton.setOnAction(e -> {
                            CartItem selectedItemOnCart = cart.getSelectionModel().getSelectedItem();
                            cart.getItems().remove(selectedItemOnCart);
                        });
                        cartItem.setAction(removeButton);
                        data_table.add(cartItem);
                        cart.refresh();

                        computeTotalAmount();
                    }
                }

            }
        });
    }

    private void computeTotalAmount(){
        double totalAmountOfCart = data_table.stream()
                .map(x-> x.getAverageCost() * Double.parseDouble(x.getQuantity()))
                .reduce(0.0,Double::sum);
        grandTotal.setText(String.valueOf(totalAmountOfCart));
    }
}

