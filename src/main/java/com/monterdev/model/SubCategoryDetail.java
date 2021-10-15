package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "subcategorydetail")
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String sub_category_detail;

    private String sub_category_header;
}

