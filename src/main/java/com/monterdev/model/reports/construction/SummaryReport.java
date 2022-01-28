package com.monterdev.model.reports.construction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryReport {

    private List<SaleOfMaterials> saleOfMaterialsList;
}
