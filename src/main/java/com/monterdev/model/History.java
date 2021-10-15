package com.monterdev.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "reason")
    private String reason;

    @Column(name = "adjustment")
    private int adjustment;

    @Column(name = "stockAfter")
    private int stockAfter;

    @Column(name = "subCategoryDetail")
    private String subCategoryDetail;

}
