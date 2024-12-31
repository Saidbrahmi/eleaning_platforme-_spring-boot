package com.example.sabora_platforme.Controller;


import com.example.sabora_platforme.Entities.ERole;
import com.example.sabora_platforme.Entities.Role;
import com.example.sabora_platforme.Entities.User;
import com.example.sabora_platforme.Payload.*;
import com.example.sabora_platforme.Repository.RoleRepository;
import com.example.sabora_platforme.Repository.UserRepository;
import com.example.sabora_platforme.Security.Jwt.JwtUtils;
import com.example.sabora_platforme.Services.OTPInterface;
import com.example.sabora_platforme.Services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")


public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


   @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

       Authentication authentication = authenticationManager
               .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

       SecurityContextHolder.getContext().setAuthentication(authentication);

       UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

       ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

       List<String> roles = userDetails.getAuthorities().stream()
               .map(item -> item.getAuthority())
               .collect(Collectors.toList());

       return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
               .body(new UserInfoResponse(userDetails.getId(),
                       userDetails.getUsername(),
                       userDetails.getEmail(),
                       roles));
   }
    /*
  @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
      logger.info("Authentication request received for username: {}", loginRequest.getUsername());

      Authentication authentication;
      try {
          logger.info("Attempting to authenticate user...");
          authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
          logger.info("Authentication successful for username: {}", loginRequest.getUsername());
      } catch (Exception e) {
          logger.error("Authentication failed for username: {}", loginRequest.getUsername(), e);
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
      }

      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserDetailsImpl userDetails;
      try {
          userDetails = (UserDetailsImpl) authentication.getPrincipal();
          logger.info("User details loaded successfully for username: {}", loginRequest.getUsername());
      } catch (Exception e) {
          logger.error("Failed to load user details for username: {}", loginRequest.getUsername(), e);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load user details: " + e.getMessage());
      }

      ResponseCookie jwtCookie;
      try {
          jwtCookie = jwtUtils.generateJwtCookie(userDetails);
          logger.info("JWT cookie generated successfully for username: {}", loginRequest.getUsername());
      } catch (Exception e) {
          logger.error("Failed to generate JWT cookie for username: {}", loginRequest.getUsername(), e);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate JWT cookie: " + e.getMessage());
      }

      List<String> roles;
      try {
          roles = userDetails.getAuthorities().stream()
                  .map(item -> item.getAuthority())
                  .collect(Collectors.toList());
          logger.info("User roles extracted successfully for username: {}", loginRequest.getUsername());
      } catch (Exception e) {
          logger.error("Failed to extract user roles for username: {}", loginRequest.getUsername(), e);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to extract user roles: " + e.getMessage());
      }

      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
              .body(new UserInfoResponse(userDetails.getId(),
                      userDetails.getUsername(),
                      userDetails.getEmail(),
                      roles));
  }*/


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role StuRole = roleRepository.findByName(ERole.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(StuRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "Pre":
                        Role PreRole = roleRepository.findByName(ERole.ROLE_PRE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(PreRole);

                        break;
                    default:
                        Role StuRole = roleRepository.findByName(ERole.ROLE_STUDENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(StuRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
    OTPInterface otpInterface;
    @PostMapping("/GenerateOTP")
    public OTP GenerateOTp() {
        return otpInterface.GenerateOTp();
    }
    @PostMapping("/VerifierOTP/{identification}")
    public Boolean VerifierOTP(@PathVariable("identification") String identification)  {
        return otpInterface.VerifierOTP(identification);
    }
    @PostMapping("/ResendOTP")
    public OTP ResendOTP(@RequestBody OTP existingOTP) {
        return otpInterface.ResendOTP(existingOTP);
    }
    @DeleteMapping("/DeleteOTP")
    public void DeleteOTP() {
        otpInterface.DeleteOTP();
    }
}