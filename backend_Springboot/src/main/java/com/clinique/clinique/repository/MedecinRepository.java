package com.clinique.clinique.repository;

import com.clinique.clinique.entity.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    Optional<Medecin> findByEmail(String email);
    boolean existsByEmail(String email);
    java.util.List<Medecin> findBySecteursIdAndDisponibiliteTrue(Long secteurId);
}