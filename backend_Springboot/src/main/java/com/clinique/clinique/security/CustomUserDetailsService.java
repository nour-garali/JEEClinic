package com.clinique.clinique.security;

import com.clinique.clinique.repository.AdminRepository;
import com.clinique.clinique.repository.MedecinRepository;
import com.clinique.clinique.repository.PatientRepository;
import com.clinique.clinique.repository.SecretaireRepository;
import com.clinique.clinique.entity.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final AdminRepository adminRepository;
    private final SecretaireRepository secretaireRepository;

    // ✅ Constructeur explicite remplace @RequiredArgsConstructor
    @Autowired
    public CustomUserDetailsService(PatientRepository patientRepository,
                                    MedecinRepository medecinRepository,
                                    AdminRepository adminRepository,
                                    SecretaireRepository secretaireRepository) {
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.adminRepository = adminRepository;
        this.secretaireRepository = secretaireRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Check Patient
        var patient = patientRepository.findByEmail(email);
        if (patient.isPresent()) {
            var p = patient.get();
            return User.builder()
                    .username(p.getEmail())
                    .password(p.getPassword())
                    .roles(p.getRole().name())
                    .build();
        }

        // 2. Check Medecin (Must be ACTIVE)
        var medecin = medecinRepository.findByEmail(email);
        if (medecin.isPresent() && medecin.get().getStatus() == UserStatus.ACTIVE) {
            var m = medecin.get();
            return User.builder()
                    .username(m.getEmail())
                    .password(m.getPassword())
                    .roles(m.getRole().name())
                    .build();
        }

        // 3. Check Secretaire (Must be ACTIVE)
        var secretaire = secretaireRepository.findByEmail(email);
        if (secretaire.isPresent() && secretaire.get().getStatus() == UserStatus.ACTIVE) {
            var s = secretaire.get();
            return User.builder()
                    .username(s.getEmail())
                    .password(s.getPassword())
                    .roles(s.getRole().name())
                    .build();
        }

        // 4. Check Admin
        var admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            var a = admin.get();
            return User.builder()
                    .username(a.getEmail())
                    .password(a.getPassword())
                    .roles(a.getRole().name())
                    .build();
        }

        throw new UsernameNotFoundException(
            "Utilisateur non trouvé ou compte non activé : " + email);
    }
}