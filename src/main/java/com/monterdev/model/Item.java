package com.monterdev.model;

import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@Table(name = "item")
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Comparable<Item>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CsvBindByName(column = "sku")
    private int sku;

    @CsvBindByName(column = "item_name")
    private String item_name;

    @CsvBindByName(column = "quantity")
    private int quantity;

    @CsvBindByName(column = "cost")
    private double cost;

    @CsvBindByName(column = "in_stock")
    private int in_stock;

    @CsvBindByName(column = "low_stock")
    private int low_stock;

    @CsvBindByName(column = "tag")
    private String tag;

    @CsvBindByName(column = "unit")
    private String unit;

    @CsvBindByName(column = "item_category")
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

    @Override
    public int compareTo(Item o) {
        return this.getItem_name().compareTo(o.getItem_name());
    }

    public double computeAmount(int quantity, double cost,boolean isCustomerNew){
        return isCustomerNew ? (((cost * 0.2) + cost) * quantity) : (quantity * cost);
    }


}
