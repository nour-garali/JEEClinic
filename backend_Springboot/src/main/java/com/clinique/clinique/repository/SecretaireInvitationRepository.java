package com.clinique.clinique.repository;

import com.clinique.clinique.entity.SecretaireInvitation;
import com.clinique.clinique.entity.Secretaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SecretaireInvitationRepository extends JpaRepository<SecretaireInvitation, Long> {
    Optional<SecretaireInvitation> findByToken(String token);
    Optional<SecretaireInvitation> findBySecretaireAndIsUsedFalse(Secretaire secretaire);
}
