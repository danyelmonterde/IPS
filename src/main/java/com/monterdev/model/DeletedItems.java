package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

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

    private double price;

    private double cost;

    private double margin;

    private int in_stock;

    private int low_stock;

    private String tag;
}
