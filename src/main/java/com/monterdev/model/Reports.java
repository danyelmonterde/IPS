package com.monterdev.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "reports")
public class Reports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "reportName")
    private String reportName;

    @Column(name = "reportLocation")
    private String reportLocation;

    @Column(name = "preparedBy")
    private String preparedBy;

    @Column(name = "checkedBy")
    private String checkedBy;

    @Column(name = "notedBy")
    private String notedBy;

    @Column(name = "monthOfReport")
    private String monthOfReport;

    @Column(name = "yearOfReport")
    private String yearOfReport;

    @Column(name = "dayOfReport")
    private String dayOfReport;
}
