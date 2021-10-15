package com.monterdev.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "stockadjustment")
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "reason")
    private String reason;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "sku")
    private int sku;

    @Column(name = "inStock")
    private int inStock;

    @Column(name = "addStock")
    private int addStock;

    @Column(name = "cost")
    private double cost;

    @Column(name = "stockAfter")
    private int stockAfter;

    @Column(name = "expectedStock")
    private int expectedStock;

    @Column(name = "removeStock")
    private int removeStock;
}
