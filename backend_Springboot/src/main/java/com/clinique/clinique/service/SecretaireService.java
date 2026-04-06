package com.clinique.clinique.service;

import com.clinique.clinique.entity.Secretaire;
import com.clinique.clinique.repository.SecretaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SecretaireService {

    private final SecretaireRepository secretaireRepository;
    private final InvitationService invitationService;

    @Autowired
    public SecretaireService(SecretaireRepository secretaireRepository, InvitationService invitationService) {
        this.secretaireRepository = secretaireRepository;
        this.invitationService = invitationService;
    }

    public List<Secretaire> findAll() {
        return secretaireRepository.findAll();
    }

    public Optional<Secretaire> findById(Long id) {
        return secretaireRepository.findById(id);
    }

    public Secretaire save(Secretaire secretaire) {
        boolean isNew = secretaire.getId() == null;
        if (isNew && (secretaire.getPassword() == null || secretaire.getPassword().isEmpty())) {
            secretaire.setPassword(java.util.UUID.randomUUID().toString());
        }
        Secretaire saved = secretaireRepository.save(secretaire);
        if (isNew) {
            invitationService.generateAndSendInvitation(saved);
        }
        return saved;
    }

    public void deleteById(Long id) {
        secretaireRepository.deleteById(id);
    }
}
