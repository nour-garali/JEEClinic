package com.clinique.clinique.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = true)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id", nullable = true)
    private Medecin medecin;

    private boolean lu = false;

    private LocalDateTime date;

    private Long rendezVousId;

    private boolean pourSecretaire = false;

    public Notification() {
    }

    public Notification(Long id, String message, Patient patient, Medecin medecin, boolean lu, LocalDateTime date, Long rendezVousId, boolean pourSecretaire) {
        this.id = id;
        this.message = message;
        this.patient = patient;
        this.medecin = medecin;
        this.lu = lu;
        this.date = date;
        this.rendezVousId = rendezVousId;
        this.pourSecretaire = pourSecretaire;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getRendezVousId() {
        return rendezVousId;
    }

    public void setRendezVousId(Long rendezVousId) {
        this.rendezVousId = rendezVousId;
    }

    public boolean isPourSecretaire() {
        return pourSecretaire;
    }

    public void setPourSecretaire(boolean pourSecretaire) {
        this.pourSecretaire = pourSecretaire;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", lu=" + lu +
                ", date=" + date +
                ", rendezVousId=" + rendezVousId +
                ", pourSecretaire=" + pourSecretaire +
                '}';
    }


}
