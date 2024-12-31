package com.example.sabora_platforme.Services;

import com.example.sabora_platforme.Entities.Student;
import com.example.sabora_platforme.Entities.User;
import com.example.sabora_platforme.Payload.ChangePasswordRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface IUserService {
    Student addStudent(Student student);
    List<Student> addStudents(List<Student> students);
    Student updateStudent(Student student);
    Student retrieveStudent(Long idUser);
    public List<Student> getAllStudents();
    List<Student> getAllStudentsDate(Date dateInscrit);
    void removeStudent(long idStudent);

    double getTotalCommission();

    public List<Student> getStudentsBetweenDates(LocalDate startDate, LocalDate endDate);
    public ResponseEntity<?> userforgetpassword(String email);
    public  ResponseEntity<?>  updatePasswordBymail(String email, ChangePasswordRequest updatePasswordDto);
}
