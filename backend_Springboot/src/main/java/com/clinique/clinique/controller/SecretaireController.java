package com.clinique.clinique.controller;

import com.clinique.clinique.entity.Secretaire;
import com.clinique.clinique.service.SecretaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/secretaires")
@CrossOrigin(origins = "http://localhost:4200")
public class SecretaireController {

    private final SecretaireService secretaireService;

    @Autowired
    public SecretaireController(SecretaireService secretaireService) {
        this.secretaireService = secretaireService;
    }

    @GetMapping
    public List<Secretaire> getAllSecretaires() {
        return secretaireService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Secretaire> getSecretaireById(@PathVariable Long id) {
        return secretaireService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Secretaire createSecretaire(@RequestBody Secretaire secretaire) {
        return secretaireService.save(secretaire);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Secretaire> updateSecretaire(@PathVariable Long id, @RequestBody Secretaire secretaireDetails) {
        return secretaireService.findById(id)
                .map(secretaire -> {
                    secretaire.setNom(secretaireDetails.getNom());
                    secretaire.setPrenom(secretaireDetails.getPrenom());
                    secretaire.setEmail(secretaireDetails.getEmail());
                    if (secretaireDetails.getPassword() != null && !secretaireDetails.getPassword().isEmpty()) {
                        secretaire.setPassword(secretaireDetails.getPassword());
                    }
                    secretaire.setSecteur(secretaireDetails.getSecteur());
                    return ResponseEntity.ok(secretaireService.save(secretaire));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecretaire(@PathVariable Long id) {
        if (secretaireService.findById(id).isPresent()) {
            secretaireService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
