package com.clinique.clinique.service;

import com.clinique.clinique.entity.*;
import com.clinique.clinique.repository.AdminRepository;
import com.clinique.clinique.repository.MedecinRepository;
import com.clinique.clinique.repository.PatientRepository;
import com.clinique.clinique.repository.SecretaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final AdminRepository adminRepository;
    private final SecretaireRepository secretaireRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PatientRepository patientRepository,
                       MedecinRepository medecinRepository,
                       AdminRepository adminRepository,
                       SecretaireRepository secretaireRepository,
                       PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.adminRepository = adminRepository;
        this.secretaireRepository = secretaireRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Patient registerPatient(String username, String email, String password,
                                   LocalDate dateNaissance, String tel) {
        if (patientRepository.existsByUsername(username) || adminRepository.existsByUsername(username)) {
            throw new RuntimeException("Le nom d'utilisateur existe déjà");
        }
        if (patientRepository.existsByEmail(email) || medecinRepository.existsByEmail(email)
                || adminRepository.existsByEmail(email) || secretaireRepository.existsByEmail(email)) {
            throw new RuntimeException("L'email existe déjà");
        }

        Patient patient = new Patient();
        patient.setUsername(username);
        patient.setEmail(email);
        patient.setPassword(passwordEncoder.encode(password));
        patient.setNom(username);
        patient.setDateNaissance(dateNaissance);
        patient.setTel(tel);
        patient.setRole(UserRole.PATIENT);

        DossierMedical dossier = new DossierMedical();
        dossier.setPatient(patient);
        patient.setDossierMedical(dossier);

        return patientRepository.save(patient);
    }

    public Optional<Patient> authenticate(String email, String password) {
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isPresent() && passwordEncoder.matches(password, patient.get().getPassword())) {
            return patient;
        }
        return Optional.empty();
    }

    public Optional<Medecin> authenticateMedecin(String email, String password) {
        Optional<Medecin> medecin = medecinRepository.findByEmail(email);
        if (medecin.isPresent() && passwordEncoder.matches(password, medecin.get().getPassword())) {
            return medecin;
        }
        return Optional.empty();
    }

    public Optional<Admin> authenticateAdmin(String email, String password) {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent() && passwordEncoder.matches(password, admin.get().getPassword())) {
            return admin;
        }
        return Optional.empty();
    }

    public Optional<Secretaire> authenticateSecretaire(String email, String password) {
        Optional<Secretaire> secretaire = secretaireRepository.findByEmail(email);
        if (secretaire.isPresent() && passwordEncoder.matches(password, secretaire.get().getPassword())) {
            return secretaire;
        }
        return Optional.empty();
    }
}