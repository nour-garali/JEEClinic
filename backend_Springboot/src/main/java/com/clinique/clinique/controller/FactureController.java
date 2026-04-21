package com.clinique.clinique.controller;

import com.clinique.clinique.entity.Facture;
import com.clinique.clinique.service.FactureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@Tag(name = "Factures", description = "API de gestion de la facturation")
public class FactureController {

    private final FactureService factureService;

    @Autowired
    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les factures (Administration)")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<List<Facture>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @PostMapping("/generation/{consultationId}")
    @Operation(summary = "Générer une facture pour une consultation (Médecin ou Secrétaire)")
    @PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Facture> genererFacture(@PathVariable Long consultationId) {
        return ResponseEntity.ok(factureService.genererFacture(consultationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une facture par son ID")
    public ResponseEntity<Facture> getFacture(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Récupérer toutes les factures d'un patient")
    @PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE', 'PATIENT')")
    public ResponseEntity<List<Facture>> getFacturesByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(factureService.getFacturesByPatient(patientId));
    }

    @GetMapping("/medecin/{medecinId}")
    @Operation(summary = "Récupérer toutes les factures générées par un médecin")
    @PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<Facture>> getFacturesByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(factureService.getFacturesByMedecin(medecinId));
    }

    @GetMapping("/consultation/{consultationId}")
    @Operation(summary = "Récupérer la facture d'une consultation spécifique")
    public ResponseEntity<Facture> getFactureByConsultation(@PathVariable Long consultationId) {
        return ResponseEntity.ok(factureService.getFactureByConsultationId(consultationId));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exporter la facture au format PDF")
    @PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE', 'PATIENT')")
    public ResponseEntity<byte[]> downloadFacturePdf(@PathVariable Long id) {
        byte[] pdfContent = factureService.generateFacturePdf(id);
        Facture facture = factureService.getFactureById(id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"facture-" + facture.getNumeroFacture() + ".pdf\"")
                .body(pdfContent);
    }

    @PatchMapping("/{id}/payer")
    @Operation(summary = "Marquer une facture comme payée (Secrétaire uniquement)")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<Facture> payerFacture(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.payerFacture(id));
    }
}
