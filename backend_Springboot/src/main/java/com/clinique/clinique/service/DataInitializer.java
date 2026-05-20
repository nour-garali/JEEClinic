package com.clinique.clinique.service;

import com.clinique.clinique.entity.Admin;
import com.clinique.clinique.entity.Medecin;
import com.clinique.clinique.entity.UserRole;
import com.clinique.clinique.repository.AdminRepository;
import com.clinique.clinique.repository.MedecinRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminRepository adminRepository;
    private final MedecinRepository medecinRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(AdminRepository adminRepository,
                           MedecinRepository medecinRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.medecinRepository = medecinRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Initialize Default Admin
        if (!adminRepository.existsByEmail("admin@gmail.com")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Mm20448816"));
            admin.setEmail("admin@gmail.com");
            admin.setNom("Administrateur");
            admin.setRole(UserRole.ADMIN);
            adminRepository.save(admin);
            log.info(">>>> Compte ADMIN créé : admin@gmail.com / Mm20448816");
        }

        // Initialize Default Medecin
        if (!medecinRepository.existsByEmail("docteur@prohealth.com")) {
            Medecin medecin = new Medecin();
            medecin.setPassword(passwordEncoder.encode("docteur123"));
            medecin.setEmail("docteur@prohealth.com");
            medecin.setNom("Dr. Smith");
            medecin.setSpecialite("Généraliste");
            medecin.setDisponibilite(true);
            medecin.setRole(UserRole.MEDECIN);
            medecin.setStatus(com.clinique.clinique.entity.UserStatus.ACTIVE);
            medecinRepository.save(medecin);
            log.info(">>>> Compte MEDECIN créé : docteur@prohealth.com / docteur123");
        }
    }
}
