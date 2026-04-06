package com.clinique.clinique.service;

import com.clinique.clinique.entity.Secteur;
import com.clinique.clinique.repository.SecteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SecteurService {

    private final SecteurRepository secteurRepository;

    @Autowired
    public SecteurService(SecteurRepository secteurRepository) {
        this.secteurRepository = secteurRepository;
    }

    public List<Secteur> findAll() {
        return secteurRepository.findAll();
    }

    public Optional<Secteur> findById(Long id) {
        return secteurRepository.findById(id);
    }

    public Secteur save(Secteur secteur) {
        return secteurRepository.save(secteur);
    }

    public void deleteById(Long id) {
        secteurRepository.deleteById(id);
    }
}
