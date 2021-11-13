package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ristype")
public class RisType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String ristype;

    private String inventory_type;

    private String risfield;

    private String risfieldtype;

    private String risname;
}
