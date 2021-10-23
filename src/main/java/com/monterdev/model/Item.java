package com.monterdev.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String sub_category_detail;

    private int quantity;

    private double cost;

    private double margin;

    private int in_stock;

    private int low_stock;

    private String tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return sku == item.sku && quantity == item.quantity && Double.compare(item.cost, cost) == 0 && Double.compare(item.margin, margin) == 0 && in_stock == item.in_stock && low_stock == item.low_stock && Objects.equals(item_name, item.item_name) && Objects.equals(sub_category_detail, item.sub_category_detail) && Objects.equals(tag, item.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, item_name, sub_category_detail, quantity, cost, margin, in_stock, low_stock, tag);
    }
}
