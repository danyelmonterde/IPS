package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "ris")
@AllArgsConstructor
@NoArgsConstructor
public class Ris {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String customer_first_name;

    private String customer_middle_name;

    private String customer_last_name;

    private String control_number;

    private String ris_number;

    private String or_number;

    private String cluster;

    private String barangay;

    private String serial_number;

    private String requested_by;

    private String date_transacted;

    private double income;

    private int is_customer_new;

    private double sales;

    private double cost;

    @Override
    public String toString() {
        return "Ris{" +
                "id=" + id +
                ", customer_first_name='" + customer_first_name + '\'' +
                ", customer_middle_name='" + customer_middle_name + '\'' +
                ", customer_last_name='" + customer_last_name + '\'' +
                ", control_number='" + control_number + '\'' +
                ", ris_number='" + ris_number + '\'' +
                ", or_number='" + or_number + '\'' +
                ", cluster='" + cluster + '\'' +
                ", barangay='" + barangay + '\'' +
                ", serial_number='" + serial_number + '\'' +
                ", requested_by='" + requested_by + '\'' +
                ", date_transacted='" + date_transacted + '\'' +
                ", income=" + income +
                ", is_customer_new=" + is_customer_new +
                ", sales=" + sales +
                ", cost=" + cost +
                '}';
    }
}
