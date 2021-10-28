package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "itemsubcategoryheader")
@AllArgsConstructor
@NoArgsConstructor
public class ItemSubCategoryHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String sub_category_header;

    private String category;
}
