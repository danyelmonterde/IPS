package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "requisitionissueslip")
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionIssueSlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ristype;

    private String control_number;

    private String requisition_and_issue_slip_number;

    private String purpose;

    private String requested_by;

    private String division;

    private String office;

    private String responsibility_center_code;

    private String date_transacted;
}
