package com.monterdev.controller;

import com.google.zxing.NotFoundException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.constants.GlobalConfiguration;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.*;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import lombok.Getter;
import lombok.SneakyThrows;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.monterdev.constants.GlobalConfiguration.*;
import static com.monterdev.util.ComponentCreator.createButtonWithoutText;
import static com.monterdev.util.ComponentCreator.createTextField;
import static com.monterdev.util.QrCodeUtil.readQrCodeImage;

@Component
@FxmlView("MainDashboard.fxml")
@Getter
public class MainDashboardController implements Initializable {

    @FXML
    private ImageView Exit;

    @FXML
    private Label Menu;

    @FXML
    private Label MenuClose;

    @FXML
    private AnchorPane sidebarAnchorpane;

    @FXML
    private AnchorPane mainAnchorpane;

    @FXML
    private AnchorPane searchPane;

    @FXML
    private HBox topHbox;

    @FXML
    private HBox midHbox;

    @FXML
    private HBox bottomHbox;

    @FXML
    private JFXButton sidebarReports;

    @FXML
    private JFXButton sidebarItems;

    @FXML
    private JFXButton sidebarDashboard;

    @FXML
    private JFXButton sidebarInventory;

    @FXML
    private JFXButton sidebarCustomers;

    @FXML
    private JFXButton sidebarSettings;

    @FXML
    private Label currentLocationBanner;

    @FXML
    private Label companyName;

    @FXML
    private Label companyCopyright;

    @FXML
    private JFXButton sidebarLogout;

    @FXML
    private JFXButton dashboardIconMenu;

    @FXML
    private JFXButton addItemMenu;

    @FXML
    private JFXButton reportMenu;

    @FXML
    private JFXButton backupMenu;

    @FXML
    private Label sku;

    @Autowired
    private ItemListController itemListController;

    @Autowired
    private EditItemController editItemController;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private Item selectedItem;

    @Autowired
    @Qualifier("itemLists")
    private List<Item> itemLists;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private RisRepository risRepository;

    @Autowired
    private SearchUtil searchUtil;

    @Autowired
    private ReportUtil reportUtil;

    @Autowired
    private RequisitionIssueSlip requisitionIssueSlip;

    @Autowired
    private RisTypeFieldsRepository risTypeFieldsRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private List<RisTypeFields> risTypeFieldsList;

    @Autowired
    private Customer customer;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UnitRepository unitRepository;

    private List<String> responseList = new ArrayList<>();

    private static int rowIndex = 0;

    private int indexToBeRemoved = 0;

    private List<Item> itemCart = new ArrayList<>();

    private boolean isEmpty = true;

    private JFXListView listView = new JFXListView();

    private TranslateTransition slide = new TranslateTransition();

    private TranslateTransition slideMainAnchorpane = new TranslateTransition();

    private TranslateTransition slideSearchPane = new TranslateTransition();

    @FXML
    private JFXButton releaseItemButton;

    private double totalSales =0; //;total amount na may 20% na patong pag new

    private double totalCost = 0; //lahat ng average cost

    private double totalIncome = 0; //totalSales - totalCost

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Exit.setOnMouseClicked(event -> {
            System.exit(-176);
        });
        sidebarAnchorpane.setTranslateX(0);
        mainAnchorpane.setTranslateX(0);
        searchPane.setTranslateX(0);
        Menu.setOnMouseClicked(event -> {

            slide.setDuration(Duration.seconds(0.4));
            slideMainAnchorpane.setDuration(Duration.seconds(0.4));
            slideSearchPane.setDuration(Duration.seconds(0.4));

            slide.setNode(sidebarAnchorpane);
            slideMainAnchorpane.setNode(mainAnchorpane);
            slideSearchPane.setNode(searchPane);

            slide.setToX(-176);
            slideMainAnchorpane.setToX(-176);
            slideSearchPane.setToX(-176);

            slide.play();
            slideMainAnchorpane.play();
            slideSearchPane.play();

            sidebarAnchorpane.setTranslateX(0);
            mainAnchorpane.setTranslateX(0);
            searchPane.setTranslateX(0);

            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(false);
                MenuClose.setVisible(true);
            });

        });

        MenuClose.setOnMouseClicked(event -> {
            //  TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(sidebarAnchorpane);

            // TranslateTransition slideMainAnchorpane = new TranslateTransition();
            slideMainAnchorpane.setDuration(Duration.seconds(0.4));
            slideMainAnchorpane.setNode(mainAnchorpane);

            //  TranslateTransition slideSearchPane = new TranslateTransition();
            slideSearchPane.setDuration(Duration.seconds(0.4));
            slideSearchPane.setNode(searchPane);

            slide.setToX(0);
            slide.play();

            slideMainAnchorpane.setToX(0);
            slideMainAnchorpane.play();

            slideSearchPane.setToX(0);
            slideSearchPane.play();

            sidebarAnchorpane.setTranslateX(-176);
            mainAnchorpane.setTranslateX(-176);
            searchPane.setTranslateX(-176);


            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(true);
                MenuClose.setVisible(false);
            });


        });
        sku.textProperty().addListener((obs, oldValue, newValue) -> {

            if (!ObjectUtils.isEmpty(sku.getText())) {
                boolean isPresent = itemLists.stream().filter(e -> e.getSku() == Integer.parseInt(newValue)).findFirst().isPresent();
                if (isPresent) {
                    ObservableList<Node> nodeStream = midHbox.getChildren();
                    for (Node node : nodeStream) {
                        if (node instanceof ScrollPane) {
                            Node node2 = ((ScrollPane) node).getContent();
                            if (node2 instanceof AnchorPane) {
                                for (Node node3 : ((AnchorPane) node2).getChildren()) {
                                    if (node3 instanceof VBox) {
                                        VBox vBox = (VBox) node3;
                                        HBox row = createRow(vBox, selectedItem, rowIndex);
                                        row.setId(String.valueOf(rowIndex));
                                        VBox.setMargin(row, new Insets(20.0, 0.0, 0.0, 0.0));
                                        vBox.getChildren().add(rowIndex, row);
                                        rowIndex++;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        });

        refreshReleaseItemsOnMouseHover();
    }

    private void refreshReleaseItemsOnMouseHover() {

        mainAnchorpane.hoverProperty().addListener((observableValue, oldValue, newValue) -> {
            Iterable<Item> initialItemList = itemsRepository.findAll();
            itemLists = StreamSupport.stream(initialItemList.spliterator(), false).collect(Collectors.toList());
            if (newValue != oldValue && currentLocationBanner.getText().equalsIgnoreCase("RELEASE ITEMS") && selectedItem.getSku() != 0) {
                refresher();
            }
        });
    }

    private void refresher() {
        ObservableList<Node> nodeStreamTopHbox = topHbox.getChildren();
        for (Node node : nodeStreamTopHbox) {
            if (node instanceof JFXTextField) {
                ((JFXTextField) node).setText(String.valueOf(selectedItem.getSku()));
                sku.setText(String.valueOf(selectedItem.getSku()));
            }
        }
    }

    private HBox createRow(VBox vBox, Item item, int currentIndex) {

        Item currentItem = new Item();
        itemLists.stream().forEach(s -> {
            if (s.getSku() == item.getSku()) {
                currentItem.setLow_stock(s.getLow_stock());
                currentItem.setMargin(s.getMargin());
                currentItem.setTag(null);
                currentItem.setCost(s.getCost());
                currentItem.setIn_stock(s.getIn_stock());
                currentItem.setSku(s.getSku());
                currentItem.setQuantity(0);
                currentItem.setSub_category_detail(s.getSub_category_detail());
                currentItem.setItem_name(s.getItem_name());
                currentItem.setUnit(s.getUnit());
            }
        });

        itemCart.add(currentItem);

        JFXTextField name = createTextField("ITEM NAME", "LIGHT GRAY");
        name.setText(currentItem.getItem_name());
        name.setId(String.valueOf(currentIndex));
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                itemCart.get(Integer.parseInt(name.getId())).setItem_name(newValue);
            }
        });
        name.setEditable(false);


        JFXTextField amount = createTextField("AMOUNT", "LIGHT GRAY");

        amount.setEditable(false);
        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && !ObjectUtils.isEmpty(newValue) ) {

            }
        });


        JFXTextField cost = createTextField("COST", "LIGHT GRAY");
        cost.setId(String.valueOf(currentIndex));
        cost.setText(String.valueOf(currentItem.getCost()));
        cost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                itemCart.get(Integer.parseInt(cost.getId())).setCost(Double.parseDouble(newValue));
                amount.setText(String.format("%.2f", currentItem.getQuantity() * Double.parseDouble(newValue)));
                currentItem.setCost(Double.parseDouble(newValue));
            }
        });

        JFXTextField quantity = createTextField("QUANTITY", "LIGHT GRAY");
        quantity.setText(String.valueOf(currentItem.getQuantity()));
        quantity.setId(String.valueOf(currentIndex));
        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                currentItem.setQuantity(Integer.parseInt(newValue));
                itemCart.get(Integer.parseInt(quantity.getId())).setQuantity(Integer.parseInt(newValue));
                amount.setText(String.format("%.2f", currentItem.getQuantity() * currentItem.getCost()));

            }
        });

        JFXComboBox unit = new JFXComboBox();
        unit.setPromptText("SELECT ITEM UNIT");
        unit.setId(String.valueOf(currentIndex));
        Iterable<Unit> units = unitRepository.findAll();
        units.forEach(u ->{
            unit.getItems().add(u);
        });

        unit.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                currentItem.setUnit((String) newValue);
            }
        });

        amount.setText(String.format("%.2f", currentItem.getQuantity() * currentItem.getCost()));

        JFXButton delete = createButtonWithoutText("DELETE");
        delete.setId(Integer.toString(currentIndex));
        delete.setOnAction(del -> {
            try {
                int indexOfItemToBeDeleted = itemCart.indexOf(currentItem);

                if (vBox.getChildren().size() == 1) {
                    rowIndex = 0;
                    selectedItem.setSku(0);
                    vBox.getChildren().clear();
                    itemCart.clear();
                    midHbox.getChildren().remove(1);
                } else {
                    itemCart.remove(Integer.parseInt(delete.getId()));
                    vBox.getChildren().remove(indexOfItemToBeDeleted);
                    midHbox.getChildren().remove(1);
                }
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                vBox.getChildren().clear();
            } catch (Exception exception) {
                vBox.getChildren().clear();
            }

        });


        HBox hBox = new HBox();
        HBox.setMargin(name, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(quantity, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(cost, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(amount, new Insets(50.0, 0.0, 0.0, 20.0));

        HBox.setMargin(delete, new Insets(35.0, 0.0, 0.0, 20.0));
        hBox.getChildren().addAll(name, quantity, cost, amount, delete);

        return hBox;

    }


    public void getReportsModule(ActionEvent actionEvent) {
        resetHboxes();
        currentLocationBanner.setText("Reports Module");

        JFXComboBox availableReports = new JFXComboBox();
        JFXComboBox monthsList = new JFXComboBox();
        JFXComboBox yearList = new JFXComboBox();

        String [] reports = getAvailableReports().split(",");
        String [] months = getMonthsofCalender().split(",");
        String [] years = getCalenderYears().split(",");

        availableReports.getItems().addAll(Arrays.asList(reports));
        monthsList.getItems().addAll(Arrays.asList(months));
        yearList.getItems().addAll(Arrays.asList(years));

        availableReports.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                reportUtil.setSelectedReport((String) newValue);
            }
        });

        monthsList.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                reportUtil.setSelectedMonth((String) newValue);
            }
        });

        yearList.valueProperty().addListener((observable,oldValue,newValue)->{
            if(oldValue!=newValue){
                reportUtil.setSelectedYear((String) newValue);
            }
        });

        JFXButton getReport = new JFXButton("Generate Report");
        getReport.setOnAction(e -> {
            try {
                //new StageLoader().load(MainReportController.class, applicationContext);
                reportUtil.generateReport();
            } catch (Exception exception){

            }
        });
        topHbox.getChildren().addAll(availableReports,monthsList,yearList, getReport);
        HBox.setMargin(availableReports, new Insets(20,0,0,20));
        HBox.setMargin(monthsList, new Insets(20,0,0,20));
        HBox.setMargin(yearList, new Insets(20,0,0,20));
        HBox.setMargin(getReport, new Insets(20,0,0,20));
    }

    private void resetHboxes() {
        topHbox.getChildren().clear();
        midHbox.getChildren().clear();
        midHbox.setPrefHeight(100.0);
        bottomHbox.getChildren().clear();
    }

    public void getItemsModule() {
        resetHboxes();
        resetSelectedItem();
        editItemController.create();
        currentLocationBanner.setText("Inventory");
        topHbox.getChildren().addAll(itemListController.createItemList());
    }

    private void resetSelectedItem() {
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
    }

    public void getDashboardModule(ActionEvent actionEvent) {
        resetHboxes();

        ImageView addItem = new ImageView(new Image("config-ui/IQWD-APP-RESOURCES/Main/Purchase-dashboard.jpg", true));
        ImageView releaseItem = new ImageView(new Image("config-ui/IQWD-APP-RESOURCES/Main/Release-dashboard.jpg", true));
        ImageView reports = new ImageView(new Image("config-ui/IQWD-APP-RESOURCES/Main/Reports-dashboard.jpg", true));

        JFXButton jfxBtnAddItem = new JFXButton();
        JFXButton jfxBtnReleaseItem = new JFXButton();
        JFXButton jfxBtnReports = new JFXButton();

        jfxBtnAddItem.setGraphic(addItem);
        jfxBtnReleaseItem.setGraphic(releaseItem);
        jfxBtnReports.setGraphic(reports);

        jfxBtnAddItem.setOnAction(e -> {
            getItemsModule();
        });

        jfxBtnReleaseItem.setOnAction(f -> {
            getCustomizeRIS();
            createRequisitionIssueSlip();
        });

        jfxBtnReports.setOnAction(g -> {
            getReportsModule(g);
        });

        Label companyName = new Label("INFANTA QUEZON WATER DISTRICT");
        Label companyCopyright = new Label("©IQWD 2021");

        companyName.setFont(Font.font("Roboto", FontWeight.LIGHT, 19.0));
        companyCopyright.setFont(Font.font("Roboto", FontWeight.MEDIUM, 10.0));


        topHbox.getChildren().addAll(jfxBtnAddItem, jfxBtnReleaseItem, jfxBtnReports);
        midHbox.getChildren().addAll(companyName);
        bottomHbox.getChildren().addAll(companyCopyright);


        topHbox.setAlignment(Pos.CENTER);
        midHbox.setAlignment(Pos.CENTER);
        bottomHbox.setAlignment(Pos.CENTER);
        currentLocationBanner.setText("Dashboard");
    }

    private void getCustomizeRIS() {
        new StageLoader().load(CustomizeRequisitionIssueSlipController.class, applicationContext);
    }

    public void getInventoryModule(ActionEvent actionEvent) {
        resetHboxes();
        currentLocationBanner.setText("Inventory");
        topHbox.getChildren().addAll(itemListController.createItemList());
    }

    public void getCustomersModule(ActionEvent actionEvent) {
        resetHboxes();
        currentLocationBanner.setText("Customer");
    }

    public void getSettingsModule(ActionEvent actionEvent) {
        resetHboxes();
        currentLocationBanner.setText("Settings");
    }

    public void getLogoutModule(ActionEvent actionEvent) {
    }

    public void createRequisitionIssueSlip() {
        resetHboxes();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(500);
        scrollPane.setPrefWidth(800);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(800);
        anchorPane.setPrefHeight(800);


        VBox vbox = new VBox();

        anchorPane.getChildren().add(vbox);
        AnchorPane.setLeftAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 0.0);
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);

        scrollPane.setContent(anchorPane);
        HBox.setMargin(scrollPane, new Insets(0.0, 20.0, 0.0, 0.0));

        JFXTextField itemName = new JFXTextField();
        itemName.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != newValue) {
                searchUtil.searchItem(newValue, itemLists, responseList, "NAME");
                if (responseList.size() > 0) {

                    HBox.setMargin(listView, new Insets(20.0, 0.0, 20.0, 20.0));
                    responseList.stream().forEach(data -> {
                        listView.getItems().clear();
                        listView.getItems().add(data);
                        listView.setOnMouseClicked(e -> {
                            if (e.getClickCount() == 2) {
                                Optional<Item> optionalItem = itemLists.stream().filter(f -> f.getItem_name().equalsIgnoreCase(listView.getSelectionModel().getSelectedItem().toString())).findFirst();
                                selectedItem.setSku(optionalItem.isPresent() ? optionalItem.get().getSku() : 0);
                                sku.setText(String.valueOf(optionalItem.isPresent() ? optionalItem.get().getSku() : 0));
                            }
                        });
                    });

                    midHbox.getChildren().add(listView);
                }

                if (ObjectUtils.isEmpty(newValue)) {
                    responseList.clear();
                    selectedItem.setSku(0);
                    sku.setText("");
                    rowIndex = 0;
                    try {
                        midHbox.getChildren().remove(1);
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                    }

                }
            }
        });

        itemName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.BACK_SPACE) {
                try {
                    responseList.clear();
                    selectedItem.setSku(0);
                    midHbox.getChildren().retainAll(scrollPane);
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                }
            }
        });

        JFXButton openCamera = new JFXButton("Open Camera");
        JFXButton viewRisDetails = new JFXButton("RIS Details");


        openCamera.setOnAction(e -> {
            new StageLoader().load(CaptureQrCodeController.class, applicationContext);
        });

        viewRisDetails.setOnAction(e -> {
            new StageLoader().load(RisDetailsController.class, applicationContext);
        });

        topHbox.getChildren().addAll(itemName, openCamera, viewRisDetails);
        midHbox.getChildren().addAll(scrollPane);
        midHbox.setPrefHeight(500.0);

        JFXButton releaseItem = new JFXButton("RELEASE ITEM");
        JFXButton cancelButton = new JFXButton("CANCEL");


        releaseItem.setOnAction(x -> {
            releaseItemsOnCart();
        });

        cancelButton.setOnAction(v -> {
            cancelTransaction();
        });

        bottomHbox.getChildren().addAll(releaseItem, cancelButton);

        HBox.setMargin(scrollPane, new Insets(20.0, 0.0, 0.0, 20.0));

        try {
            currentLocationBanner.setText(readQrCodeImage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }


    private void cancelTransaction() {
        rowIndex = 0;
        resetSelectedItem();
        itemCart.clear();
        getDashboardModule(null);
    }

    private void releaseItemsOnCart() {
        if (!ObjectUtils.isEmpty(itemCart) || selectedItem.getSku() != 0) {
            itemCart.stream().forEach(item -> {

                History history = new History();
                history.setDate(AppTime.now());
                history.setItem_name(item.getItem_name());
                history.setAdjustment(item.getQuantity()*-1);
                history.setSub_category_detail(item.getSub_category_detail());
                history.setReason("RELEASE ITEM");

                if(requisitionIssueSlip.getIs_customer_new()==1){
                    totalCost+=item.getCost() * item.getQuantity();
                    totalSales+=(item.getCost()+(item.getCost()*.2)) * item.getQuantity();
                    totalIncome = totalSales - totalCost;
                }else{
                    totalCost+=item.getCost() * item.getQuantity();
                    totalSales+= item.getCost() * item.getQuantity();
                    totalIncome = totalSales - totalCost;
                }

                int inStock = item.getIn_stock() - item.getQuantity();
                item.setQuantity(inStock);
                item.setIn_stock(inStock);
                item.setTag(null);

                history.setStock_after(inStock);
                historyRepository.save(history);




            });
            if (!ObjectUtils.isEmpty(itemsRepository.saveAll(itemCart))) {
                resetSelectedItem();
                sku.setText("0");
                itemCart.clear();
                try {
                    midHbox.getChildren().remove(1);
                    cancelTransaction();
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                }

                if (!ObjectUtils.isEmpty(requisitionIssueSlip)) {
                    if (!ObjectUtils.isEmpty(risRepository.save(requisitionIssueSlip))) {
                        Prompt.success("Item released!");
                    }
                } else {
                    Prompt.failed("Item could not be released! Please check RIS details!");
                }

            } else {
                Prompt.failed("An error occured while saving!");
            }
            risTypeFieldsList.stream().forEach(data ->{
                risTypeFieldsRepository.save(data);
            });

            Sales sales = new Sales();
            sales.setControl_number(requisitionIssueSlip.getControl_number());
            sales.setTotal_sales(totalSales);
            sales.setDate_transacted(AppTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-YYYY")));
            sales.setIncome(totalIncome);
            sales.setTotal_cost(totalCost);

            salesRepository.save(sales);

            customerRepository.save(customer);

        } else {
            Prompt.failed("Please enter valid input!");
        }

    }


    public void getBackupModule(ActionEvent actionEvent) {
    }

    public void openRISModule(ActionEvent actionEvent) {
        getCustomizeRIS();
        createRequisitionIssueSlip();
    }
}
