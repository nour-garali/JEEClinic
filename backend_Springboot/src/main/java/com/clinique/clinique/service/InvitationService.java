package com.clinique.clinique.service;

import com.clinique.clinique.entity.*;
import com.clinique.clinique.repository.DoctorInvitationRepository;
import com.clinique.clinique.repository.MedecinRepository;
import com.clinique.clinique.repository.SecretaireInvitationRepository;
import com.clinique.clinique.repository.SecretaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvitationService {

    private final DoctorInvitationRepository doctorInvitationRepository;
    private final SecretaireInvitationRepository secretaireInvitationRepository;
    private final MedecinRepository medecinRepository;
    private final SecretaireRepository secretaireRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InvitationService(DoctorInvitationRepository doctorInvitationRepository,
                             SecretaireInvitationRepository secretaireInvitationRepository,
                             MedecinRepository medecinRepository,
                             SecretaireRepository secretaireRepository,
                             EmailService emailService,
                             PasswordEncoder passwordEncoder) {
        this.doctorInvitationRepository = doctorInvitationRepository;
        this.secretaireInvitationRepository = secretaireInvitationRepository;
        this.medecinRepository = medecinRepository;
        this.secretaireRepository = secretaireRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void generateAndSendInvitation(Medecin medecin) {
        doctorInvitationRepository.findByMedecinAndIsUsedFalse(medecin).ifPresent(invitation -> {
            invitation.setIsUsed(true);
            doctorInvitationRepository.save(invitation);
        });

        String token = UUID.randomUUID().toString();
        DoctorInvitation invitation = new DoctorInvitation();
        invitation.setToken(token);
        invitation.setMedecin(medecin);
        invitation.setExpirationDate(LocalDateTime.now().plusHours(24));
        invitation.setIsUsed(false);

        doctorInvitationRepository.save(invitation);
        
        try {
            emailService.sendInvitationEmail(medecin.getEmail(), token, UserRole.MEDECIN);
        } catch (Exception e) {
            // Log the error but don't fail the transaction if email fails
            // User can resend invitation later from dashboard
            System.err.println(">>>> [Back] Échec de l'envoi de l'email à " + medecin.getEmail() + " : " + e.getMessage());
        }
    }

    @Transactional
    public void generateAndSendInvitation(Secretaire secretaire) {
        secretaireInvitationRepository.findBySecretaireAndIsUsedFalse(secretaire).ifPresent(invitation -> {
            invitation.setIsUsed(true);
            secretaireInvitationRepository.save(invitation);
        });

        String token = UUID.randomUUID().toString();
        SecretaireInvitation invitation = new SecretaireInvitation();
        invitation.setToken(token);
        invitation.setSecretaire(secretaire);
        invitation.setExpirationDate(LocalDateTime.now().plusHours(24));
        invitation.setIsUsed(false);

        secretaireInvitationRepository.save(invitation);
        
        try {
            emailService.sendInvitationEmail(secretaire.getEmail(), token, UserRole.SECRETAIRE);
        } catch (Exception e) {
            System.err.println(">>>> [Back] Échec de l'envoi de l'email à " + secretaire.getEmail() + " : " + e.getMessage());
        }
    }

    public boolean validateToken(String token) {
        // Check doctors
        Optional<DoctorInvitation> doctorInv = doctorInvitationRepository.findByToken(token);
        if (doctorInv.isPresent()) {
            return !doctorInv.get().getIsUsed() && doctorInv.get().getExpirationDate().isAfter(LocalDateTime.now());
        }
        
        // Check secretaries
        Optional<SecretaireInvitation> secretaireInv = secretaireInvitationRepository.findByToken(token);
        if (secretaireInv.isPresent()) {
            return !secretaireInv.get().getIsUsed() && secretaireInv.get().getExpirationDate().isAfter(LocalDateTime.now());
        }

        return false;
    }

    @Transactional
    public void acceptInvitation(String token, String newPassword) {
        // Check doctors
        Optional<DoctorInvitation> doctorInv = doctorInvitationRepository.findByToken(token);
        if (doctorInv.isPresent()) {
            DoctorInvitation invitation = doctorInv.get();
            validateInvitation(invitation.getIsUsed(), invitation.getExpirationDate());
            
            Medecin medecin = invitation.getMedecin();
            medecin.setPassword(passwordEncoder.encode(newPassword));
            medecin.setStatus(UserStatus.ACTIVE);
            medecinRepository.save(medecin);
            
            invitation.setIsUsed(true);
            doctorInvitationRepository.save(invitation);
            return;
        }

        // Check secretaries
        Optional<SecretaireInvitation> secretaireInv = secretaireInvitationRepository.findByToken(token);
        if (secretaireInv.isPresent()) {
            SecretaireInvitation invitation = secretaireInv.get();
            validateInvitation(invitation.getIsUsed(), invitation.getExpirationDate());
            
            Secretaire secretaire = invitation.getSecretaire();
            secretaire.setPassword(passwordEncoder.encode(newPassword));
            secretaire.setStatus(UserStatus.ACTIVE);
            secretaireRepository.save(secretaire);
            
            invitation.setIsUsed(true);
            secretaireInvitationRepository.save(invitation);
            return;
        }

        throw new RuntimeException("Token invalide");
    }

    private void validateInvitation(boolean isUsed, LocalDateTime expiry) {
        if (isUsed || expiry.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le lien d'invitation a expiré ou a déjà été utilisé");
        }
    }
}
