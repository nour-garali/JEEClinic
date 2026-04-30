package com.clinique.clinique.service;

import com.clinique.clinique.entity.Message;
import com.clinique.clinique.entity.Message.SenderType;
import com.clinique.clinique.entity.RendezVous;
import com.clinique.clinique.repository.MessageRepository;
import com.clinique.clinique.repository.RendezVousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RendezVousRepository rendezVousRepository;
    private final NotificationService notificationService;
    private final Map<Long, List<SseEmitter>> chatEmitters = new ConcurrentHashMap<>();

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          RendezVousRepository rendezVousRepository,
                          NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.notificationService = notificationService;
    }

    public SseEmitter subscribeToChat(Long rvId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        chatEmitters.computeIfAbsent(rvId, k -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(rvId, emitter));
        emitter.onTimeout(() -> removeEmitter(rvId, emitter));
        emitter.onError((e) -> removeEmitter(rvId, emitter));

        return emitter;
    }

    private void removeEmitter(Long rvId, SseEmitter emitter) {
        List<SseEmitter> emitters = chatEmitters.get(rvId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }

    public Message sendMessage(Long rvId, SenderType sender, String contenu) {
        Message message = new Message();
        message.setRendezVousId(rvId);
        message.setSender(sender);
        message.setContenu(contenu);
        message.setDate(LocalDateTime.now());

        Message saved = messageRepository.save(message);
        notifyChatSubscribers(rvId, saved);

        if (sender == SenderType.SECRETAIRE) {
            rendezVousRepository.findById(rvId).ifPresent(rv -> {
                notificationService.createNotification(rv.getPatient(), "Nouveau message de la secrétaire : " + contenu, rvId);
            });
        } else if (sender == SenderType.PATIENT) {
            rendezVousRepository.findById(rvId).ifPresent(rv -> {
                notificationService.createNotificationForSecretaire("Nouveau message du patient " + rv.getPatient().getNom() + " : " + contenu, rvId);
            });
        }

        return saved;
    }

    private void notifyChatSubscribers(Long rvId, Message message) {
        List<SseEmitter> emitters = chatEmitters.get(rvId);
        if (emitters != null) {
            List<SseEmitter> deadEmitters = new java.util.ArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("message").data(message));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }
            emitters.removeAll(deadEmitters);
        }
    }

    public List<Message> getMessagesByRendezVous(Long rvId) {

        return messageRepository.findByRendezVousIdOrderByDateAsc(rvId);
    }
}
