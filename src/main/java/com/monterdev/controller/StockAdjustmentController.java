package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.History;
import com.monterdev.model.Item;
import com.monterdev.repository.HistoryRepository;
import com.monterdev.repository.ItemsRepository;
import com.monterdev.util.AppTime;
import com.monterdev.util.DataUtil;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.monterdev.configuration.ItemsUIConfiguration.stockAdjustmentReasons;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
@FxmlView("StockAdjustment.fxml")
@Getter
public class StockAdjustmentController implements Initializable {

    @FXML
    private JFXTextField txtSearchItem;
    @FXML
    private JFXTextField txtCost;
    @FXML
    private JFXTextField txtInStock;
    @FXML
    private JFXTextField txtLowStock;
    @FXML
    private JFXTextField txtItemName;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXComboBox comboReason;
    @FXML
    private TableView<Item> tableResult;
    @FXML
    private TableColumn<Item, String> columnSku;
    @FXML
    private TableColumn<Item, String> columnName;
    @FXML
    private TableColumn<Item, String> columnCategory;
    @FXML
    private TableColumn<Item, String> columnCost;
    @FXML
    private TableColumn<Item, String> columnInStock;
    @FXML
    private TableColumn<Item, String> columnLowStock;
    @FXML
    private Label hiddenSku;

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private ItemsRepository itemsRepository;
    @Autowired
    private HistoryRepository historyRepository;

    private List<Item> itemList = new ArrayList<>();
    private ObservableList<Item> itemObservableList;
    private String selectedCellCategory;
    private String selectCellUnit;
    private String selectedQuantity;
    private List<String> stockAdjustmentReasonList = new ArrayList<>();
    private boolean isPositiveNumber  = false;
    private String oldInStockValue;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeStockAdjustmentReasons();
        setTableColumnNames();
        fillTableResult("");
        tableCellOnClick();
        txtSearchItemOnAction();
        btnSaveOnAction();
        btnCancelOnAction();
        inStockOnChange();
    }

    private void inStockOnChange() {
        txtInStock.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
            }
        });
    }

    private void initializeStockAdjustmentReasons() {
        stockAdjustmentReasonList=stockAdjustmentReasons();
        stockAdjustmentReasonList.stream().forEach(e->{
            comboReason.getItems().add(e);
        });
        comboReason.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                String selectedReason = (String) newValue;
                if(selectedReason.equalsIgnoreCase(stockAdjustmentReasonList.get(0))){
                    isPositiveNumber = true;
                }else{
                    isPositiveNumber = false;
                }
            }
        });
    }

    private void setTableColumnNames() {
        columnSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("item_name"));
        columnCategory.setCellValueFactory(new PropertyValueFactory<>("item_category"));
        columnCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        columnInStock.setCellValueFactory(new PropertyValueFactory<>("in_stock"));
        columnLowStock.setCellValueFactory(new PropertyValueFactory<>("low_stock"));
    }

    private void tableCellOnClick() {
        ObservableList<Item> cellData = FXCollections.observableArrayList();
        tableResult.setOnMouseClicked(cell -> {
            if (cell.getClickCount() == 2) {
                try{
                    selectedCellCategory = null;
                    selectCellUnit = null;
                    cellData.clear();
                    cellData.add(tableResult.getSelectionModel().getSelectedItem());
                    txtItemName.setText(cellData.get(0).getItem_name());
                    txtCost.setText(String.valueOf(cellData.get(0).getCost()));
                    txtInStock.setText(String.valueOf(cellData.get(0).getIn_stock()));
                    txtLowStock.setText(String.valueOf(cellData.get(0).getLow_stock()));
                    hiddenSku.setText(String.valueOf(cellData.get(0).getSku()));
                    selectedCellCategory = cellData.get(0).getItem_category();
                    selectCellUnit = cellData.get(0).getUnit();
                    oldInStockValue = String.valueOf(cellData.get(0).getIn_stock());
                    selectedQuantity = String.valueOf(cellData.get(0).getQuantity());
                }catch (IndexOutOfBoundsException e){
                    clearFields();
                    Prompt.failed("Error getting data!");
                }catch (NullPointerException e){
                    clearFields();
                    Prompt.failed("Error getting data!");
                }

            }
        });
    }

    private void clearFields(){
        txtItemName.setText("");
        txtCost.setText("");
        txtInStock.setText("");
        txtLowStock.setText("");
        hiddenSku.setText("");
        selectedCellCategory =null;
        selectCellUnit = null;
        isPositiveNumber = false;
        oldInStockValue = null;
        hiddenSku.setText("");
        selectedQuantity = null;
    }

    private void fillTableResult(String itemToSearch) {
        reloadItemListFromDatabase(itemToSearch);
        itemObservableList = FXCollections.observableArrayList(itemList);
        tableResult.getItems().setAll(itemObservableList);
    }

    private void reloadItemListFromDatabase(String itemToSearch) {
        Pageable initialItemPageList = PageRequest.of(0, 10);
        Page<Item> initialItemPage = itemsRepository.findAll(where(hasItemNameLike(itemToSearch)), initialItemPageList);
        itemList = initialItemPage.getContent();
    }

    private void resetFields(){
        txtSearchItem.setText("");
        txtItemName.setText("");
        txtCost.setText("");
        txtInStock.setText("");
        txtLowStock.setText("");
        selectCellUnit = null;
        selectedCellCategory = null;
    }

    private void btnSaveOnAction() {
        btnSave.setOnAction(e->{
            if(isFieldsValid()){
                Item mappedItem = mapItem();
                Item savedItem = itemsRepository.save(mappedItem);
                if(!ObjectUtils.isEmpty(savedItem)){
                    History mappedHistory = mapHistory(savedItem);
                    History savedHistory = historyRepository.save(mappedHistory);
                    if(!ObjectUtils.isEmpty(savedHistory)){
                        Prompt.success("Stock updated successfully!");
                    }else{
                        Prompt.failed("Some error occurred during saving!");
                    }
                    fillTableResult("");
                    resetFields();
                }
            }
        });
    }

    private History mapHistory(Item savedItem) {
        History history = new History();
        history.setItem_name(savedItem.getItem_name());
        history.setItem_category(savedItem.getItem_category());
        history.setReason((String)comboReason.getValue());
        history.setStock_after(savedItem.getIn_stock());
        history.setDate(AppTime.now());
        if(isPositiveNumber){
            history.setAdjustment(Integer.parseInt(txtInStock.getText()));
        }else{
            int oldInStockIntegerValue = Integer.parseInt(oldInStockValue);
            int newInStockIntegerValue = Integer.parseInt(txtInStock.getText());
            if(newInStockIntegerValue>oldInStockIntegerValue){
                history.setAdjustment(newInStockIntegerValue-oldInStockIntegerValue);
            }else if(newInStockIntegerValue==oldInStockIntegerValue){
                history.setAdjustment(0);
            }
            else{
                history.setAdjustment((oldInStockIntegerValue-newInStockIntegerValue) * -1);
            }
        }
        return history;
    }

    private Item mapItem() {
        Item item = new Item();
        item.setItem_category(selectedCellCategory);
        item.setItem_name(txtItemName.getText());
        item.setCost(DataUtil.formatDouble(txtCost.getText()));
        item.setIn_stock(Integer.parseInt(txtInStock.getText()));
        item.setLow_stock(Integer.parseInt(txtLowStock.getText()));
        item.setUnit(selectCellUnit);
        item.setSku(Integer.parseInt(hiddenSku.getText()));
        item.setQuantity(Integer.parseInt(selectedQuantity));
        return item;
    }

    private boolean isFieldsValid() {
        int validFieldCounts=0;
        if(ObjectUtils.isEmpty(txtItemName.getText())){
            Prompt.failed("Item name is Empty!");
        }else{
            validFieldCounts++;
        }
        try{

            if(ObjectUtils.isEmpty(txtCost.getText())
                    || DataUtil.formatDouble(txtCost.getText())<0){
                Prompt.failed("Item Cost is not valid!");
            }else{
                validFieldCounts++;
            }

            if(ObjectUtils.isEmpty(txtInStock.getText())
                    || (Integer.parseInt(txtInStock.getText())<0)){
                Prompt.failed("Item In Stock is not valid!");
            }else{
                validFieldCounts++;
            }

            if(ObjectUtils.isEmpty(txtLowStock.getText())
            || (Integer.parseInt(txtLowStock.getText()))<0){
                Prompt.failed("Item Low Stock is not valid!");
            }else{
                validFieldCounts++;
            }
        }catch (NumberFormatException e){
            Prompt.failed("Some number type fields are not valid!");
        }catch (NullPointerException e){
            Prompt.failed("Some number type fields are not valid!");
        }catch (Exception e){
            Prompt.failed("Some number type fields are not valid!");
        }


        if(ObjectUtils.isEmpty(comboReason.getValue())){
            Prompt.failed("Stock adjustment reason is Empty!");
        }else{
            validFieldCounts++;
        }
        return validFieldCounts==5?true:false;
    }

    private void txtSearchItemOnAction() {
        txtSearchItem.textProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                fillTableResult(newValue);
            }
        });
    }

    private Specification<Item> hasItemNameLike(String itemName) {
        return (item, cq, cb) -> cb.like(item.get("item_name"), "%" + itemName + "%");
    }

    private void btnCancelOnAction() {
        btnCancel.setOnAction(e -> {
            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            new StageLoader().load(MainDashboardController.class, applicationContext, currentStage);
        });
    }
}
