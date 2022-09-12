package com.monterdev.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private LocalDateTime date;

    private String item_name;

    private String item_category;

    private String reason;

    private int adjustment;

    private int stock_after;

    private int stock_before;

    private String updated_by;

    private String remarks;

    private  String unit;

    private double cost;

    private String control_number;

}
