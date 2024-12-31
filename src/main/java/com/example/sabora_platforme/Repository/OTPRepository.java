package com.example.sabora_platforme.Repository;

import com.example.sabora_platforme.Payload.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface OTPRepository extends JpaRepository<OTP,Long> {
    OTP findByIdentificationAndExpiredDateAfter(String identification, Date now);

    OTP findByIdentification(String identification);

}
