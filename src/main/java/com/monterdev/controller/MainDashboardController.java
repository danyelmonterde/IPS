package com.monterdev.controller;

import com.google.zxing.NotFoundException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.model.Item;
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
import javafx.scene.shape.Line;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

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

    private ScheduledExecutorService timer;

    private int rowIndex=0;

    private int indexToBeRemoved=0;

    private List<Item> itemCart = new ArrayList<>();

    private boolean isEmpty = true;

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
            TranslateTransition slide = new TranslateTransition();
            TranslateTransition slideMainAnchorpane = new TranslateTransition();
            TranslateTransition slideSearchPane = new TranslateTransition();

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
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(sidebarAnchorpane);

            TranslateTransition slideMainAnchorpane = new TranslateTransition();
            slideMainAnchorpane.setDuration(Duration.seconds(0.4));
            slideMainAnchorpane.setNode(mainAnchorpane);

            TranslateTransition slideSearchPane = new TranslateTransition();
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
        sku.textProperty().addListener((obs,old,newv)->{
            if(old!=newv){
                ObservableList<Node> nodeStream = midHbox.getChildren();

                for(Node node: nodeStream){
                    if(node instanceof ScrollPane){
                        Node node2 = ((ScrollPane) node).getContent();
                        if(node2 instanceof AnchorPane){
                            for (Node node3: ((AnchorPane) node2).getChildren()){
                                if(node3 instanceof VBox){
                                    VBox vBox = (VBox) node3;
                                    HBox row = createRow(vBox,selectedItem,rowIndex);
                                    VBox.setMargin(row,new Insets(20.0, 0.0, 0.0, 0.0));
                                    vBox.getChildren().add(rowIndex, row);
                                    rowIndex++;
                                    itemCart.add(selectedItem);
                                }
                            }
                        }

                    }
                }
            }

        });
        refreshReleaseItemsOnMouseHover();
    }

    private void refreshReleaseItemsOnMouseHover(){

        mainAnchorpane.hoverProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != oldValue && currentLocationBanner.getText().equalsIgnoreCase("RELEASE ITEMS") && !ObjectUtils.isEmpty(selectedItem.getItem_name())){

                ObservableList<Node> nodeStreamTopHbox = topHbox.getChildren();
                for(Node node: nodeStreamTopHbox){
                    if(node instanceof JFXTextField){
                        ((JFXTextField) node).setText(selectedItem.getItem_name());
                        sku.setText(selectedItem.getItem_name());
                    }
                }

            }
        });
    }

    private HBox createRow(VBox vBox,Item item, int currentIndex) {

        Item currentItem = new Item();
        itemLists.stream().forEach(s ->{
            if(s.getSku()==item.getSku()){
                currentItem.setLow_stock(s.getLow_stock());
                currentItem.setMargin(s.getMargin());
                currentItem.setTag(s.getTag());
                currentItem.setCost(s.getCost());
                currentItem.setIn_stock(s.getIn_stock());
                currentItem.setSku(s.getSku());
                currentItem.setQuantity(s.getQuantity());
                currentItem.setSub_category_detail(s.getSub_category_detail());
                currentItem.setItem_name(s.getItem_name());
            }
        });

        JFXTextField name = createTextField("ITEM NAME","LIGHT GRAY");
        name.setText(currentItem.getItem_name());
        JFXTextField quantity = createTextField("QUANTITY","LIGHT GRAY");
        quantity.setText(String.valueOf(currentItem.getQuantity()));
        JFXTextField cost = createTextField("COST","LIGHT GRAY");
        cost.setText(String.valueOf(currentItem.getCost()));
        JFXTextField amount = createTextField("AMOUNT","LIGHT GRAY");
        amount.setText(String.valueOf(currentItem.getQuantity()*currentItem.getCost()));
        JFXButton edit = createButtonWithoutText("EDIT");
        JFXButton delete = createButtonWithoutText("DELETE");
        delete.setId(Integer.toString(currentIndex));
        deleteOnClick(vBox,delete);

        HBox hBox = new HBox();
        HBox.setMargin(name,new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(quantity,new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(cost,new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(amount,new Insets(50.0, 0.0, 0.0, 20.0));
        HBox.setMargin(edit,new Insets(35.0,0.0,0.0,20.0));
        HBox.setMargin(delete,new Insets(35.0,0.0,0.0,20.0));
        hBox.getChildren().addAll(name,quantity,cost,amount,edit,delete);


        currentIndex++;
        System.out.println("row created");
        return hBox;
    }

    private void deleteOnClick(VBox vBox,JFXButton delete){
        delete.setOnAction(e->{
            System.out.println(delete.getId());
            //vBox.getChildren().remove(delete.getId());
        });
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

        AnchorPane anchorPane  = new AnchorPane();
        anchorPane.setPrefWidth(800);
        anchorPane.setPrefHeight(800);


        VBox vbox = new VBox();

        anchorPane.getChildren().add(vbox);
        AnchorPane.setLeftAnchor(vbox,0.0);
        AnchorPane.setRightAnchor(vbox,0.0);
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox,0.0);

        scrollPane.setContent(anchorPane);


        JFXTextField itemName = new JFXTextField();
        itemNameOnChange(itemName);
        JFXButton openCamera = new JFXButton("Open Camera");

        openCamera.setOnAction(e -> {
           new StageLoader().load(CaptureQrCodeController.class, applicationContext);
        });

        topHbox.getChildren().addAll(itemName, openCamera);
        midHbox.getChildren().addAll(scrollPane);
        midHbox.setPrefHeight(500.0);
        HBox.setMargin(scrollPane, new Insets(20.0, 0.0, 0.0, 20.0));

        try {
            currentLocationBanner.setText(readQrCodeImage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }


    private void itemNameOnChange(JFXTextField itemName){
        itemName.textProperty().addListener((observable, oldValue, newValue)->{
            if(selectedItem.getSku()!=0){
                itemName.setText(newValue);
            }
        });
    }





    public void getBackupModule(ActionEvent actionEvent) {
    }
}
