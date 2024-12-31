package com.example.sabora_platforme.Services;

import com.example.sabora_platforme.Payload.OTP;

public interface OTPInterface {
    OTP GenerateOTp( );
    Boolean VerifierOTP ( String identification )  ;

    OTP ResendOTP(OTP existingOTP);
    void  DeleteOTP();
}
