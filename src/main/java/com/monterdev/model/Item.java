package com.monterdev.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@Table(name = "item")
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sku;

    private String item_name;

    private int quantity;

    private double cost;

    private int in_stock;

    private int low_stock;

    private String tag;

    private String unit;

    private String item_category;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return sku == item.sku && quantity == item.quantity && Double.compare(item.cost, cost) == 0 && in_stock == item.in_stock && low_stock == item.low_stock && Objects.equals(item_name, item.item_name) && Objects.equals(tag, item.tag) && Objects.equals(unit, item.unit) && Objects.equals(item_category, item.item_category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, item_name, quantity, cost, in_stock, low_stock, tag, unit, item_category);
    }
}
