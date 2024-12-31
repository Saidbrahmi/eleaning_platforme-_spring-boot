package com.example.sabora_platforme.Payload;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    String code;
}

