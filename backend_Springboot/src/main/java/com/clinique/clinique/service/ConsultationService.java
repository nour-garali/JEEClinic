package com.clinique.clinique.service;

import com.clinique.clinique.entity.*;
import com.clinique.clinique.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final MedecinRepository medecinRepository;
    private final FactureService factureService;

    @Autowired
    public ConsultationService(ConsultationRepository consultationRepository,
                               PatientRepository patientRepository,
                               RendezVousRepository rendezVousRepository,
                               DossierMedicalRepository dossierMedicalRepository,
                               MedecinRepository medecinRepository,
                               FactureService factureService) {
        this.consultationRepository = consultationRepository;
        this.patientRepository = patientRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.medecinRepository = medecinRepository;
        this.factureService = factureService;
    }

    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    public Optional<Consultation> findById(Long id) {
        return consultationRepository.findById(id);
    }

    public Consultation ajouterConsultation(Long patientId, Long medecinId, Long rendezVousId, String diagnostic, String observations, String ordonnance, Double prix) {
        // 1. Récupérer le patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));

        // 2. Vérifier/Créer le dossier médical si nécessaire
        DossierMedical dossier = patient.getDossierMedical();
        if (dossier == null) {
            dossier = new DossierMedical();
            dossier.setPatient(patient);
            dossier = dossierMedicalRepository.save(dossier);
            patient.setDossierMedical(dossier);
        }

        // 3. Récupérer le rendez-vous
        RendezVous rv = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable"));

        // 4. Vérifier le statut du rendez-vous
        if (rv.getStatut() != RendezVous.StatutRendezVous.CONFIRME) {
            throw new RuntimeException("Le rendez-vous doit être en statut CONFIRME pour effectuer une consultation.");
        }

        // 5. Récupérer le médecin créateur
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin introuvable"));

        // 6. Créer la consultation
        Consultation consultation = new Consultation();
        consultation.setDossierMedical(dossier);
        consultation.setRendezVous(rv);
        consultation.setDiagnostic(diagnostic);
        consultation.setObservations(observations);
        consultation.setOrdonnance(ordonnance);
        consultation.setPrix(prix);
        consultation.setCreePar(medecin);
        consultation.setVerrouille(false); // Par défaut non verrouillée

        // 7. Mettre à jour le rendez-vous en TERMINE
        rv.setStatut(RendezVous.StatutRendezVous.TERMINE);
        rendezVousRepository.save(rv);

        // 8. Enregistrer la consultation
        Consultation savedConsultation = consultationRepository.save(consultation);

        // 9. Générer la facture automatiquement
        factureService.genererFacture(savedConsultation.getId());

        return savedConsultation;
    }

    public Consultation verrouillerConsultation(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable"));
        
        consultation.setVerrouille(true);
        return consultationRepository.save(consultation);
    }

    public Consultation modifierConsultation(Long id, String diagnostic, String observations, String ordonnance, Double prix) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable"));

        if (consultation.isVerrouille()) {
            throw new RuntimeException("Cette consultation est verrouillée et ne peut plus être modifiée.");
        }

        consultation.setDiagnostic(diagnostic);
        consultation.setObservations(observations);
        consultation.setOrdonnance(ordonnance);
        consultation.setPrix(prix);

        return consultationRepository.save(consultation);
    }

    public List<Consultation> getHistoriqueMedical(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));
        
        DossierMedical dossier = patient.getDossierMedical();
        if (dossier == null) {
            return new ArrayList<>();
        }
        
        return consultationRepository.findByDossierMedicalIdOrderByDateConsultationDesc(dossier.getId());
    }

    public String getPatientUsername(Long patientId) {
        return patientRepository.findById(patientId)
                .map(Patient::getUsername)
                .orElse(null);
    }

    public void deleteById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable"));
        
        if (consultation.isVerrouille()) {
            throw new RuntimeException("Impossible de supprimer une consultation verrouillée.");
        }
        
        consultationRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return consultationRepository.existsById(id);
    }

    public List<Consultation> findByRendezVousId(Long rendezVousId) {
        return consultationRepository.findByRendezVousId(rendezVousId);
    }
}