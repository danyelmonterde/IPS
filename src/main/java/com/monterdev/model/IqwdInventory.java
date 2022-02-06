package com.monterdev.model;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IqwdInventory implements Comparable<IqwdInventory>{

    @Id
    @CsvBindByName(column = "NUMBER")
    private int number;

    @CsvBindByName(column = "DESCRIPTION")
    private String description;

    @CsvBindByName(column = "ACQUISITION_COST")
    private double acquisition_cost;

    @CsvBindByName(column = "AMOUNT")
    private double amount;

    @CsvBindByName(column = "QUANTITY")
    private int quantity;

    @Override
    public int compareTo(IqwdInventory o) {
        return this.getDescription().compareTo(o.getDescription());
    }
}
