package com.clinique.clinique.controller;

import com.clinique.clinique.service.InvitationService;
import com.clinique.clinique.service.MedecinService;
import com.clinique.clinique.service.SecretaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@Tag(name = "Invitations", description = "API de gestion des invitations")
@CrossOrigin(origins = "http://localhost:4200")
public class InvitationController {

    private final InvitationService invitationService;
    private final MedecinService medecinService;
    private final SecretaireService secretaireService;

    @Autowired
    public InvitationController(InvitationService invitationService,
                                MedecinService medecinService,
                                SecretaireService secretaireService) {
        this.invitationService = invitationService;
        this.medecinService = medecinService;
        this.secretaireService = secretaireService;
    }

    @GetMapping("/validate")
    @Operation(summary = "Valider un token d'invitation")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = invitationService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/accept")
    @Operation(summary = "Accepter l'invitation et définir le mot de passe")
    public ResponseEntity<Void> acceptInvitation(@RequestBody AcceptInvitationRequest request) {
        try {
            invitationService.acceptInvitation(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/resend/medecin/{id}")
    @Operation(summary = "Renvoyer une invitation à un médecin")
    public ResponseEntity<Void> resendMedecinInvitation(@PathVariable Long id) {
        return medecinService.findById(id).map(medecin -> {
            invitationService.generateAndSendInvitation(medecin);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/resend/secretaire/{id}")
    @Operation(summary = "Renvoyer une invitation à un secrétaire")
    public ResponseEntity<Void> resendSecretaireInvitation(@PathVariable Long id) {
        return secretaireService.findById(id).map(secretaire -> {
            invitationService.generateAndSendInvitation(secretaire);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    public static class AcceptInvitationRequest {
        private String token;
        private String newPassword;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
