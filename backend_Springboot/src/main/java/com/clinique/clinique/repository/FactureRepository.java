package com.clinique.clinique.repository;

import com.clinique.clinique.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    Optional<Facture> findByNumeroFacture(String numeroFacture);
    Optional<Facture> findByConsultationId(Long consultationId);
    List<Facture> findByPatientIdOrderByDateCreationDesc(Long patientId);
    List<Facture> findByConsultationCreeParIdOrderByDateCreationDesc(Long medecinId);
}
