package com.clinique.clinique.auth;

import com.clinique.clinique.security.JwtUtils;
import com.clinique.clinique.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        String email = authRequest.getEmail();
        String password = authRequest.getPassword();

        var patient = userService.authenticate(email, password);
        if (patient.isPresent()) {
            return buildAuthResponse(patient.get().getEmail(), patient.get().getRole().name(), patient.get().getId());
        }

        var medecin = userService.authenticateMedecin(email, password);
        if (medecin.isPresent()) {
            return buildAuthResponse(medecin.get().getEmail(), medecin.get().getRole().name(), medecin.get().getId());
        }

        var secretaire = userService.authenticateSecretaire(email, password);
        if (secretaire.isPresent()) {
            return buildAuthResponse(secretaire.get().getEmail(), secretaire.get().getRole().name(), secretaire.get().getId());
        }

        var admin = userService.authenticateAdmin(email, password);
        if (admin.isPresent()) {
            return buildAuthResponse(admin.get().getEmail(), admin.get().getRole().name(), admin.get().getId());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private ResponseEntity<AuthResponse> buildAuthResponse(String email, String role, Long userId) {
        UserDetails userDetails = User.builder()
                .username(email)
                .password("")
                .roles(role)
                .build();
        String jwt = jwtUtils.generateToken(userDetails);
        String token = "Bearer " + jwt;
        return ResponseEntity.ok(new AuthResponse(token, role, userId));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            var patient = userService.registerPatient(
                req.getUsername(),
                req.getEmail(),
                req.getPassword(),
                req.getDateNaissance(),
                req.getTel()
            );
            return buildAuthResponse(patient.getEmail(), patient.getRole().name(), patient.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}