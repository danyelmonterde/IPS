package com.monterdev.model;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomItemList {

    private JFXCheckBox itemName;
    private JFXComboBox category;
    private JFXTextField price;
    private Label cost;
    private Label margin;
    private JFXTextField inStock;

}
