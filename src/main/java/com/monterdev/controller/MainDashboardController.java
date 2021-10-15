package com.monterdev.controller;

import com.jfoenix.controls.JFXButton;
import com.monterdev.model.Item;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

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
    private JFXButton sidebarLogout;

    @Autowired
    private ItemListController itemListController;

    @Autowired
    private EditItemController editItemController;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private Item selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Exit.setOnMouseClicked(event -> {
            System.exit(-176);
        });
        sidebarAnchorpane.setTranslateX(0);
        mainAnchorpane.setTranslateX(0);
        Menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            TranslateTransition slideMainAnchorpane = new TranslateTransition();

            slide.setDuration(Duration.seconds(0.4));
            slideMainAnchorpane.setDuration(Duration.seconds(0.4));

            slide.setNode(sidebarAnchorpane);
            slideMainAnchorpane.setNode(mainAnchorpane);

            slide.setToX(-176);
            slideMainAnchorpane.setToX(-176);

            slide.play();
            slideMainAnchorpane.play();

            sidebarAnchorpane.setTranslateX(0);
            mainAnchorpane.setTranslateX(0);

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

            slide.setToX(0);
            slide.play();

            slideMainAnchorpane.setToX(0);
            slideMainAnchorpane.play();

            sidebarAnchorpane.setTranslateX(-176);
            mainAnchorpane.setTranslateX(-176);

            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(true);
                MenuClose.setVisible(false);
            });
        });
    }

    public void getReportsModule(ActionEvent actionEvent) {
        resetHboxes();
        Label label = new Label("Reports Module");
        topHbox.getChildren().add(label);
    }

    private void resetHboxes() {
        topHbox.getChildren().clear();
        midHbox.getChildren().clear();
        bottomHbox.getChildren().clear();

    }

    public void getItemsModule(ActionEvent actionEvent) {
        resetHboxes();
        Label label = new Label("Items Module");
        selectedItem = applicationContext.getBean(Item.class);
        selectedItem.setTag(null);
        selectedItem.setItem_name("");
        selectedItem.setLow_stock(0);
        selectedItem.setPrice(0.0);
        selectedItem.setSku(0);
        selectedItem.setMargin(0.0);
        selectedItem.setCost(0.0);
        selectedItem.setIn_stock(0);
        selectedItem.setSub_category_detail("TEST-CATEGORY-1");
        editItemController.create(actionEvent);

    }

    public void getDashboardModule(ActionEvent actionEvent) {
        resetHboxes();
        Label label = new Label("Dashboard Module");
        topHbox.getChildren().add(label);
    }

    public void getInventoryModule(ActionEvent actionEvent) {
        resetHboxes();
        Label label = new Label("Inventory Module");
        topHbox.getChildren().addAll(itemListController.createItemList());
    }

    public void getCustomersModule(ActionEvent actionEvent) {
        resetHboxes();
        Label label = new Label("Customers Module");
        topHbox.getChildren().add(label);
    }

    public void getSettingsModule(ActionEvent actionEvent) {
        resetHboxes();
        Label label = new Label("Settings Module");
        topHbox.getChildren().add(label);
    }

    public void getLogoutModule(ActionEvent actionEvent) {
    }

}
