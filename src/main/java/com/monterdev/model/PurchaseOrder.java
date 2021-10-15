package com.monterdev.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "purchaseorder")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "inStock")
    private int inStock;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "purchaseCost")
    private double purchaseCost;

    @Column(name = "sku")
    private int sku;

    @Column(name = "amount")
    private double amount;

}
