package com.monterdev.model;

import lombok.*;

import javax.persistence.*;
import java.util.Comparator;

@Getter
@Setter
@Entity
@Builder
@Table(name = "itemcategory")
@AllArgsConstructor
@NoArgsConstructor
public class ItemCategory implements Comparable<ItemCategory> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String category_name;

    @Override
    public int compareTo(ItemCategory o) {
        return this.getCategory_name().compareTo(o.getCategory_name());

    }
}
