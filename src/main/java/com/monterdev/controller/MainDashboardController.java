package com.monterdev.controller;

import com.google.zxing.NotFoundException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.Item;
import com.monterdev.repository.ItemsRepository;
import com.monterdev.util.Prompt;
import com.monterdev.util.StageLoader;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;

import static com.monterdev.constants.GlobalConfiguration.defaultCategoryData;
import static com.monterdev.constants.GlobalConfiguration.getConfigValue;
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

    private static int rowIndex = 0;

    private int indexToBeRemoved = 0;

    private List<Item> itemCart = new ArrayList<>();

    private boolean isEmpty = true;

    private TranslateTransition slide = new TranslateTransition();
    private TranslateTransition slideMainAnchorpane = new TranslateTransition();
    private TranslateTransition slideSearchPane = new TranslateTransition();

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
            if (oldValue != newValue && !ObjectUtils.isEmpty(sku.getText())) {

                ObservableList<Node> nodeStream = midHbox.getChildren();

                for (Node node : nodeStream) {
                    if (node instanceof ScrollPane) {
                        Node node2 = ((ScrollPane) node).getContent();
                        if (node2 instanceof AnchorPane) {
                            for (Node node3 : ((AnchorPane) node2).getChildren()) {
                                if (node3 instanceof VBox) {
                                    VBox vBox = (VBox) node3;
                                    HBox row = createRow(vBox, selectedItem, rowIndex-1);
                                    row.setId(String.valueOf(rowIndex-1));
                                    VBox.setMargin(row, new Insets(20.0, 0.0, 0.0, 0.0));
                                    vBox.getChildren().add(rowIndex-1, row);

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
            itemLists = new ArrayList<>();
            initialItemList.forEach(itemLists::add);
            if (newValue != oldValue && currentLocationBanner.getText().equalsIgnoreCase("RELEASE ITEMS") && !ObjectUtils.isEmpty(selectedItem.getItem_name())) {
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

        JFXTextField quantity = createTextField("QUANTITY", "LIGHT GRAY");
        quantity.setText(String.valueOf(currentItem.getQuantity()));
        quantity.setId(String.valueOf(currentIndex));
        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                itemCart.get(Integer.parseInt(quantity.getId())).setQuantity(Integer.parseInt(newValue));
            }
        });

        JFXTextField cost = createTextField("COST", "LIGHT GRAY");
        cost.setText(String.valueOf(currentItem.getCost()));
        cost.setId(String.valueOf(currentIndex));
        cost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                itemCart.get(Integer.parseInt(cost.getId())).setCost(Double.parseDouble(newValue));
            }
        });

        JFXTextField amount = createTextField("AMOUNT", "LIGHT GRAY");
        amount.setText(String.valueOf(currentItem.getQuantity() * currentItem.getCost()));
        amount.setEditable(false);

        JFXButton edit = createButtonWithoutText("EDIT");
        JFXButton delete = createButtonWithoutText("DELETE");
        delete.setId(Integer.toString(currentIndex));
        delete.setOnAction(del -> {

            vBox.getChildren().remove(delete.getParent());
            itemCart.remove(Integer.parseInt(delete.getId()));
            selectedItem.setItem_name("");
            sku.setText("");
            selectedItem.setSku(0);
            if(vBox.getChildren().size()==0){
                rowIndex =1;
                refresher();
            }else{
                rowIndex--;
            }
        });

        HBox hBox = new HBox();
        HBox.setMargin(name, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(quantity, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(cost, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(amount, new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(edit, new Insets(35.0, 0.0, 0.0, 20.0));
        HBox.setMargin(delete, new Insets(35.0, 0.0, 0.0, 20.0));
        hBox.getChildren().addAll(name, quantity, cost, amount, edit, delete);


        return hBox;

    }



    public void getReportsModule(ActionEvent actionEvent) {
        resetHboxes();
        currentLocationBanner.setText("Reports Module");
        Label label = new Label("Reports Module");
        topHbox.getChildren().add(label);
    }

    private void resetHboxes() {
        topHbox.getChildren().clear();
        midHbox.getChildren().clear();
        midHbox.setPrefHeight(100.0);
        bottomHbox.getChildren().clear();

    }

    public void getItemsModule() {
        resetHboxes();
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
        editItemController.create();
        currentLocationBanner.setText("Inventory");
        topHbox.getChildren().addAll(itemListController.createItemList());

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
            getReleaseItemModule();
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

    public void getReleaseItemModule() {
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


        JFXTextField itemName = new JFXTextField();
        itemName.textProperty().addListener((observableValue, oldValue, newValue)->{
            if(oldValue!=newValue){
                boolean isPresent = itemLists.stream().filter(e -> e.getSku() == Integer.parseInt(newValue)).findFirst().isPresent();
                if(isPresent) {
                    rowIndex++;
                    refresher();
                }
            }
        });

        JFXButton openCamera = new JFXButton("Open Camera");

        openCamera.setOnAction(e -> {
            new StageLoader().load(CaptureQrCodeController.class, applicationContext);
        });

        topHbox.getChildren().addAll(itemName, openCamera);
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
        getDashboardModule(null);
    }

    private void releaseItemsOnCart() {
        itemCart.stream().forEach(item -> {
            System.out.println("SKU: "+ item.getSku());
            System.out.println("Item name: "+item.getItem_name());
            System.out.println("Quantity: "+item.getQuantity());
            System.out.println("Cost: "+item.getCost());
            System.out.println("In Stock: "+item.getIn_stock());
            System.out.println("- - - - - - - - - - - - - - - - - - - - ");
            System.out.println("\n");

            int inStock = item.getIn_stock() - item.getQuantity();
            item.setQuantity(inStock);
            item.setIn_stock(inStock);
        });
        if(!ObjectUtils.isEmpty(itemsRepository.saveAll(itemCart))){
            Prompt.success("Item released!");
        }else{
            Prompt.failed("An error occured while saving!");
        }
    }


    public void getBackupModule(ActionEvent actionEvent) {
    }
}
