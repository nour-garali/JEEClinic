package com.clinique.clinique.service;

import com.clinique.clinique.entity.Medecin;
import com.clinique.clinique.repository.MedecinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedecinService {

    private final MedecinRepository medecinRepository;
    private final InvitationService invitationService;

    @Autowired
    public MedecinService(MedecinRepository medecinRepository, InvitationService invitationService) {
        this.medecinRepository = medecinRepository;
        this.invitationService = invitationService;
    }

    public List<Medecin> findAll() {
        return medecinRepository.findAll();
    }

    public Optional<Medecin> findById(Long id) {
        return medecinRepository.findById(id);
    }

    public Medecin save(Medecin medecin) {
        boolean isNew = medecin.getId() == null;
        if (isNew && (medecin.getPassword() == null || medecin.getPassword().isEmpty())) {
            medecin.setPassword(java.util.UUID.randomUUID().toString());
        }
        Medecin savedMedecin = medecinRepository.save(medecin);
        
        if (isNew) {
            invitationService.generateAndSendInvitation(savedMedecin);
        }
        
        return savedMedecin;
    }

    public void deleteById(Long id) {
        medecinRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return medecinRepository.existsById(id);
    }

    public List<Medecin> findByDisponibiliteTrue() {
        return medecinRepository.findAll().stream()
                .filter(Medecin::getDisponibilite)
                .toList();
    }

    public List<Medecin> findBySecteurId(Long secteurId) {
        return medecinRepository.findBySecteursIdAndDisponibiliteTrue(secteurId);
    }
}