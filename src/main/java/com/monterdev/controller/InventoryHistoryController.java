package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.monterdev.model.History;
import com.monterdev.model.Item;
import com.monterdev.repository.CategoryRepository;
import com.monterdev.repository.HistoryRepository;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.monterdev.configuration.ItemsUIConfiguration.stockAdjustmentReasons;
import static com.monterdev.constants.HistoryConstants.PURCHASED_ITEM;
import static com.monterdev.constants.HistoryConstants.RELEASED_ITEM;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
@FxmlView("InventoryHistory.fxml")
@Getter
@Setter
public class InventoryHistoryController implements Initializable {

    @FXML
    private JFXButton btnExport;
    @FXML
    private JFXButton btnPrevious;
    @FXML
    private JFXButton btnNext;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXComboBox comboReasonType;
    @FXML
    private JFXComboBox comboItemCategory;
    @FXML
    private TableView tableResult;
    @FXML
    private TableColumn<History, String> columnDate;
    @FXML
    private TableColumn<History, String> columnName;
    @FXML
    private TableColumn<History, String> columnCategory;
    @FXML
    private TableColumn<History, String> columnReason;
    @FXML
    private TableColumn<History, String> columnAdjustment;
    @FXML
    private TableColumn<History, String> columnStockAfter;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private int currentPage = 0;
    private List<History> historyList = new ArrayList<>();
    private ObservableList<History> historyObservableList;
    private int maximumPage = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadReasonType();
        loadItemCategory();
        setTableColumnNames();
        loadTableView(where(hasReasonType((String) comboReasonType.getValue())).and(where(hasCategoryName((String) comboItemCategory.getValue()))));
        btnPreviousOnAction();
        btnNextOnAction();
        btnExportOnAction();
        btnCloseOnAction();
    }

    private void btnCloseOnAction() {
        btnClose.setOnAction(e->{
            Stage currentStage = (Stage) btnClose.getScene().getWindow();
            new StageLoader().load(InventoryListController.class, applicationContext, currentStage);
        });
    }

    private void btnExportOnAction() {
        btnExport.setOnAction(e->{
            if (Prompt.confirm("Are you sure you want to export all list of items?").get().getText().equalsIgnoreCase("OK")) {
                Stage stage = (Stage) btnExport.getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Comma-Separated Values (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extensionFilter);
                fileChooser.setTitle("Export Item List to CSV File");
                File file = fileChooser.showSaveDialog(stage);
                List<History> itemList = historyRepository.findAll();
                if (file != null) {
                    try {
                        Writer writer = new FileWriter(file.getAbsolutePath());
                        StatefulBeanToCsv statefulBeanToCsv = new StatefulBeanToCsvBuilder(writer)
                                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                                .build();
                        statefulBeanToCsv.write(itemList);
                        writer.close();
                        Prompt.success("Success! History backup was saved to " + file.getAbsolutePath());

                    } catch (IOException x) {
                        x.printStackTrace();
                    } catch (CsvRequiredFieldEmptyException f) {
                        f.printStackTrace();
                    } catch (CsvDataTypeMismatchException g) {
                        g.printStackTrace();
                    } finally {
                    }

                }
            }
        });
    }

    private void btnNextOnAction() {
        btnNext.setOnAction(e -> {
            if (currentPage < maximumPage) {
                loadTableView(where(hasReasonType((String) comboReasonType.getValue())).and(where(hasCategoryName((String) comboItemCategory.getValue()))));
                currentPage++;
            }else{
                currentPage = maximumPage-1;
                loadTableView(where(hasReasonType((String) comboReasonType.getValue())).and(where(hasCategoryName((String) comboItemCategory.getValue()))));
            }
        });
    }

    private void btnPreviousOnAction() {
        btnPrevious.setOnAction(e -> {
            if (currentPage > 0) {
                currentPage--;
                loadTableView(where(hasReasonType((String) comboReasonType.getValue())).and(where(hasCategoryName((String) comboItemCategory.getValue()))));
            } else {
                currentPage = 0;
                loadTableView(where(hasReasonType((String) comboReasonType.getValue())).and(where(hasCategoryName((String) comboItemCategory.getValue()))));
            }
        });
    }

    private Specification<Item> hasReasonType(String reasonType) {
        return (item, cq, cb) -> cb.like(item.get("reason"), "%" + reasonType + "%");
    }

    private Specification<Item> hasCategoryName(String categoryName) {
        return (item, cq, cb) -> cb.like(item.get("item_category"), "%" + categoryName + "%");
    }


    private void loadTableView(Specification specification) {
        Pageable initialHistoryPage = PageRequest.of(currentPage, 20);
        Page<History> initialItemPage = historyRepository.findAll(specification, initialHistoryPage);
        historyList = initialItemPage.getContent();
        historyObservableList = FXCollections.observableArrayList(historyList);
        tableResult.getItems().setAll(historyObservableList);
        maximumPage = initialItemPage.getTotalPages();
    }

    private void setTableColumnNames() {
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("item_name"));
        columnCategory.setCellValueFactory(new PropertyValueFactory<>("item_category"));
        columnReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        columnAdjustment.setCellValueFactory(new PropertyValueFactory<>("adjustment"));
        columnStockAfter.setCellValueFactory(new PropertyValueFactory<>("stock_after"));
    }

    private void loadReasonType() {
        comboReasonType.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                loadTableView(where(hasReasonType((String) comboReasonType.getValue())).and(where(hasCategoryName((String) comboItemCategory.getValue()))));
            }
        });
        stockAdjustmentReasons().stream().forEach(e -> {
            comboReasonType.getItems().add(e);
        });
        comboReasonType.getItems().add(PURCHASED_ITEM);
        comboReasonType.getItems().add(RELEASED_ITEM);
        comboReasonType.setValue(stockAdjustmentReasons().get(0));

    }

    private void loadItemCategory() {
        comboItemCategory.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                loadTableView(where(hasReasonType((String) comboReasonType.getValue())).and(where(hasCategoryName((String) comboItemCategory.getValue()))));
            }
        });
        categoryRepository.findAll().stream().forEach(e -> {
            comboItemCategory.getItems().add(e.getCategory_name());
        });
        comboItemCategory.setValue(categoryRepository.findAll().get(0).getCategory_name());
    }
}
