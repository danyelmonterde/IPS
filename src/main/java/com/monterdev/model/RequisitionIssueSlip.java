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
    private int is_customer_new;
    private String customer_name;
    private String requisition_and_issue_slip_number;
    private String purpose;
    private String requested_by;
    private String designation;
    private String division;
    private String unit;
    private String office;
    private String responsibility_center_code;
    private String date_transacted;
}
