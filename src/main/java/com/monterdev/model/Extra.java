package com.monterdev.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "extra")
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "extraColumn")
    private String extraColumn;

    @Column(name = "value")
    private String value;

    @Column(name = "subCategoryDetail")
    private String subCategoryDetail;

    @Column(name = "controlNumber")
    private String controlNumber;


}
