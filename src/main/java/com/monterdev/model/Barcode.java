package com.monterdev.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "barcode")
public class Barcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String bar_code_path;

    private LocalDateTime date_created;

    private int sku;
}
