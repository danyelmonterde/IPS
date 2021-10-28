package com.monterdev.util;

import com.jfoenix.controls.*;
import com.monterdev.constants.ItemsUIConfiguration;
import com.monterdev.model.Item;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static com.monterdev.constants.ItemsUIConfiguration.ITEMS_VBOX_CLASSES;

public class ComponentCreator {

    @Autowired
    private List<Item> itemLists;

    @Autowired
    private ConfigurableApplicationContext applicationContext;


    public static JFXButton addJFXButton(String function, JFXButton.ButtonType buttonType) {
        ItemsUIConfiguration itemsUIConfiguration = new ItemsUIConfiguration();
        JFXButton jfxButton = new JFXButton(itemsUIConfiguration.getButtonName(function));
        jfxButton.setButtonType(buttonType);
        jfxButton.setFont(itemsUIConfiguration.getFont(function));
        jfxButton.setTextFill(itemsUIConfiguration.getTextFill(function));
        jfxButton.getStyleClass().addAll(itemsUIConfiguration.getCssClassorId(function));
        jfxButton.setPadding(itemsUIConfiguration.getPadding(function));
        return jfxButton;
    }

    public static JFXCheckBox addCheckbox(String function) {
        ItemsUIConfiguration itemsUIConfiguration = new ItemsUIConfiguration();
        JFXCheckBox jfxCheckBox = new JFXCheckBox(function);
        jfxCheckBox.setFont(itemsUIConfiguration.getFont(function));
        jfxCheckBox.setTextFill(itemsUIConfiguration.getTextFill(function));
        jfxCheckBox.getStyleClass().addAll(itemsUIConfiguration.getCssClassorId(function));
        jfxCheckBox.setPadding(itemsUIConfiguration.getPadding(function));
        return jfxCheckBox;
    }

    public static Label addLabel(String function) {
        ItemsUIConfiguration itemsUIConfiguration = new ItemsUIConfiguration();
        Label label = new Label(itemsUIConfiguration.getLabelName(function));
        label.setFont(itemsUIConfiguration.getFont(function));
        label.setTextFill(itemsUIConfiguration.getTextFill(function));
        return label;
    }

    public static Label addLabel(String function, String labelName) {
        ItemsUIConfiguration itemsUIConfiguration = new ItemsUIConfiguration();
        Label label = new Label(itemsUIConfiguration.getLabelName(labelName));
        label.setFont(itemsUIConfiguration.getFont(function));
        label.setTextFill(itemsUIConfiguration.getTextFill(function));
        label.setPadding(itemsUIConfiguration.getPadding(function));
        return label;
    }


    public static JFXTextField addTextFieldWithPadding(String function,Pos pos) {
        ItemsUIConfiguration itemsUIConfiguration = new ItemsUIConfiguration();
        JFXTextField jfxTextField = new JFXTextField();
        jfxTextField.setAlignment(pos);
        jfxTextField.setPadding(itemsUIConfiguration.getPadding(function));
        return jfxTextField;
    }

    public static JFXTextField createTextField(String promptText, String paint){
        JFXTextField textField = new JFXTextField();
        textField.setLabelFloat(true);
        textField.setPromptText(promptText);
        textField.setUnFocusColor(Color.LIGHTGRAY);
        return textField;
    }

    public static JFXButton createButtonWithoutText(String buttonType){
        JFXButton jfxButton  = new JFXButton();
        jfxButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        ImageView imageView = new ImageView();

        if(buttonType.equalsIgnoreCase("DELETE")){
            Image image = null;
            try {
                image = new Image(new FileInputStream(ItemsUIConfiguration.getDeleteButtonImage()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageView.setImage(image);
            imageView.setFitHeight(25.0);
            imageView.setFitWidth(25.0);
        }
        jfxButton.setGraphic(imageView);
        return jfxButton;
    }

    public static JFXComboBox addCombobox(String function, List<String> values) {
        JFXComboBox comboBox = new JFXComboBox();
        values.stream().forEach(value -> {
            comboBox.getItems().add(value);
        });
        comboBox.getSelectionModel().select(0);
        return comboBox;
    }

    public static JFXComboBox addComboboxWithPadding(String function) {
        JFXComboBox comboBox = new JFXComboBox();
        return comboBox;
    }

    public static VBox addVbox(Node...  node) {
        ItemsUIConfiguration itemsUIConfiguration = new ItemsUIConfiguration();
        VBox vBox = new VBox();
        vBox.getStyleClass().addAll(itemsUIConfiguration.getCssClassorId(ITEMS_VBOX_CLASSES));
        vBox.setPadding(itemsUIConfiguration.getPadding(ITEMS_VBOX_CLASSES));
        vBox.getChildren().addAll(node);

        return vBox;
    }

    public static AnchorPane createSearchBox(){
        AnchorPane searchBoxContainer = new AnchorPane();
        VBox searchGroup = new VBox();
        JFXListView searchedItems = new JFXListView();
        searchedItems.setPrefHeight(100);
        searchGroup.getChildren().addAll(searchedItems);
        searchBoxContainer.getChildren().addAll(searchGroup);


        searchBoxContainer.setPrefWidth(225);
        searchBoxContainer.setPrefHeight(212);
        AnchorPane.setLeftAnchor(searchGroup, 0.0);
        AnchorPane.setRightAnchor(searchGroup, 0.0);

        return searchBoxContainer;
    }

}
