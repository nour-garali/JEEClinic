package com.clinique.clinique.repository;

import com.clinique.clinique.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPatientIdOrderByDateDesc(Long patientId);
    List<Notification> findByPatientIdAndLuFalse(Long patientId);
    List<Notification> findByPourSecretaireTrueOrderByDateDesc();
    List<Notification> findByPourSecretaireTrueAndLuFalse();
    List<Notification> findByMedecinIdOrderByDateDesc(Long medecinId);
    List<Notification> findByMedecinIdAndLuFalse(Long medecinId);
}
