package com.monterdev.controller;

import com.jfoenix.controls.*;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
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


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.monterdev.constants.ItemsUIConfiguration.*;
import static com.monterdev.constants.ItemsUIConfiguration.ITEMS_SEARCH_BUTTON_NAME;
import static com.monterdev.util.ComponentCreator.addJFXButton;
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
    private JFXTextField price;

    @FXML
    private JFXTextField cost;

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

    private List<Item> resultsList;

    private List<String> responseList;

    public void initialize() {

        selectedItem = applicationContext.getBean(Item.class);
        name.setText(selectedItem.getItem_name());
        price.setText(Double.toString(selectedItem.getPrice()));
        cost.setText(Double.toString(selectedItem.getCost()));
        sku.setText(Integer.toString(selectedItem.getSku()));
        inStock.setText(Integer.toString(selectedItem.getIn_stock()));
        lowStock.setText(Integer.toString(selectedItem.getLow_stock()));
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


        SubCategoryDetail subCategoryDetail = subCategoryDetailsRepository.findBySubCategoryDetail(selectedItem.getSub_category_detail());
        SubCategoryHeader subCategoryHeader = subCategoryHeaderRepository.findBySubCategoryHeader(subCategoryDetail.getSub_category_header());
        Category category = categoryRepository.findByCategory(subCategoryHeader.getCategory());

        loadCategories(subCategoryDetail, subCategoryHeader, category);

        itemNameonChange();

        priceOnChange();

        skuOnChange();

        inStockOnChange();

        lowStockOnChange();

        subCategoryDetailOnChange();

        trackStockHbox.setVisible(false);
    }


    public void create(ActionEvent actionEvent) {
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

        //TOP HBOX

        JFXButton addItem = addJFXButton(ITEMS_ADD_ITEM_BUTTON_NAME, JFXButton.ButtonType.RAISED);
        JFXButton importButton = addJFXButton(ITEMS_IMPORT_BUTTON_NAME, JFXButton.ButtonType.FLAT);
        JFXButton export = addJFXButton(ITEMS_EXPORT_BUTTON_NAME, JFXButton.ButtonType.FLAT);
        JFXButton settings = addJFXButton(ITEMS_SETTINGS_BUTTON_NAME, JFXButton.ButtonType.FLAT);
        JFXButton searchItemsButton = addJFXButton(ITEMS_SEARCH_BUTTON_NAME, JFXButton.ButtonType.FLAT);
        new StageLoader().load(EditItemController.class, actionEvent, applicationContext);
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
                            searchItem(newvalue);
                            VBox vbox = (VBox) ((AnchorPane) node).getChildren().get(0);
                            JFXListView listView = (JFXListView) vbox.getChildren().get(0);
                            listView.getItems().clear();
                            listView.getItems().addAll(responseList);

                        } else if (node instanceof JFXTextField && searchGroupMainContainer.getChildren().size() < 3) {
                            AnchorPane searchBox = createSearchBox();
                            searchGroupMainContainer.getChildren().add(searchBox);
                            VBox.setMargin(searchBox, new Insets(0.0, 0.0, 20.0, 20.0));
                        }
                    }
                } catch (ConcurrentModificationException e) {

                }

            }else if(ObjectUtils.isEmpty(newvalue)){
                searchGroupMainContainer.getChildren().remove(2);
                responseList.clear();
            }

        });
    }

    private void searchItem(String text) {
        resultsList = itemLists;
        responseList = new ArrayList<>();
        responseList.clear();
        StringBuilder regexTextBuilder = new StringBuilder();
        regexTextBuilder.setLength(0);
        if(text.matches(".*\\s.*") && text.length()>=5){
            String[] splittedText = text.split("\\s+");
            regexTextBuilder.append(".*");
            for(int ctr=0;ctr<splittedText.length;ctr++){
                regexTextBuilder.append(splittedText[ctr].toCharArray()[1]);
                regexTextBuilder.append(splittedText[ctr].toCharArray()[2]);
                regexTextBuilder.append(".");
                regexTextBuilder.append(splittedText[ctr].toCharArray()[4]);
                regexTextBuilder.append(".*");
                System.out.println(regexTextBuilder.toString());
            }
        }else if(text.length()>=5){
            regexTextBuilder.append(text.toCharArray()[1]);
            regexTextBuilder.append(text.toCharArray()[2]);
            regexTextBuilder.append(".");
            regexTextBuilder.append(text.toCharArray()[4]);
            regexTextBuilder.append(".*");
        }
        Pattern p = Pattern.compile(regexTextBuilder.toString(), Pattern.CASE_INSENSITIVE);
        for (int ctr = 0; ctr < resultsList.size(); ctr++) {
            Matcher m = p.matcher(resultsList.get(ctr).getItem_name());
            if (m.matches()) {

                responseList.add(resultsList.get(ctr).getItem_name().concat("  |  ")
                        .concat(Integer.toString(resultsList.get(ctr).getSku())));
                Collections.sort(responseList);
                //break;)
            }
        }


    }

    private void priceOnChange() {
        price.textProperty().addListener((observable, oldvalue, newvalue) -> {
            if (oldvalue != newvalue) {
                selectedItem.setPrice(Double.parseDouble(newvalue));
            }


        });
    }

    private void skuOnChange() {
        sku.textProperty().addListener((obs, old, newv) -> {
            if (old != newv) {
                selectedItem.setPrice(Double.parseDouble(newv));
            }
        });
    }

    private void inStockOnChange() {
        inStock.textProperty().addListener((obs, old, newv) -> {
            if (old != newv) {
                selectedItem.setIn_stock(Integer.parseInt(newv));
            }
        });
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
                            .item_name(selectedItem.getItem_name())
                            .in_stock(selectedItem.getIn_stock())
                            .low_stock(selectedItem.getLow_stock())
                            .margin(selectedItem.getMargin())
                            .sku(selectedItem.getSku())
                            .price(selectedItem.getPrice())
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
        selectedItem.setTag(null);
        selectedItem.setItem_name("");
        selectedItem.setLow_stock(0);
        selectedItem.setPrice(0.0);
        selectedItem.setSku(0);
        selectedItem.setMargin(0.0);
        selectedItem.setCost(0.0);
        selectedItem.setIn_stock(0);
        selectedItem.setSub_category_detail("TEST-CATEGORY-1");
        name.setText("");
        lowStock.setText("0");
        price.setText("0");
        cost.setText("0");
        inStock.setText("0");
    }

    public void cancel(ActionEvent actionEvent) {
        selectedItem = new Item();
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    public void save(ActionEvent actionEvent) {
        selectedItem.setTag(null);
        Item item = itemsRepository.save(selectedItem);
        if (!ObjectUtils.isEmpty(item)) {
            Prompt.success("Data updated!");
            selectedItem = new Item();
            Stage stage = (Stage) save.getScene().getWindow();
            stage.close();
        } else {
            Prompt.failed("Data was not updated successfully!");
        }
    }

    public void showTrackStock(ActionEvent actionEvent) {
        if (trackStock.isSelected()) {
            trackStockHbox.setVisible(true);
        } else {
            trackStockHbox.setVisible(false);
        }
    }
}
