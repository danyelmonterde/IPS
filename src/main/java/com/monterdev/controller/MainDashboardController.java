package com.monterdev.controller;

import com.jfoenix.controls.*;
import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.*;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.SneakyThrows;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.monterdev.configuration.GlobalConfiguration.*;
import static com.monterdev.constants.HistoryConstants.RELEASED_ITEM;
import static com.monterdev.constants.InventoryTypeConstants.SUMMARY;
import static com.monterdev.constants.TextFieldValidatorConstants.*;
import static com.monterdev.util.ComponentCreator.createButtonWithoutText;
import static com.monterdev.util.ComponentCreator.createTextField;

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
    private JFXButton releaseItemButton;
    @FXML
    private JFXButton addItemButton;
    @FXML
    private Label sku;
    @FXML
    private JFXTextField dashboardSearchField;
    @FXML
    private TextField dashboardSearchResult;
    @FXML
    private JFXButton purchaseExistingItemButton;


    @Autowired
    private EditItemController editItemController;
    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private Item selectedItem;
    @Autowired
    private CapturedItem capturedItem;

    @Autowired
    private ItemsRepository itemsRepository;
    @Autowired
    private RisRepository risRepository;
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
    @Autowired
    private ReportNamesRepository reportNamesRepository;
    @Autowired
    private SearchEngineController searchEngineController;
    @Autowired
    private SelectedRisTemplate selectedRisTemplate;
    @Autowired
    private User user;

    @Autowired
    private ReportUtil reportUtil;
    @Autowired
    private SearchUtil searchUtil;
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private List<Item> itemLists;

    private List<String> responseList = new ArrayList<>();
    private static int rowIndex = 0;
    private int indexToBeRemoved = 0;
    private List<Item> itemCart = new ArrayList<>();
    private boolean isEmpty = true;
    private JFXListView listView = new JFXListView();
    private TranslateTransition slide = new TranslateTransition();
    private TranslateTransition slideMainAnchorpane = new TranslateTransition();
    private TranslateTransition slideSearchPane = new TranslateTransition();
    private double totalSales = 0; //;total amount na may 20% na patong pag new
    private double totalCost = 0; //lahat ng average cost
    private double totalIncome = 0; //totalSales - totalCost
    private boolean isFirstCharacter = true;
    private boolean isItemOnCart;
    private boolean proceed = false;
    private ScheduledExecutorService currentTimeUpdater;
    private static final String DATE_FORMAT = "dd-MMM-YYYY hh:mm:ss";
    private int validNumberOfItemsInCart = 0;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(ObjectUtils.isEmpty(reportUtil)){
            reportUtil = new ReportUtil();
        }
        if(ObjectUtils.isEmpty(itemLists)){
            itemLists = itemsRepository.findAll();
        }


        if(!ObjectUtils.isEmpty(requisitionIssueSlip.getRequisition_and_issue_slip_number())){
            createRequisitionIssueSlip();
        }

        Runnable timeUpdater = () -> Platform.runLater(() -> {
            if (capturedItem.getSku() != 0) {
                selectedItem.setSku(capturedItem.getSku());
                selectedItem.setItem_name(capturedItem.getItem_name());
                sku.setText(String.valueOf(capturedItem.getSku()));
                itemLists = itemsRepository.findAll();
            }
        });

        this.currentTimeUpdater = Executors.newSingleThreadScheduledExecutor();
        this.currentTimeUpdater.scheduleAtFixedRate(timeUpdater, 0, 1, TimeUnit.SECONDS);

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

        // refreshReleaseItemsOnMouseHover();
        addItemOnAction();
        searchItemOnDashboard();
        purchaseExistingItemButtonOnAction();
    }

    private void purchaseExistingItemButtonOnAction() {
        purchaseExistingItemButton.setOnAction(add -> {
            Stage currentStage = (Stage) mainAnchorpane.getScene().getWindow();
            new StageLoader().load(EditItemController.class, applicationContext, currentStage);
        });
    }


    private void addItemOnAction() {
        addItemButton.setOnAction(add -> {
            Stage currentStage = (Stage) mainAnchorpane.getScene().getWindow();
            new StageLoader().load(AddItemController.class, applicationContext, currentStage);
        });
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
        itemLists = itemsRepository.findAll();
        itemLists.stream().forEach(s -> {
            if (s.getSku() == item.getSku() && (!(item.getItem_name().equalsIgnoreCase(s.getItem_name())))) {
                currentItem.setLow_stock(s.getLow_stock());
                currentItem.setTag(null);
                currentItem.setCost(s.getCost());
                currentItem.setIn_stock(s.getIn_stock());
                currentItem.setSku(s.getSku());
                currentItem.setQuantity(0);
                currentItem.setItem_name(s.getItem_name());
                currentItem.setItem_category(s.getItem_category());
                currentItem.setUnit(s.getUnit());
            }
        });

        itemCart.add(currentIndex,currentItem);


        JFXButton delete = createButtonWithoutText("DELETE");
        delete.setId(Integer.toString(currentIndex));
        delete.setOnAction(del -> {
            try {

                    int id = Integer.parseInt(delete.getId());
                    itemCart.remove(id);
                    vBox.getChildren().remove(currentIndex);
                    updateIdOfRemainingHbox(id);
                    System.out.println(itemCart.size());
                    rowIndex--;

            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                vBox.getChildren().clear();
            } catch (Exception exception) {
                vBox.getChildren().clear();
            }

        });



        JFXTextField name = createTextField("ITEM NAME", "LIGHT GRAY");
        name.setPrefWidth(220.0);
        name.setEditable(false);
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
            if (oldValue != newValue && !ObjectUtils.isEmpty(newValue)) {

            }
        });


        JFXTextField cost = createTextField("COST", "LIGHT GRAY");
        cost.setEditable(false);
        cost.setId(String.valueOf(currentIndex));
        cost.setText(String.valueOf(currentItem.getCost()));
        cost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                itemCart.get(Integer.parseInt(cost.getId())).setCost(DataUtil.formatDouble(newValue));
                amount.setText(String.format("%.2f", currentItem.getQuantity() * DataUtil.formatDouble(newValue)));

                if (requisitionIssueSlip.getIs_customer_new() == 1) {
                    amount.setText(String.format("%.2f", currentItem.getQuantity() * DataUtil.formatDouble(newValue) + (DataUtil.formatDouble(newValue) * .2)));
                } else {
                    amount.setText(String.format("%.2f", currentItem.getQuantity() * DataUtil.formatDouble(newValue)));
                }
                currentItem.setCost(DataUtil.formatDouble(newValue));
            }
        });

        JFXTextField quantity = createTextField("QUANTITY", "LIGHT GRAY");
        quantity.setText(String.valueOf(currentItem.getQuantity()));
        quantity.setId(String.valueOf(currentIndex));
        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                int inStock =currentItem.getIn_stock() ;
                if (NumberUtils.isParsable(quantity.getText()) && !ObjectUtils.isEmpty(quantity.getText())) {
                    //VALID TEXT FIELD
                    if(Integer.parseInt(quantity.getText())>inStock){
                        Prompt.failed("Quantity is greater than In Stock!");
                        quantity.setText("0");
                        return;
                    }
                    quantity.setText(quantity.getText().replaceAll(NEGATIVE_WHOLE_NUMBERS, ""));
                    currentItem.setQuantity(Math.abs(Integer.parseInt(newValue)));
                    itemCart.get(Integer.parseInt(quantity.getId())).setQuantity(Math.abs(Integer.parseInt(newValue)));
                    if (requisitionIssueSlip.getIs_customer_new() == 1) {
                        amount.setText(String.format("%.2f", currentItem.getQuantity() * currentItem.getCost() + (currentItem.getCost() * .2)));
                    } else {
                        amount.setText(String.format("%.2f", currentItem.getQuantity() * currentItem.getCost()));
                    }
                } else if (ObjectUtils.isEmpty(quantity.getText())) {
                    //EMPTY TEXT FIELD
                    item.setLow_stock(0);
                    quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                } else {
                    //INVALID TEXT FIELD
                    quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                    quantity.setText(quantity.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
                }
                checkIfItemIsInCart(quantity,newValue);
            } catch (NumberFormatException exception) {
                quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                quantity.setText(quantity.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
            } catch (IllegalArgumentException exception) {
                quantity.setText(quantity.getText().replaceAll(WHOLE_NUMBERS_REGEX_EXCLUDE, ""));
                quantity.setText(quantity.getText().replaceAll(PLUS_DOLLAR_REGEX_EXCLUDE, ""));
            }

        });

        JFXComboBox unit = new JFXComboBox();
        unit.setPromptText("SELECT ITEM UNIT");
        unit.setId(String.valueOf(currentIndex));
        Iterable<Unit> units = unitRepository.findAll();
        units.forEach(u -> {
            unit.getItems().add(u);
        });

        unit.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                currentItem.setUnit((String) newValue);
            }
        });

        amount.setText(String.format("%.2f", currentItem.getQuantity() * currentItem.getCost()));


        HBox hBox = new HBox();
        HBox.setMargin(name, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(quantity, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(cost, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(amount, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(delete, new Insets(50.0, 0.0, 0.0, 20.0));
        hBox.getChildren().addAll(name, quantity, cost, amount, delete);

        return hBox;

    }

    private void checkIfItemIsInCart(JFXTextField item, String newValue) {
       try{
           int itemIndex= Integer.parseInt(item.getId());
           String s = newValue;
           itemCart.get(itemIndex).setQuantity(Integer.parseInt(newValue));
           System.out.println("New qty is "+itemCart.get(itemIndex).getQuantity());
       }catch (IndexOutOfBoundsException e){
            System.out.println("Item not in cart");
       }
    }

    private void updateIdOfRemainingHbox(int indexOfItemDeleted){
        ObservableList<Node> nodeStream = midHbox.getChildren();
        for (Node node : nodeStream) {
            if (node instanceof ScrollPane) {
                Node node2 = ((ScrollPane) node).getContent();
                if (node2 instanceof AnchorPane) {
                    for (Node node3 : ((AnchorPane) node2).getChildren()) {
                        if (node3 instanceof VBox) {
                            //vbox
                            //hbox
                            for (Node node4: ((VBox) node3).getChildren()){
                                if(node4 instanceof HBox){
                                   for(Node node5: ((HBox) node4).getChildren()){
                                       if(node5 instanceof JFXButton){
                                           JFXButton deleteButton = (JFXButton) node5;
                                           String currentId = deleteButton.getId();
                                           if(Integer.parseInt(currentId)>indexOfItemDeleted){
                                               deleteButton.setId(String.valueOf(Integer.parseInt(currentId)-1));
                                           }

                                       }
                                   }
                                }
                            }

//                            VBox vBox = (VBox) node3;
//                            HBox row = createRow(vBox, selectedItem, rowIndex);
//                            row.setId(String.valueOf(rowIndex));
//                            VBox.setMargin(row, new Insets(20.0, 0.0, 0.0, 0.0));
//                            vBox.getChildren().add(rowIndex, row);
//                            rowIndex++;
                        }
                    }
                }
            }
        }
    }


    public void getReportsModule(ActionEvent actionEvent) {
        resetHboxes();
        currentLocationBanner.setText("Reports Module");

        JFXComboBox<ReportNames> availableReports = new JFXComboBox();
        JFXComboBox monthsList = new JFXComboBox();
        JFXComboBox yearList = new JFXComboBox();

        availableReports.setPromptText("CHOOSE TYPE OF REPORT");
        monthsList.setPromptText("SELECT MONTH OF REPORT");
        yearList.setPromptText("SELECT YEAR OF REPORT");

        yearList.setLabelFloat(true);
        monthsList.setLabelFloat(true);
        availableReports.setLabelFloat(true);

        ObservableList<ReportNames> reportLists = FXCollections.observableArrayList();
        List<ReportNames> reports = reportNamesRepository.findAllAvailableReports();
        String[] months = getMonthsofCalender().split(",");
        String[] years = getCalenderYears().split(",");


        reports.stream().forEach(report -> {
            reportLists.add(report);
        });

        monthsList.getItems().addAll(Arrays.asList(months));
        yearList.getItems().addAll(Arrays.asList(years));

        availableReports.setItems(reportLists);
        availableReports.setConverter(new StringConverter<ReportNames>() {
            @Override
            public String toString(ReportNames object) {
                return object.getName();
            }

            @Override
            public ReportNames fromString(String string) {
                return availableReports.getItems().stream().filter(ap ->
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });

        availableReports.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                reportUtil.setSelectedReport(newValue.getName());
                reportUtil.setReportId(newValue.getId());
                reportUtil.setSelectedReportRisTypeCode(newValue.getRisTypeName());//ex.CM-NEW CONNECTION
               // reportUtil.setFILE_UPPER_PART(new File(getReportJrxmlLocation() + newValue.getJrxmlReportFileName() + ".jrxml"));
                try {
                    reportUtil.setFILE_UPPER_PART(resourceLoader.getResource(getReportJrxmlLocation() + newValue.getJrxmlReportFileName() + ".jrxml").getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                reportUtil.setInventoryType(newValue.getInventorytype());
                if (newValue.getRisTypeName().equalsIgnoreCase(SUMMARY)) {
                    try {
                        reportUtil.setFILE_LOWER_PART(resourceLoader.getResource(getReportJrxmlLocation() + "SINGLE_INVENTORY_SUMMARY_LOWER_PART.jrxml").getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        monthsList.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                reportUtil.setSelectedMonth((String) newValue);
            }
        });

        yearList.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                reportUtil.setSelectedYear((String) newValue);
            }
        });


        JFXButton getReport = new JFXButton("Generate");
        getReport.setOnAction(e -> {
            try {
                reportUtil.generateReport();
            } catch (Exception exception) {
                    System.out.println(exception.getLocalizedMessage());
            }
        });


        topHbox.getChildren().addAll(availableReports, monthsList, yearList, getReport);
        HBox.setMargin(availableReports, new Insets(20, 0, 20, 20));
        HBox.setMargin(monthsList, new Insets(20, 0, 20, 20));
        HBox.setMargin(yearList, new Insets(20, 0, 20, 20));
        HBox.setMargin(getReport, new Insets(20, 0, 20, 20));
    }

    private void resetHboxes() {
        topHbox.getChildren().clear();
        midHbox.getChildren().clear();
        midHbox.setPrefHeight(100.0);
        bottomHbox.getChildren().clear();
    }


    public void getAddItemModule() {
        Stage currentStage = (Stage) mainAnchorpane.getScene().getWindow();
        new StageLoader().load(AddItemController.class, applicationContext, currentStage);
    }

    private void resetSelectedItem() {
        capturedItem = applicationContext.getBean(CapturedItem.class);
        selectedItem = applicationContext.getBean(Item.class);
        selectedItem.setTag(null);
        selectedItem.setItem_name("");
        selectedItem.setLow_stock(0);
        selectedItem.setQuantity(0);
        selectedItem.setSku(0);
        selectedItem.setCost(0.0);
        selectedItem.setItem_category(getDefaultCategoryData());
        selectedItem.setIn_stock(0);
        capturedItem.setSku(0);
        capturedItem.setItem_name("");
    }

    public void getDashboardModule(ActionEvent actionEvent) {
        resetHboxes();

        ImageView addItem = new ImageView(new Image("config-ui/IQWD-APP-RESOURCES/Main/AddItem-dashboard.png", true));
        ImageView releaseItem = new ImageView(new Image("config-ui/IQWD-APP-RESOURCES/Main/Release-dashboard.png", true));
        ImageView reports = new ImageView(new Image("config-ui/IQWD-APP-RESOURCES/Main/Reports-dashboard.png", true));
        ImageView purchaseItem = new ImageView(new Image("config-ui/IQWD-APP-RESOURCES/Main/Purchase-dashboard.png", true));

        addItem.setFitHeight(150.0);
        addItem.setFitWidth(200.0);
        addItem.setPickOnBounds(true);
        addItem.setPreserveRatio(true);

        releaseItem.setFitHeight(150.0);
        releaseItem.setFitWidth(200.0);
        releaseItem.setPickOnBounds(true);
        releaseItem.setPreserveRatio(true);

        reports.setFitHeight(150.0);
        reports.setFitWidth(200.0);
        reports.setPickOnBounds(true);
        reports.setPreserveRatio(true);

        purchaseItem.setFitHeight(150.0);
        purchaseItem.setFitWidth(200.0);
        purchaseItem.setPickOnBounds(true);
        purchaseItem.setPreserveRatio(true);



        JFXButton jfxBtnAddItem = new JFXButton();
        JFXButton jfxBtnReleaseItem = new JFXButton();
        JFXButton jfxBtnReports = new JFXButton();
        JFXButton jfxBtnPurchaseItem = new JFXButton();

        jfxBtnAddItem.setGraphic(addItem);
        jfxBtnReleaseItem.setGraphic(releaseItem);
        jfxBtnReports.setGraphic(reports);
        jfxBtnPurchaseItem.setGraphic(purchaseItem);

        jfxBtnPurchaseItem.setOnAction(e -> {

            Stage currentStage = (Stage) mainAnchorpane.getScene().getWindow();
            new StageLoader().load(EditItemController.class, applicationContext, currentStage);
        });

        jfxBtnAddItem.setOnAction(e -> {
            Stage currentStage = (Stage) mainAnchorpane.getScene().getWindow();
            new StageLoader().load(AddItemController.class, applicationContext, currentStage);
        });

        jfxBtnReleaseItem.setOnAction(f -> {
            getCustomizeRIS();
        });

        jfxBtnReports.setOnAction(g -> {
            getReportsModule(g);
        });

        Label companyName = new Label("INFANTA QUEZON WATER DISTRICT");
        Label companyCopyright = new Label("©IQWD 2021");

        companyName.setFont(Font.font("Roboto", FontWeight.LIGHT, 19.0));
        companyCopyright.setFont(Font.font("Roboto", FontWeight.MEDIUM, 10.0));


        topHbox.getChildren().addAll(jfxBtnAddItem, jfxBtnPurchaseItem, jfxBtnReleaseItem, jfxBtnReports);
        midHbox.getChildren().addAll(companyName);
        bottomHbox.getChildren().addAll(companyCopyright);


        topHbox.setAlignment(Pos.CENTER);
        midHbox.setAlignment(Pos.CENTER);
        bottomHbox.setAlignment(Pos.CENTER);
        currentLocationBanner.setText("Dashboard");
    }

    private void getCustomizeRIS() {
        Stage stage = (Stage) sidebarAnchorpane.getScene().getWindow();
        new StageLoader().loadTest(CustomizeRequisitionIssueSlipController.class, applicationContext, stage);
    }

    public void getInventoryModule(ActionEvent actionEvent) {
        resetHboxes();
        Stage currentStage = (Stage) mainAnchorpane.getScene().getWindow();
        new StageLoader().load(InventoryListController.class, applicationContext, currentStage);

    }

    public void getCustomersModule(ActionEvent actionEvent) {
        resetHboxes();
        currentLocationBanner.setText("Customer");
    }

    public void getSettingsModule(ActionEvent actionEvent) {
        resetHboxes();
        JFXButton createMasterData = new JFXButton("ADD MASTER DATA");
        JFXButton updatePassword = new JFXButton("UPDATE PASSWORD");
        JFXButton rolesAndPrivileges = new JFXButton("ADMIN ROLES & PRIVILEGES");
        JFXButton balanceSettings = new JFXButton("BALANCE SETTINGS");
        JFXButton stockAdjusment = new JFXButton("STOCK ADJUSTMENT");

        Stage currentStage = (Stage) sidebarAnchorpane.getScene().getWindow();

        createMasterData.setOnAction(e -> {
            new StageLoader().load(MasterDataController.class, applicationContext, currentStage);

        });

        updatePassword.setOnAction(e -> {
            new StageLoader().load(UpdatePasswordController.class, applicationContext, currentStage);

        });

        rolesAndPrivileges.setOnAction(e -> {
            new StageLoader().load(AdminRolesController.class, applicationContext, currentStage);

        });


        stockAdjusment.setOnAction(e -> {
            new StageLoader().load(StockAdjustmentController.class, applicationContext, currentStage);

        });

        balanceSettings.setOnAction(e -> {
            new StageLoader().load(BalanceSettingsController.class, applicationContext, currentStage);

        });

        topHbox.getChildren().addAll(createMasterData, updatePassword, rolesAndPrivileges, balanceSettings, stockAdjusment);
        topHbox.setAlignment(Pos.CENTER);
        midHbox.setAlignment(Pos.CENTER);
        bottomHbox.setAlignment(Pos.CENTER);
        currentLocationBanner.setText("Settings");
    }

    public void getLogoutModule(ActionEvent actionEvent) {
        rowIndex = 0;
        requisitionIssueSlip.setRequisition_and_issue_slip_number("");
        requisitionIssueSlip.setRistype("");
        requisitionIssueSlip.setPurpose("");
        requisitionIssueSlip.setCustomer_name("");
        requisitionIssueSlip.setDate_transacted(AppTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        resetSelectedItem();
        user.setUsername(null);
        user.setIsAdmin(null);
        user.setIsSuperAdmin(null);
        user.setPassword(null);
        user.setId(0);
        Stage currentStage = (Stage) sidebarAnchorpane.getScene().getWindow();
        currentStage.close();
        Stage newStage = new Stage();
        new StageLoader().load(LoginController.class, applicationContext, newStage);
    }

    public void createRequisitionIssueSlip() {
        resetHboxes();
        topHbox.setAlignment(Pos.BOTTOM_LEFT);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(500);
        scrollPane.setPrefWidth(600);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(600);
        anchorPane.setPrefHeight(600);


        VBox vbox = new VBox();

        anchorPane.getChildren().add(vbox);
        AnchorPane.setLeftAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 40.0);
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);

        scrollPane.setContent(anchorPane);

        midHbox.setAlignment(Pos.CENTER_RIGHT);
        midHbox.setPadding(new Insets(0.0, 20.0, 0.0, 20.0));
        HBox.setMargin(scrollPane, new Insets(0.0, 40.0, 20.0, 20.0));

        JFXTextField itemName = new JFXTextField();
        itemName.setPrefWidth(200.0);
        HBox.setMargin(itemName, new Insets(20.0, 20.0, 20.0, 20.0));

        ToggleGroup searchGroup = new ToggleGroup();

        searchGroup.selectedToggleProperty().addListener((observable,oldValue,newValue)->{
            RadioButton rb = (RadioButton) searchGroup.getSelectedToggle();
            if(rb != null){
                System.out.println(rb.getText());
            }
        });


        itemName.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != newValue) {

                if(NumberUtils.isParsable(newValue)){
                   Optional<Item> item = itemsRepository.findById(Integer.parseInt(newValue));
                    if(item.isPresent()){
                        itemName.setText(item.get().getItem_name());
                    }
                }

                if (isFirstCharacter && itemName.getText().length() == 1) {
                   // midHbox.getChildren().add(0,listView);
                    isFirstCharacter = false;
                }
                itemLists = itemsRepository.findAll();
                searchUtil.searchItem(itemName.getText(), itemLists, responseList, "NAME");
                if (responseList.size() > 0) {

                    listView.setPrefWidth(400);

                    HBox.setMargin(listView, new Insets(20.0, 0.0, 20.0, 0.0));
                    listView.getItems().clear();
                    responseList.stream().forEach(data -> {

                        listView.getItems().add(data);

                    });
                    listView.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2) {
                            List<Item> updatedItemList = itemsRepository.findAll();
                            Optional<Item> optionalItem = updatedItemList.stream().filter(f -> String.valueOf(f.getItem_name()).equalsIgnoreCase(listView.getSelectionModel().getSelectedItem().toString())).findFirst();
                            isItemOnCart = false;
                            itemCart.stream().forEach(x -> {
                                if (x.getItem_name().equalsIgnoreCase(optionalItem.get().getItem_name())) {
                                    Prompt.failed("Item already in cart!");
                                    isItemOnCart = true;
                                }
                            });
                            if (optionalItem.get().getIn_stock() == 0) {
                                Prompt.failed("Item is out of Stock");
                            } else if ((optionalItem.get().getIn_stock() == optionalItem.get().getLow_stock()) && (isItemOnCart == false)) {
                                Prompt.failed("Item is Low Stock!");
                                selectedItem.setSku(optionalItem.isPresent() ? optionalItem.get().getSku() : 0);
                                sku.setText(String.valueOf(optionalItem.isPresent() ? optionalItem.get().getSku() : 0));
                            } else if (isItemOnCart == false) {
                                selectedItem.setSku(optionalItem.isPresent() ? optionalItem.get().getSku() : 0);
                                sku.setText(String.valueOf(optionalItem.isPresent() ? optionalItem.get().getSku() : 0));
                            }


                        }
                    });


                }

                if (ObjectUtils.isEmpty(newValue)) {
                    responseList.clear();
                    selectedItem.setSku(0);
                    sku.setText("");
                    rowIndex = 0;
                    try {
                     //   midHbox.getChildren().remove(0);
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                    }

                }
            }
        });

        itemName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.BACK_SPACE) {
                try {
                    isFirstCharacter = true;
                    responseList.clear();
                    selectedItem.setSku(0);
                //    midHbox.getChildren().retainAll(scrollPane);
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                }
            }
        });

//        JFXButton openCamera = new JFXButton("Open Camera");
//        openCamera.setButtonType(JFXButton.ButtonType.RAISED);
//        openCamera.setStyle("-fx-background-color: NAVY BLUE;");
//        openCamera.setTextAlignment(TextAlignment.CENTER);
//        openCamera.setTextFill(Paint.valueOf("WHITE"));
//        openCamera.setFont(Font.font("System Bold", 14.0));
//
//        JFXButton viewRisDetails = new JFXButton("RIS Details");
//        viewRisDetails.setButtonType(JFXButton.ButtonType.RAISED);
//        viewRisDetails.setStyle("-fx-background-color: NAVY BLUE;");
//        viewRisDetails.setTextAlignment(TextAlignment.CENTER);
//        viewRisDetails.setTextFill(Paint.valueOf("WHITE"));
//        viewRisDetails.setFont(Font.font("System Bold", 14.0));

  //      Stage currentStage = (Stage) sidebarAnchorpane.getScene().getWindow();
//        openCamera.setOnAction(e -> {
//            if (ObjectUtils.isEmpty(selectedRisTemplate.getSelectedRisTemplate())) {
//                Prompt.failed("Please set RIS Type first!");
//                Stage stage = new Stage();
//                currentStage.close();
//                new StageLoader().load(MainDashboardController.class, applicationContext, stage);
//            } else {
//                Stage stage = new Stage();
//                new StageLoader().load(CaptureQrCodeController.class, applicationContext, stage);
//            }
//
//        });

//        viewRisDetails.setOnAction(e -> {
//            if (ObjectUtils.isEmpty(selectedRisTemplate.getSelectedRisTemplate())) {
//                Prompt.failed("Please set RIS Type first!");
//                Stage stage = new Stage();
//                currentStage.close();
//                new StageLoader().load(MainDashboardController.class, applicationContext, stage);
//            } else {
//                Stage stage = new Stage();
//                new StageLoader().load(RisDetailsController.class, applicationContext, stage);
//            }
//
//        });

        //topHbox.getChildren().addAll(itemName, openCamera, viewRisDetails);
        topHbox.getChildren().addAll(itemName);
        midHbox.getChildren().addAll(listView,scrollPane);
        midHbox.setPrefHeight(500.0);

         HBox.setMargin(listView, new Insets(20.0, 20.0, 20.0, 0.0));
         HBox.setMargin(scrollPane, new Insets(20.0, 20.0, 20.0, 20.0));
        //HBox.setMargin(viewRisDetails, new Insets(20.0, 20.0, 20.0, 20.0));


        JFXButton releaseItem = new JFXButton("RELEASE ITEM");
        releaseItem.setButtonType(JFXButton.ButtonType.RAISED);
        releaseItem.setStyle("-fx-background-color: GREEN;");
        releaseItem.setTextAlignment(TextAlignment.CENTER);
        releaseItem.setTextFill(Paint.valueOf("WHITE"));
        releaseItem.setFont(Font.font("System Bold", 14.0));

        JFXButton cancelButton = new JFXButton("CANCEL");
        cancelButton.setButtonType(JFXButton.ButtonType.RAISED);
        cancelButton.setStyle("-fx-background-color: RED;");
        cancelButton.setTextAlignment(TextAlignment.CENTER);
        cancelButton.setTextFill(Paint.valueOf("WHITE"));
        cancelButton.setFont(Font.font("System Bold", 14.0));

        JFXButton deleteAllButton = new JFXButton("DELETE ALL ITEMS ON CART");
        deleteAllButton.setButtonType(JFXButton.ButtonType.RAISED);
        deleteAllButton.setStyle("-fx-background-color: black;");
        deleteAllButton.setTextAlignment(TextAlignment.CENTER);
        deleteAllButton.setTextFill(Paint.valueOf("WHITE"));
        deleteAllButton.setFont(Font.font("System Bold", 14.0));


        releaseItem.setOnAction(x -> {
            releaseItemsOnCart();
        });

        cancelButton.setOnAction(v -> {
            cancelTransaction();
        });

        deleteAllButton.setOnAction(e -> {
            selectedItem.setSku(0);
            itemCart.clear();
            sku.setText("0");
            rowIndex = 0;
            validNumberOfItemsInCart = 0;

            ObservableList<Node> nodeStream = midHbox.getChildren();
            for (Node node : nodeStream) {
                if (node instanceof ScrollPane) {
                    Node node2 = ((ScrollPane) node).getContent();
                    if (node2 instanceof AnchorPane) {
                        for (Node node3 : ((AnchorPane) node2).getChildren()) {
                            if (node3 instanceof VBox) {
                                VBox vBox = (VBox) node3;
                                vBox.getChildren().clear();
                            }
                        }
                    }
                }
            }
        });

        bottomHbox.getChildren().addAll(releaseItem, cancelButton, deleteAllButton);

        HBox.setMargin(scrollPane, new Insets(20.0, 0.0, 20.0, 20.0));
        HBox.setMargin(cancelButton, new Insets(20.0, 0.0, 20.0, 20.0));
        HBox.setMargin(deleteAllButton, new Insets(20.0, 0.0, 20.0, 20.0));
        HBox.setMargin(releaseItem, new Insets(20.0, 0.0, 20.0, 20.0));

        currentLocationBanner.setText("RELEASE ITEMS");
    }


    private void cancelTransaction() {
        rowIndex = 0;
        resetSelectedItem();
        itemCart.clear();
        validNumberOfItemsInCart = 0;
        getDashboardModule(null);
    }

    private void releaseItemsOnCart() {

        if (!ObjectUtils.isEmpty(itemCart) && selectedItem.getSku() != 0) {
            int numberOfItemsInCart = itemCart.size();
            for(int counter=0;counter<numberOfItemsInCart;counter++){
                if(itemCart.get(counter).getQuantity()==0 || itemCart.get(counter).getIn_stock()==0 || itemCart.get(counter).getCost()==0 ){
                    proceed = false;
                    break;
                }else{
                    validNumberOfItemsInCart++;
                    proceed = true;
                }

                History history = new History();
                history.setDate(AppTime.now());
                history.setItem_name(itemCart.get(counter).getItem_name());
                history.setAdjustment(itemCart.get(counter).getQuantity() * -1);
                history.setItem_category(itemCart.get(counter).getItem_category());
                history.setReason(RELEASED_ITEM);
                history.setItem_category(itemCart.get(counter).getItem_category());

                if (requisitionIssueSlip.getIs_customer_new() == 1) {
                    totalCost += itemCart.get(counter).getCost() * itemCart.get(counter).getQuantity();
                    totalSales += (itemCart.get(counter).getCost() + (itemCart.get(counter).getCost() * .2)) * itemCart.get(counter).getQuantity();
                    totalIncome = totalSales - totalCost;
                } else {
                    totalCost += itemCart.get(counter).getCost() * itemCart.get(counter).getQuantity();
                    totalSales += itemCart.get(counter).getCost() * itemCart.get(counter).getQuantity();
                    totalIncome = totalSales - totalCost;
                }

                int inStock = itemCart.get(counter).getIn_stock() - itemCart.get(counter).getQuantity();
                itemCart.get(counter).setQuantity(inStock);
                itemCart.get(counter).setIn_stock(inStock);
                itemCart.get(counter).setTag(null);

                history.setStock_after(inStock);
                historyRepository.save(history);

            }
            if(proceed && numberOfItemsInCart==validNumberOfItemsInCart){
                if (!ObjectUtils.isEmpty(itemsRepository.saveAll(itemCart))) {
                    resetSelectedItem();
                    sku.setText("0");
                    itemCart.clear();
                    try {
                        //   midHbox.getChildren().remove(1);
                        cancelTransaction();
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

                    }

                    if (!ObjectUtils.isEmpty(requisitionIssueSlip)) {
                        requisitionIssueSlip.setId(0);
                        if (!ObjectUtils.isEmpty(risRepository.save(requisitionIssueSlip))) {
                            if (!ObjectUtils.isEmpty(risTypeFieldsList)) {
                                risTypeFieldsList.stream().forEach(data -> {
                                    risTypeFieldsRepository.save(data);
                                });
                                Prompt.success("Item released!");
                                isFirstCharacter = true;
                                risTypeFieldsList.clear();
                            } else {
                                Prompt.failed("Item could not be released! Please check RIS details!");
                            }
                        } else {
                            Prompt.failed("Item could not be released! Please check RIS details!");
                        }

                        Sales sales = new Sales();
                        sales.setControl_number(requisitionIssueSlip.getControl_number());
                        sales.setTotal_sales((double) Math.round(totalSales * 100d) / 100d);
                        sales.setDate_transacted(AppTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-YYYY")));
                        sales.setIncome((double) Math.round(totalIncome * 100d) / 100d);
                        sales.setTotal_cost(totalCost);

                        salesRepository.save(sales);

                        customerRepository.save(customer);
                    } else {
                        Prompt.failed("Item could not be released! Please check RIS details!");
                    }

                } else {
                    Prompt.failed("An error occurred while saving!");
                }
            }else{
                //Please check item list if there is no zero values
                Prompt.failed("Please check the quantity, in stock and cost of item. It must not be equal to 0!");
                return;
            }



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

    public void searchItemOnDashboard() {
        dashboardSearchField.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                List<Item> itemList = itemsRepository.findAll(hasItemName(nv));
                if (!ObjectUtils.isEmpty(itemList)) {
                    setItemResultOnDashboard(itemList.get(0).getItem_name());
                }
            }
        });
        dashboardSearchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.BACK_SPACE) {
                dashboardSearchResult.setText("");
            } else if (e.getCode() == KeyCode.ENTER) {

            }
        });

    }

    private void setItemResultOnDashboard(String result) {

        dashboardSearchResult.textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {

            }
        });

        dashboardSearchResult.setText(result);
    }

    private Specification<Item> hasItemName(String itemName) {
        return (item, cq, cb) -> cb.like(item.get("item_name"), "%" + itemName + "%");
    }

    private Specification<Item> hasSku(String sku) {
        return (item, cq, cb) -> cb.like(item.get("sku"), "%" + sku + "%");
    }


}
