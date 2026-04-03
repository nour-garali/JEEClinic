package com.clinique.clinique.repository;

import com.clinique.clinique.entity.Secretaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SecretaireRepository extends JpaRepository<Secretaire, Long> {
    Optional<Secretaire> findByEmail(String email);
    boolean existsByEmail(String email);
}
