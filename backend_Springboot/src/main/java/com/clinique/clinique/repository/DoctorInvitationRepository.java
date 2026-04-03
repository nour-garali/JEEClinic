package com.clinique.clinique.repository;

import com.clinique.clinique.entity.DoctorInvitation;
import com.clinique.clinique.entity.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorInvitationRepository extends JpaRepository<DoctorInvitation, Long> {
    Optional<DoctorInvitation> findByToken(String token);
    Optional<DoctorInvitation> findByMedecinAndIsUsedFalse(Medecin medecin);
}
