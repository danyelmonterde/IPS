package com.monterdev.model;

import com.jfoenix.controls.JFXButton;
import com.monterdev.controller.PointOfSaleController;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    private int itemNumber;

    private int sku;

    private String itemName;

    private String quantity;

    private double averageCost;

    private JFXButton action;

}
