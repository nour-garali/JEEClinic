package com.clinique.clinique.service;

import com.clinique.clinique.entity.Medecin;
import com.clinique.clinique.entity.Notification;
import com.clinique.clinique.entity.Patient;
import com.clinique.clinique.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, SseEmitter> medecinEmitters = new ConcurrentHashMap<>();
    private final List<SseEmitter> secretaireEmitters = new java.util.concurrent.CopyOnWriteArrayList<>();

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public SseEmitter subscribe(Long patientId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(patientId, emitter);

        emitter.onCompletion(() -> emitters.remove(patientId));
        emitter.onTimeout(() -> emitters.remove(patientId));
        emitter.onError((e) -> emitters.remove(patientId));

        return emitter;
    }

    public SseEmitter subscribeSecretaire() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        secretaireEmitters.add(emitter);

        emitter.onCompletion(() -> secretaireEmitters.remove(emitter));
        emitter.onTimeout(() -> secretaireEmitters.remove(emitter));
        emitter.onError((e) -> secretaireEmitters.remove(emitter));

        return emitter;
    }

    public SseEmitter subscribeMedecin(Long medecinId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        medecinEmitters.put(medecinId, emitter);

        emitter.onCompletion(() -> medecinEmitters.remove(medecinId));
        emitter.onTimeout(() -> medecinEmitters.remove(medecinId));
        emitter.onError((e) -> medecinEmitters.remove(medecinId));

        return emitter;
    }

    public Notification createNotification(Patient patient, String message, Long rvId) {
        Notification notification = new Notification();
        notification.setPatient(patient);
        notification.setMessage(message);
        notification.setRendezVousId(rvId);
        notification.setDate(LocalDateTime.now());
        notification.setLu(false);
        notification.setPourSecretaire(false);

        Notification saved = notificationRepository.save(notification);
        sendToClient(patient.getId(), saved);
        return saved;
    }

    public Notification createNotificationForSecretaire(String message, Long rvId) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRendezVousId(rvId);
        notification.setDate(LocalDateTime.now());
        notification.setLu(false);
        notification.setPourSecretaire(true);

        Notification saved = notificationRepository.save(notification);
        sendToSecretaires(saved);
        return saved;
    }

    public Notification createNotificationForMedecin(Medecin medecin, String message, Long rvId) {
        Notification notification = new Notification();
        notification.setMedecin(medecin);
        notification.setMessage(message);
        notification.setRendezVousId(rvId);
        notification.setDate(LocalDateTime.now());
        notification.setLu(false);

        Notification saved = notificationRepository.save(notification);
        sendToMedecin(medecin.getId(), saved);
        return saved;
    }

    private void sendToClient(Long patientId, Notification notification) {
        SseEmitter emitter = emitters.get(patientId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                emitters.remove(patientId);
            }
        }
    }

    private void sendToMedecin(Long medecinId, Notification notification) {
        SseEmitter emitter = medecinEmitters.get(medecinId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                medecinEmitters.remove(medecinId);
            }
        }
    }

    private void sendToSecretaires(Notification notification) {
        List<SseEmitter> deadEmitters = new java.util.ArrayList<>();
        for (SseEmitter emitter : secretaireEmitters) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        secretaireEmitters.removeAll(deadEmitters);
    }

    public List<Notification> getNotificationsForPatient(Long patientId) {
        return notificationRepository.findByPatientIdOrderByDateDesc(patientId);
    }

    public List<Notification> getUnreadNotifications(Long patientId) {
        return notificationRepository.findByPatientIdAndLuFalse(patientId);
    }

    public List<Notification> getNotificationsForSecretaire() {
        return notificationRepository.findByPourSecretaireTrueOrderByDateDesc();
    }

    public List<Notification> getUnreadNotificationsForSecretaire() {
        return notificationRepository.findByPourSecretaireTrueAndLuFalse();
    }

    public List<Notification> getNotificationsForMedecin(Long medecinId) {
        return notificationRepository.findByMedecinIdOrderByDateDesc(medecinId);
    }

    public List<Notification> getUnreadNotificationsForMedecin(Long medecinId) {
        return notificationRepository.findByMedecinIdAndLuFalse(medecinId);
    }

    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setLu(true);
            notificationRepository.save(n);
        });
    }
}
