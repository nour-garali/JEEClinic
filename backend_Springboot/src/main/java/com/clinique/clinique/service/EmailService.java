package com.clinique.clinique.service;

import com.clinique.clinique.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInvitationEmail(String toEmail, String token, UserRole role) {
        String roleSegment = role == UserRole.MEDECIN ? "register-doctor" : "register-secretary";
        String inviteUrl = "http://localhost:4200/" + roleSegment + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ProHealth <nourgarali@gmail.com>");
        message.setTo(toEmail);
        message.setSubject("Invitation à rejoindre ProHealth");
        
        String roleText = role == UserRole.MEDECIN ? "médecin" : "secrétaire";
        
        message.setText("Bonjour,\n\nVous avez été invité à rejoindre le système ProHealth en tant que " + roleText + ".\n"
                + "Veuillez cliquer sur le lien ci-dessous pour créer votre mot de passe et finaliser votre inscription :\n\n"
                + inviteUrl + "\n\n"
                + "Ce lien est valable 24 heures.\n\nCordialement,\nL'équipe ProHealth");

        mailSender.send(message);
        log.info(">>>> Invitation envoyée avec succès à {}", toEmail);
    }
}
