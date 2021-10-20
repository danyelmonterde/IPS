package com.monterdev.controller;

import com.google.zxing.NotFoundException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.monterdev.constants.GlobalConfiguration;
import com.monterdev.model.Item;
import com.monterdev.util.QrCodeUtil;
import com.monterdev.util.StageLoader;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

    @Autowired
    private ItemListController itemListController;

    @Autowired
    private EditItemController editItemController;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private Item selectedItem;


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
        selectedItem.setSub_category_detail(GlobalConfiguration.DEFAULT_CATEGORY_DATA);
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
        JFXTextField sku = new JFXTextField();
        JFXButton openCamera = new JFXButton("Open Camera");
        openCamera.setOnAction(e->{
            new StageLoader().load(CaptureQrCodeController.class,applicationContext);
        });
        topHbox.getChildren().addAll(sku,openCamera);
        try {
            currentLocationBanner.setText(QrCodeUtil.readQrCodeImage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getBackupModule(ActionEvent actionEvent) {
    }
}
