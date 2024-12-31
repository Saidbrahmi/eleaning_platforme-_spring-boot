package com.example.sabora_platforme.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity

public class Pre_Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public  String firstname;
    public String lastname;
    public String email;
   //private float commission;

}
