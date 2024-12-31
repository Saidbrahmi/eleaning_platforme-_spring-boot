package com.example.sabora_platforme.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@Entity


public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public  String firstname;
    public String lastname;
    public String Formation;
    public Long phoneNumber;
    public Long parentPhoneNumber;
    public Date dateInscrit;
    public  float commission;

    public Long price;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus status;
}
