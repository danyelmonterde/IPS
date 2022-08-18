package com.monterdev.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", columnDefinition = "VARCHAR(255)")
    private String username;

    @Column(name = "password", columnDefinition = "VARCHAR(255)")
    private String password;

    @Column(name = "is_admin", columnDefinition = "TINYINT")
    private boolean is_admin;

    @Column(name = "is_super_admin", columnDefinition = "TINYINT")
    private boolean is_super_admin;
}
