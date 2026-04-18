package com.clinique.clinique.dto;

import jakarta.validation.constraints.NotNull;

public class ConsultationRequest {
    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;
    
    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long medecinId;
    
    @NotNull(message = "L'ID du rendez-vous est obligatoire")
    private Long rendezVousId;
    
    private String diagnostic;
    private String observations;
    private String ordonnance;
    private Double prix;

    public ConsultationRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }

    public Long getRendezVousId() {
        return rendezVousId;
    }

    public void setRendezVousId(Long rendezVousId) {
        this.rendezVousId = rendezVousId;
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

    @Override
    public String toString() {
        return "ConsultationRequest{" +
                "patientId=" + patientId +
                ", medecinId=" + medecinId +
                ", rendezVousId=" + rendezVousId +
                ", diagnostic='" + diagnostic + '\'' +
                ", observations='" + observations + '\'' +
                ", ordonnance='" + ordonnance + '\'' +
                ", prix=" + prix +
                '}';
    }
}
