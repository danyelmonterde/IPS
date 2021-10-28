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

    private String report_name;

    private String report_location;

    private String prepared_by;

    private String checked_by;

    private String noted_by;

    private String month_of_report;

    private String year_of_report;

    private String day_of_report;
}
