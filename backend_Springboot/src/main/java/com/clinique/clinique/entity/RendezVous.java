package com.clinique.clinique.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "rendez_vous")
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    private Medecin medecin;

    @NotNull(message = "La date est obligatoire")
    @FutureOrPresent(message = "La date doit être dans le futur ou aujourd'hui")
    private LocalDate date;

    @NotNull(message = "L'heure est obligatoire")
    private LocalTime heure;

    @NotBlank(message = "Le motif est obligatoire")
    private String motif;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le statut est obligatoire")
    @Column(name = "statut_rv", length = 50)
    private StatutRendezVous statut;

    private LocalDate propositionDate;
    private LocalTime propositionHeure;

    private boolean chatActif = false;

    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Consultation consultation;

    public enum StatutRendezVous {
        EN_ATTENTE, CONFIRME, ANNULE, TERMINE, PROPOSITION
    }

    public RendezVous() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public StatutRendezVous getStatut() {
        return statut;
    }

    public void setStatut(StatutRendezVous statut) {
        this.statut = statut;
    }

    public LocalDate getPropositionDate() {
        return propositionDate;
    }

    public void setPropositionDate(LocalDate propositionDate) {
        this.propositionDate = propositionDate;
    }

    public LocalTime getPropositionHeure() {
        return propositionHeure;
    }

    public void setPropositionHeure(LocalTime propositionHeure) {
        this.propositionHeure = propositionHeure;
    }

    public boolean isChatActif() {
        return chatActif;
    }

    public void setChatActif(boolean chatActif) {
        this.chatActif = chatActif;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", date=" + date +
                ", heure=" + heure +
                ", motif='" + motif + '\'' +
                ", statut=" + statut +
                ", chatActif=" + chatActif +
                '}';
    }
}