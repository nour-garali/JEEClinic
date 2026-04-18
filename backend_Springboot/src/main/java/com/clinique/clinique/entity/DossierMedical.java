package com.clinique.clinique.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dossiers_medicaux")
public class DossierMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnore
    private Patient patient;

    private LocalDate dateCreation = LocalDate.now();

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consultation> consultations = new ArrayList<>();

    // Getters
    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public LocalDate getDateCreation() { return dateCreation; }
    public List<Consultation> getConsultations() { return consultations; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public void setConsultations(List<Consultation> consultations) { this.consultations = consultations; }
}