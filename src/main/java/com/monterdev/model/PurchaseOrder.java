package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchaseorder")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String item_name;

    private int in_stock;

    private int quantity;

    private double purchase_cost;

    private int sku;

    private double amount;

}
