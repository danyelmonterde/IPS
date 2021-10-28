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

    private String reason;

    private String item_name;

    private int sku;

    private int in_stock;

    private int add_stock;

    private double cost;

    private int stock_after;

    private int expected_stock;

    private int remove_stock;
}
