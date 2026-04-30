package com.clinique.clinique.controller;

import com.clinique.clinique.entity.Message;
import com.clinique.clinique.entity.Message.SenderType;
import com.clinique.clinique.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/subscribe/{rvId}")
    public SseEmitter subscribeToChat(@PathVariable Long rvId) {
        return messageService.subscribeToChat(rvId);
    }

    @GetMapping("/rendezvous/{rvId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long rvId) {
        return ResponseEntity.ok(messageService.getMessagesByRendezVous(rvId));
    }

    @PostMapping("/rendezvous/{rvId}")
    public ResponseEntity<Message> sendMessage(
            @PathVariable Long rvId,
            @RequestParam SenderType sender,
            @RequestBody String contenu) {
        return ResponseEntity.ok(messageService.sendMessage(rvId, sender, contenu));
    }
}
