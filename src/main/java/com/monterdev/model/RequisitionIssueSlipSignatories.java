package com.monterdev.model;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requisitionissueslipsignatories")
public class RequisitionIssueSlipSignatories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @CsvBindByName(column = "id")
    private int id;

    @CsvBindByName(column = "requested_by")
    private String requested_by;

    @CsvBindByName(column = "designation")
    private String designation;

    @CsvBindByName(column = "division")
    private String division;

    @CsvBindByName(column = "unit")
    private String unit;

}
