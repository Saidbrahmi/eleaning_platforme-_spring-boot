package com.example.sabora_platforme.Repository;

import com.example.sabora_platforme.Entities.ERole;
import com.example.sabora_platforme.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
