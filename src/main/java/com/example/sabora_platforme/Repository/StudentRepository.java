package com.example.sabora_platforme.Repository;

import com.example.sabora_platforme.Entities.EStatus;
import com.example.sabora_platforme.Entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

    List<Student> findByDateInscrit(Date dateInscrit);
    List<Student> findByStatus(EStatus status);
    List<Student> findByDateInscritBetween(Date startDate, Date endDate);

    void deleteById(long idStudent);
}
