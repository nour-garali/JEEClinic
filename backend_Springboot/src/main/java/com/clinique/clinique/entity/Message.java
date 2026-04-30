package com.clinique.clinique.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rendezVousId;

    @Enumerated(EnumType.STRING)
    private SenderType sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    private LocalDateTime date;

    public enum SenderType {
        PATIENT, SECRETAIRE
    }

    public Message() {
    }

    public Message(Long id, Long rendezVousId, SenderType sender, String contenu, LocalDateTime date) {
        this.id = id;
        this.rendezVousId = rendezVousId;
        this.sender = sender;
        this.contenu = contenu;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRendezVousId() {
        return rendezVousId;
    }

    public void setRendezVousId(Long rendezVousId) {
        this.rendezVousId = rendezVousId;
    }

    public SenderType getSender() {
        return sender;
    }

    public void setSender(SenderType sender) {
        this.sender = sender;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", rendezVousId=" + rendezVousId +
                ", sender=" + sender +
                ", contenu='" + contenu + '\'' +
                ", date=" + date +
                '}';
    }


}
