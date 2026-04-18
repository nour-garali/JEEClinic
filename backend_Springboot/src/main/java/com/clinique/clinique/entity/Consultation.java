package com.clinique.clinique.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
public class Consultation {

    // Jackson Views for security filtering
    public static class Views {
        public static class Public {}      // Visible for Admin/Secretary
        public static class Medical extends Public {} // Only for Doctor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_medical_id")
    @JsonIgnore
    private DossierMedical dossierMedical;

    @OneToOne
    @JoinColumn(name = "rendez_vous_id")
    @JsonView(Views.Public.class)
    private RendezVous rendezVous;

    @JsonView(Views.Public.class)
    private LocalDateTime dateConsultation = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Medical.class) // SENSIBLE DATA
    private String diagnostic;

    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Public.class) // Visible (observations aren't diagnostics)
    private String observations;

    @Column(columnDefinition = "TEXT")
    @JsonView(Views.Medical.class) // SENSIBLE DATA
    private String ordonnance;

    @JsonView(Views.Public.class)
    private Double prix;

    @JsonView(Views.Public.class)
    private boolean verrouille = false;

    @CreationTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime dateModification;

    @ManyToOne
    @JoinColumn(name = "medecin_createur_id")
    @JsonView(Views.Public.class)
    private Medecin creePar;

    public Consultation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DossierMedical getDossierMedical() {
        return dossierMedical;
    }

    public void setDossierMedical(DossierMedical dossierMedical) {
        this.dossierMedical = dossierMedical;
    }

    public RendezVous getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
    }

    public LocalDateTime getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDateTime dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getOrdonnance() {
        return ordonnance;
    }

    public void setOrdonnance(String ordonnance) {
        this.ordonnance = ordonnance;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public boolean isVerrouille() {
        return verrouille;
    }

    public void setVerrouille(boolean verrouille) {
        this.verrouille = verrouille;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Medecin getCreePar() {
        return creePar;
    }

    public void setCreePar(Medecin creePar) {
        this.creePar = creePar;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", dateConsultation=" + dateConsultation +
                ", diagnostic='" + diagnostic + '\'' +
                ", observations='" + observations + '\'' +
                ", ordonnance='" + ordonnance + '\'' +
                ", prix=" + prix +
                ", verrouille=" + verrouille +
                '}';
    }
}