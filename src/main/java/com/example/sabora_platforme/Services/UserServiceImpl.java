package com.example.sabora_platforme.Services;

import com.example.sabora_platforme.Entities.EStatus;
import com.example.sabora_platforme.Entities.Student;
import com.example.sabora_platforme.Entities.User;
import com.example.sabora_platforme.Payload.ChangePasswordRequest;
import com.example.sabora_platforme.Repository.Pre_SellerRepository;
import com.example.sabora_platforme.Repository.StudentRepository;
import com.example.sabora_platforme.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.sabora_platforme.Services.EmailSenderService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service

public class UserServiceImpl implements IUserService {
    @Autowired
    StudentRepository studentRepository;
    Pre_SellerRepository preSellerRepository;
    @Autowired
    private OTPInterface otpInterface;
    @Autowired
    private EmailSenderService emailUtile;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public List<Student> addStudents(List<Student> students) {
        return studentRepository.saveAll(students);
    }

    @Override
    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student retrieveStudent(Long idUser) {
        return studentRepository.findById(idUser).orElse(null);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getAllStudentsDate(Date dateInscrit) {
        return studentRepository.findByDateInscrit(dateInscrit);
    }

    @Override
    public void removeStudent(long idStudent) {
        studentRepository.deleteById(idStudent);
    }

    @Autowired
    private UserRepository userRepository;

    private final double commissionRate = 0.1; // Taux de commission de 10%

    public void calculateAndSetCommissions() {
        List<Student> enrolledStudents = studentRepository.findByStatus(EStatus.ENROLLED);

        for (Student student : enrolledStudents) {
            if (student.getCommission() == 0) { // Si la commission n'a pas encore été calculée
                double commission = calculateCommissionAmount(student.getPrice());
                student.setCommission((float) commission);
                studentRepository.save(student);
            }
        }
    }

    private double calculateCommissionAmount(double price) {
        return price * commissionRate;
    }

    public double getTotalCommission() {
        calculateAndSetCommissions(); // Calculer et enregistrer les commissions pour les étudiants éligibles

        List<Student> students = studentRepository.findByStatus(EStatus.ENROLLED);
        double totalCommission = 0;

        for (Student student : students) {
            totalCommission += student.getCommission();
        }

        return totalCommission;
    }


    //}
    @Override
    public List<Student> getStudentsBetweenDates(LocalDate startDate, LocalDate endDate) {
        // Convertir LocalDate en Date
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return studentRepository.findByDateInscritBetween(start, end);
    }

    public ResponseEntity<?> updatePasswordBymail(String email, ChangePasswordRequest updatePasswordDto) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            Boolean verif = otpInterface.VerifierOTP(updatePasswordDto.getCode());
            if (verif == false) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                user.get().setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
                userRepository.save(user.get());
                return new ResponseEntity<>(HttpStatus.OK);
            }

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    public ResponseEntity<?> userforgetpassword(String email) {
        // Log initial
        System.out.println("Début de la méthode userforgetpassword pour l'email : " + email);

        // Vérification de l'existence de l'utilisateur
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("Utilisateur trouvé : " + user.get().getUsername());

            // Génération du code OTP
            String verificationCode;
            try {
                verificationCode = otpInterface.GenerateOTp().getIdentification();
                System.out.println("Code OTP généré avec succès : " + verificationCode);
            } catch (Exception e) {
                System.out.println("Erreur lors de la génération du code OTP : " + e.getMessage());
                return new ResponseEntity<>("Erreur lors de la génération du code OTP", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Préparation du message email
            String newLine = "<br/>"; // HTML line break
            String htmlMessage = "<div style='border: 1px solid #ccc; padding: 10px; margin-bottom: 10px;'>"
                    + "Une tentative de réinitialisation du mot de passe a été effectuée " + newLine
                    + "<strong>Code de vérification :</strong>" + newLine
                    + "<p>NB : ce code est valide pour 15 minutes</p> " + verificationCode + newLine
                    + "</div>";



            // Envoi de l'email
            try {
                emailUtile.send(user.get().getEmail(), "Avez-vous oublié votre mot de passe ?" + user.get().getUsername(), htmlMessage);
                System.out.println("Email envoyé avec succès à : " + user.get().getEmail());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
                return new ResponseEntity<>("Erreur lors de l'envoi de l'email : " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }

        } else {
            System.out.println("Utilisateur non trouvé pour l'email : " + email);
            return new ResponseEntity<>("Utilisateur non trouvé", HttpStatus.NOT_FOUND);
        }

    }
}







