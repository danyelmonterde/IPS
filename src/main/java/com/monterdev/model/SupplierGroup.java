package com.monterdev.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private int id;

    private String supplier;

    private String receipt_number;

    private int sku;

    private String purchase_order_number;

    private LocalDateTime date_created;
}
