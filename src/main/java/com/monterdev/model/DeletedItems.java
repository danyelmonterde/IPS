package com.monterdev.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@Table(name = "deleteditems")
@AllArgsConstructor
@NoArgsConstructor
public class DeletedItems {

    @Id
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
        DeletedItems that = (DeletedItems) o;
        return sku == that.sku && quantity == that.quantity && Double.compare(that.cost, cost) == 0 && Double.compare(that.margin, margin) == 0 && in_stock == that.in_stock && low_stock == that.low_stock && Objects.equals(item_name, that.item_name) && Objects.equals(sub_category_detail, that.sub_category_detail) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, item_name, sub_category_detail, quantity, cost, margin, in_stock, low_stock, tag);
    }
}
