package com.clinique.clinique.controller;

import com.clinique.clinique.entity.RendezVous;
import com.clinique.clinique.service.RendezVousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/rendezvous")
@Tag(name = "Rendez-vous", description = "API de gestion des rendez-vous")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @Autowired
    public RendezVousController(RendezVousService rendezVousService) {
        this.rendezVousService = rendezVousService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les rendez-vous")
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        List<RendezVous> rendezVous = rendezVousService.findAll();
        return ResponseEntity.ok(rendezVous);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un rendez-vous par ID")
    public ResponseEntity<RendezVous> getRendezVousById(@PathVariable Long id) {
        return rendezVousService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Obtenir les rendez-vous d'un patient")
    public ResponseEntity<List<RendezVous>> getRendezVousByPatient(@PathVariable Long patientId) {
        List<RendezVous> rendezVous = rendezVousService.findByPatientId(patientId);
        return ResponseEntity.ok(rendezVous);
    }

    @GetMapping("/medecin/{medecinId}")
    @Operation(summary = "Obtenir les rendez-vous d'un médecin")
    public ResponseEntity<List<RendezVous>> getRendezVousByMedecin(@PathVariable Long medecinId) {
        List<RendezVous> rendezVous = rendezVousService.findByMedecinId(medecinId);
        return ResponseEntity.ok(rendezVous);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Obtenir les rendez-vous par date")
    public ResponseEntity<List<RendezVous>> getRendezVousByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RendezVous> rendezVous = rendezVousService.findByDate(date);
        return ResponseEntity.ok(rendezVous);
    }

    @PostMapping
    @Operation(summary = "Prendre un rendez-vous")
    public ResponseEntity<?> createRendezVous(@Valid @RequestBody RendezVous rendezVous) {
        try {
            RendezVous savedRendezVous = rendezVousService.save(rendezVous);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRendezVous);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : e.toString()));
        }
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(ex.getMessage());
        return ResponseEntity.badRequest().body(java.util.Map.of("message", errorMessage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un rendez-vous")
    public ResponseEntity<RendezVous> updateRendezVous(@PathVariable Long id, @Valid @RequestBody RendezVous rendezVous) {
        if (!rendezVousService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rendezVous.setId(id);
        try {
            RendezVous updatedRendezVous = rendezVousService.save(rendezVous);
            return ResponseEntity.ok(updatedRendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'un rendez-vous")
    public ResponseEntity<RendezVous> updateRendezVousStatut(@PathVariable Long id, @RequestParam RendezVous.StatutRendezVous statut) {
        try {
            return ResponseEntity.ok(rendezVousService.updateStatut(id, statut));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annuler un rendez-vous")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable Long id) {
        if (!rendezVousService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rendezVousService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/proposer")
    @Operation(summary = "Proposer un nouveau créneau")
    public ResponseEntity<RendezVous> proposerCreneau(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heure) {
        return ResponseEntity.ok(rendezVousService.proposerNouveauCreneau(id, date, heure));
    }

    @PostMapping("/{id}/repondre")
    @Operation(summary = "Répondre à une proposition")
    public ResponseEntity<RendezVous> repondreProposition(
            @PathVariable Long id,
            @RequestParam String action) {
        return ResponseEntity.ok(rendezVousService.repondreProposition(id, action));
    }
}