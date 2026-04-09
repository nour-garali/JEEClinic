package com.clinique.clinique.controller;

import com.clinique.clinique.entity.Medecin;
import com.clinique.clinique.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medecins")
@Tag(name = "Médecins", description = "API de gestion des médecins")
public class MedecinController {

    private final MedecinService medecinService;

    @Autowired
    public MedecinController(MedecinService medecinService) {
        this.medecinService = medecinService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les médecins")
    public ResponseEntity<List<Medecin>> getAllMedecins() {
        List<Medecin> medecins = medecinService.findAll();
        return ResponseEntity.ok(medecins);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Lister les médecins disponibles")
    public ResponseEntity<List<Medecin>> getMedecinsDisponibles() {
        List<Medecin> medecins = medecinService.findByDisponibiliteTrue();
        return ResponseEntity.ok(medecins);
    }

    @GetMapping("/secteur/{secteurId}")
    @Operation(summary = "Lister les médecins disponibles par secteur")
    public ResponseEntity<List<Medecin>> getMedecinsBySecteur(@PathVariable Long secteurId) {
        List<Medecin> medecins = medecinService.findBySecteurId(secteurId);
        return ResponseEntity.ok(medecins);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un médecin par ID")
    public ResponseEntity<Medecin> getMedecinById(@PathVariable Long id) {
        return medecinService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau médecin")
    public ResponseEntity<Medecin> createMedecin(@Valid @RequestBody Medecin medecin) {
        Medecin savedMedecin = medecinService.save(medecin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMedecin);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un médecin")
    public ResponseEntity<Medecin> updateMedecin(@PathVariable Long id, @Valid @RequestBody Medecin medecin) {
        return medecinService.findById(id).map(existing -> {
            existing.setNom(medecin.getNom());
            existing.setSpecialite(medecin.getSpecialite());
            existing.setDisponibilite(medecin.getDisponibilite());
            existing.setEmail(medecin.getEmail());
            if (medecin.getUsername() != null) existing.setUsername(medecin.getUsername());
            if (medecin.getPassword() != null && !medecin.getPassword().isEmpty()) existing.setPassword(medecin.getPassword());
            if (medecin.getRole() != null) existing.setRole(medecin.getRole());
            if (medecin.getStatus() != null) existing.setStatus(medecin.getStatus());
            existing.setSecteurs(medecin.getSecteurs());
            return ResponseEntity.ok(medecinService.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un médecin")
    public ResponseEntity<Void> deleteMedecin(@PathVariable Long id) {
        if (!medecinService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        medecinService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}