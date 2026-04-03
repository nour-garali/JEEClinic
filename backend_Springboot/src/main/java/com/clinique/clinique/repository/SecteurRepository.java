package com.clinique.clinique.repository;

import com.clinique.clinique.entity.Secteur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SecteurRepository extends JpaRepository<Secteur, Long> {
    Optional<Secteur> findByNom(String nom);
}
