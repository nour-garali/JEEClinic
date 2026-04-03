package com.clinique.clinique.repository;

import com.clinique.clinique.entity.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findByPatientId(Long patientId);
    List<RendezVous> findByMedecinId(Long medecinId);
    List<RendezVous> findByDate(LocalDate date);
    List<RendezVous> findByMedecinIdAndDate(Long medecinId, LocalDate date);
}