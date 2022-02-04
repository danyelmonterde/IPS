package com.monterdev.model;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CapturedItem {

    private int sku;
    private String item_name;
    private int quantity;
    private double cost;
    private int in_stock;
    private int low_stock;
    private String tag;
    private String unit;
    private String item_category;
}
