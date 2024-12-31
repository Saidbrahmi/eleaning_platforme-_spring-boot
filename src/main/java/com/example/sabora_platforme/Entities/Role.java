package com.example.sabora_platforme.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Role(ERole name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public Role() {

    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
}
