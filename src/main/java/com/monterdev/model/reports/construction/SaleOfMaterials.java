package com.monterdev.model.reports.construction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleOfMaterials {

    private String date;

    private String name;

    private String orNumber;

    private double sales;

    private String referenceNumber;

    private double cost;

    private double income;
}
