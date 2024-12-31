package com.example.sabora_platforme.Controller;

import com.example.sabora_platforme.Entities.EStatus;
import com.example.sabora_platforme.Entities.Student;
import com.example.sabora_platforme.Entities.StudentRequest;
import com.example.sabora_platforme.Payload.ChangePasswordRequest;
import com.example.sabora_platforme.Services.EmailSenderService;
import com.example.sabora_platforme.Services.IUserService;
import com.example.sabora_platforme.Services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/User")


public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private EmailSenderService emailUtile;


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/add")
    //@PreAuthorize("hasRole('ROLE_PRE_SELLER') or hasRole('ROLE_ADMIN')")
    public Student addStudent(@RequestBody StudentRequest studentRequest) {
        logger.info("Request to add student received with details: {}", studentRequest);

        Student student = new Student();
        student.setFirstname(studentRequest.getFirstname());
        student.setLastname(studentRequest.getLastname());
        student.setFormation(studentRequest.getFormation());
        student.setPhoneNumber(studentRequest.getPhoneNumber());
        student.setParentPhoneNumber(studentRequest.getParentPhoneNumber());
        student.setDateInscrit(studentRequest.getDateInscrit());
        student.setPrice(studentRequest.getPrice());
        student.setStatus(EStatus.valueOf(studentRequest.getStatus().toUpperCase()));

        Student addedStudent;
        try {
            addedStudent = userService.addStudent(student);
            logger.info("Student added successfully: {}", addedStudent);
        } catch (Exception e) {
            logger.error("Failed to add student: {}", e.getMessage());
            throw e; // Or handle the exception as needed
        }

        return addedStudent;
    }

    @PostMapping("/addBatch")
    public List<Student> addStudents(@RequestBody List<Student> students) {
        return userService.addStudents(students);
    }

    @PutMapping("/update")
   // @PreAuthorize("hasRole('ROLE_PRE_SELLER') or hasRole('ROLE_ADMIN')")
    public Student updateStudent(@RequestBody Student student) {
        return userService.updateStudent(student);
    }

    @GetMapping("/get/{id}")
   // @PreAuthorize("hasRole('ROLE_PRE_SELLER') or hasRole('ROLE_ADMIN')")
    public Student retrieveStudent(@PathVariable Long id) {
        return userService.retrieveStudent(id);
    }

    @GetMapping("/all")
    //@PreAuthorize("hasRole('ROLE_PRE_SELLER') or hasRole('ROLE_ADMIN')")
    public List<Student> getAllStudents() {
        return userService.getAllStudents();
    }

    @GetMapping("/byDate")
    @PreAuthorize("hasRole('ROLE_PRE_SELLER') or hasRole('ROLE_ADMIN')")
    public List<Student> getAllStudentsDate(@RequestParam Date dateInscrit) {
        return userService.getAllStudentsDate(dateInscrit);

    }
   /* @GetMapping("/byDate")
    @PreAuthorize("hasRole('ROLE_PRE_SELLER') or hasRole('ROLE_ADMIN')")
    public List<Student> getAllStudentsByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateInscrit) {
        return userService.getAllStudentsDate(dateInscrit);
    }*/

    @GetMapping("/total")
    //@PreAuthorize("hasRole('ROLE_PRE_SELLER') or hasRole('ROLE_ADMIN')")
    public double getTotalCommission() {
        return userService.getTotalCommission();
    }

    @GetMapping("/studentsDate")
    @PreAuthorize("hasRole('ROLE_PRE_SELLER') or hasRole('ROLE_ADMIN')")
   /* public List<Student> getStudentsBetweenDates(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)  {
        return userService.getStudentsBetweenDates(startDate, endDate);
    }*/
    public List<Student> getStudentsBetweenDates(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Appeler la méthode pour calculer les commissions
        userService.getTotalCommission();
        return userService.getStudentsBetweenDates(startDate, endDate);
    }
    @DeleteMapping("/remove/{id}")
    //  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Allow access for users with USER or ADMIN roles
    public void removeStudent(@PathVariable long id) {
        userService.removeStudent(id);
    }


    @PostMapping("forgetpassword/{email}")
    public ResponseEntity<?> userForgetPassword(@PathVariable("email") String email) {
        return userService.userforgetpassword(email);
    }
    @PutMapping("forgetpassbyemail/{email}")
    public ResponseEntity<?> forgetPasswordbyemail(@PathVariable("email") String email, @RequestBody ChangePasswordRequest resetPass) {
        return userService.updatePasswordBymail(email, resetPass);
    }


}

