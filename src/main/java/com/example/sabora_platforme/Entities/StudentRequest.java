package com.example.sabora_platforme.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter

public class StudentRequest {
    private String firstname;
    private String lastname;
    private String formation;
    private Long phoneNumber;
    private Long parentPhoneNumber;
    private Date dateInscrit;
   // private float commission;
    private Long price;
    private String status;
}
