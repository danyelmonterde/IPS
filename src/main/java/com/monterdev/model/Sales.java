package com.monterdev.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "sales")
    private double sales;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "controlNumber")
    private String controlNumber;

    @Column(name = "price")
    private double price;
}
