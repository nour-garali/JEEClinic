package com.clinique.clinique.controller;

import com.clinique.clinique.entity.Secteur;
import com.clinique.clinique.service.SecteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/secteurs")
@CrossOrigin(origins = "http://localhost:4200")
public class SecteurController {

    private final SecteurService secteurService;

    @Autowired
    public SecteurController(SecteurService secteurService) {
        this.secteurService = secteurService;
    }

    @GetMapping
    public List<Secteur> getAllSecteurs() {
        return secteurService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Secteur> getSecteurById(@PathVariable Long id) {
        return secteurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Secteur createSecteur(@RequestBody Secteur secteur) {
        return secteurService.save(secteur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Secteur> updateSecteur(@PathVariable Long id, @RequestBody Secteur secteurDetails) {
        return secteurService.findById(id)
                .map(secteur -> {
                    secteur.setNom(secteurDetails.getNom());
                    secteur.setDescription(secteurDetails.getDescription());
                    return ResponseEntity.ok(secteurService.save(secteur));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecteur(@PathVariable Long id) {
        if (secteurService.findById(id).isPresent()) {
            secteurService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
