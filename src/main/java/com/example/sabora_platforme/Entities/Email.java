package com.example.sabora_platforme.Entities;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Email {
    private String to;
    private String subject;
    private String body;
}
