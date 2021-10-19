package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "supplier")
@AllArgsConstructor
@NoArgsConstructor
public class SupplierGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sku;

    private String supplier;

    private String receipt_number;

    private String purchase_order_number;
}
