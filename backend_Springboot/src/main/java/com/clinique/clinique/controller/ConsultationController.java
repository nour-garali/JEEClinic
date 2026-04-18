package com.clinique.clinique.controller;

import com.clinique.clinique.dto.ConsultationRequest;
import com.clinique.clinique.entity.Consultation;
import com.clinique.clinique.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@Tag(name = "Consultations", description = "API de gestion du dossier médical")
public class ConsultationController {

    private final ConsultationService consultationService;

    @Autowired
    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    @Operation(summary = "Ajouter une nouvelle consultation (Médecin uniquement)")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<Consultation> ajouterConsultation(@Valid @RequestBody ConsultationRequest request) {
        Consultation consultation = consultationService.ajouterConsultation(
                request.getPatientId(),
                request.getMedecinId(),
                request.getRendezVousId(),
                request.getDiagnostic(),
                request.getObservations(),
                request.getOrdonnance(),
                request.getPrix()
        );
        return ResponseEntity.ok(consultation);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Récupérer l'historique médical d'un patient (Filtrage selon rôle)")
    public MappingJacksonValue getHistoriqueMedical(@PathVariable Long patientId) {
        List<Consultation> historique = consultationService.getHistoriqueMedical(patientId);
        
        MappingJacksonValue mapping = new MappingJacksonValue(historique);
        
        // Déterminer la vue selon le rôle et l'identité de l'utilisateur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        
        boolean isMedecin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEDECIN"));
        
        boolean isOwner = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")) && 
                currentUsername.equals(consultationService.getPatientUsername(patientId));
        
        if (isMedecin || isOwner) {
            mapping.setSerializationView(Consultation.Views.Medical.class);
        } else {
            mapping.setSerializationView(Consultation.Views.Public.class);
        }
        
        return mapping;
    }

    @PatchMapping("/{id}/verrouiller")
    @Operation(summary = "Verrouiller une consultation (Médecin uniquement)")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<Consultation> verrouillerConsultation(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.verrouillerConsultation(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une consultation (Médecin uniquement, si non verrouillée)")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<Consultation> modifierConsultation(
            @PathVariable Long id,
            @RequestBody ConsultationRequest request) {
        return ResponseEntity.ok(consultationService.modifierConsultation(
                id,
                request.getDiagnostic(),
                request.getObservations(),
                request.getOrdonnance(),
                request.getPrix()
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une consultation (Médecin uniquement, si non verrouillée)")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}