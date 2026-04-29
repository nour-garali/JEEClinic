package com.clinique.clinique.controller;

import com.clinique.clinique.entity.Notification;
import com.clinique.clinique.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/subscribe/{patientId}")
    public SseEmitter subscribe(@PathVariable Long patientId) {
        return notificationService.subscribe(patientId);
    }

    @GetMapping("/subscribe/secretaire")
    public SseEmitter subscribeSecretaire() {
        return notificationService.subscribeSecretaire();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long patientId) {
        return ResponseEntity.ok(notificationService.getNotificationsForPatient(patientId));
    }

    @GetMapping("/patient/{patientId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long patientId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(patientId));
    }

    @GetMapping("/secretaire")
    public ResponseEntity<List<Notification>> getSecretaireNotifications() {
        return ResponseEntity.ok(notificationService.getNotificationsForSecretaire());
    }

    @GetMapping("/secretaire/unread")
    public ResponseEntity<List<Notification>> getSecretaireUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsForSecretaire());
    }

    @GetMapping("/subscribe/medecin/{medecinId}")
    public SseEmitter subscribeMedecin(@PathVariable Long medecinId) {
        return notificationService.subscribeMedecin(medecinId);
    }

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<Notification>> getMedecinNotifications(@PathVariable Long medecinId) {
        return ResponseEntity.ok(notificationService.getNotificationsForMedecin(medecinId));
    }

    @GetMapping("/medecin/{medecinId}/unread")
    public ResponseEntity<List<Notification>> getMedecinUnreadNotifications(@PathVariable Long medecinId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsForMedecin(medecinId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
