package com.clinique.clinique.service;

import com.clinique.clinique.entity.Medecin;
import com.clinique.clinique.entity.Message.SenderType;
import com.clinique.clinique.entity.RendezVous;
import com.clinique.clinique.repository.RendezVousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final MedecinService medecinService;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final com.clinique.clinique.repository.PatientRepository patientRepository;

    @Autowired
    public RendezVousService(RendezVousRepository rendezVousRepository,
                             MedecinService medecinService,
                             NotificationService notificationService,
                             MessageService messageService,
                             com.clinique.clinique.repository.PatientRepository patientRepository) {
        this.rendezVousRepository = rendezVousRepository;
        this.medecinService = medecinService;
        this.notificationService = notificationService;
        this.messageService = messageService;
        this.patientRepository = patientRepository;
    }

    public List<RendezVous> findAll() {
        return rendezVousRepository.findAll();
    }

    public Optional<RendezVous> findById(Long id) {
        return rendezVousRepository.findById(id);
    }

    public RendezVous save(RendezVous rendezVous) {
        boolean isNew = rendezVous.getId() == null;
        // Fetch the actual references if needed, or at least the medecin to check
        // availability
        Medecin realMedecin = medecinService.findById(rendezVous.getMedecin().getId())
                .orElseThrow(() -> new RuntimeException("Médecin introuvable"));

        com.clinique.clinique.entity.Patient realPatient = patientRepository.findById(rendezVous.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));

        // Mettre à jour l'entité avec le vrai médecin pour la sauvegarde et les
        // vérifications
        rendezVous.setMedecin(realMedecin);
        rendezVous.setPatient(realPatient);

        // Vérifier la disponibilité du médecin
        if (!isMedecinAvailable(realMedecin, rendezVous.getDate(), rendezVous.getHeure(), rendezVous.getId())) {
            throw new RuntimeException("Le médecin n'est pas disponible à cette date et heure");
        }

        RendezVous saved = rendezVousRepository.save(rendezVous);

        if (isNew) {
            notificationService.createNotificationForSecretaire(
                    "Nouveau rendez-vous demandé par le patient " + realPatient.getNom() + " pour le "
                            + saved.getDate(),
                    saved.getId());
        }
        return saved;
    }

    public void deleteById(Long id) {
        rendezVousRepository.findById(id).ifPresent(rv -> {
            notificationService.createNotificationForSecretaire(
                    "Le rendez-vous du patient " + rv.getPatient().getNom() + " prévu le " + rv.getDate()
                            + " a été annulé par le patient.",
                    id);
            // Notifier aussi le médecin
            notificationService.createNotificationForMedecin(rv.getMedecin(),
                    "Le rendez-vous du patient " + rv.getPatient().getNom() + " prévu le " + rv.getDate()
                            + " a été annulé.",
                    id);
        });
        rendezVousRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return rendezVousRepository.existsById(id);
    }

    public List<RendezVous> findByPatientId(Long patientId) {
        return rendezVousRepository.findByPatientId(patientId);
    }

    public List<RendezVous> findByMedecinId(Long medecinId) {
        return rendezVousRepository.findByMedecinId(medecinId);
    }

    public List<RendezVous> findByDate(LocalDate date) {
        return rendezVousRepository.findByDate(date);
    }

    private boolean isMedecinAvailable(Medecin medecin, LocalDate date, LocalTime heure, Long idToExclude) {
        // Vérifier si le médecin est disponible
        if (!medecin.getDisponibilite()) {
            return false;
        }

        // Vérifier s'il n'y a pas déjà un rendez-vous à cette heure
        return rendezVousRepository.findByMedecinIdAndDate(medecin.getId(), date).stream()
                .noneMatch(rv -> rv.getHeure().equals(heure) &&
                        !rv.getId().equals(idToExclude));
    }

    public RendezVous proposerNouveauCreneau(Long rvId, LocalDate date, LocalTime heure) {
        RendezVous rv = rendezVousRepository.findById(rvId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable"));

        rv.setStatut(RendezVous.StatutRendezVous.PROPOSITION);
        rv.setPropositionDate(date);
        rv.setPropositionHeure(heure);
        rv.setChatActif(true);

        RendezVous saved = rendezVousRepository.save(rv);

        // Notify patient
        notificationService.createNotification(rv.getPatient(),
                "Une nouvelle proposition de créneau a été faite pour votre rendez-vous du " + rv.getDate() +
                        ". Nouvelle date proposée : " + date + " à " + heure,
                rvId);

        // Initial message in chat
        messageService.sendMessage(rvId, SenderType.SECRETAIRE,
                "Bonjour, ce créneau n'est pas disponible. Je vous propose le " + date + " à " + heure
                        + ". Est-ce que cela vous convient ?");

        return saved;
    }

    public RendezVous repondreProposition(Long rvId, String action) {
        RendezVous rv = rendezVousRepository.findById(rvId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable"));

        if (action.equalsIgnoreCase("ACCEPTER")) {
            rv.setStatut(RendezVous.StatutRendezVous.CONFIRME);
            rv.setDate(rv.getPropositionDate());
            rv.setHeure(rv.getPropositionHeure());
            rv.setChatActif(false);
            notificationService.createNotification(rv.getPatient(),
                    "Votre rendez-vous a été confirmé pour le " + rv.getDate(), rvId);
            notificationService.createNotificationForSecretaire(
                    "Le patient " + rv.getPatient().getNom() + " a confirmé la proposition du " + rv.getDate(), rvId);
            notificationService.createNotificationForMedecin(rv.getMedecin(),
                    "Le rendez-vous du patient " + rv.getPatient().getNom() + " a été confirmé pour le " + rv.getDate(), rvId);
            messageService.sendMessage(rvId, SenderType.PATIENT, "J'accepte cette proposition.");
        } else if (action.equalsIgnoreCase("REFUSER")) {
            rv.setStatut(RendezVous.StatutRendezVous.ANNULE);
            rv.setChatActif(false);
            notificationService.createNotification(rv.getPatient(), "Le rendez-vous a été annulé suite à votre refus.",
                    rvId);
            notificationService.createNotificationForSecretaire(
                    "Le patient " + rv.getPatient().getNom() + " a refusé la proposition", rvId);
            notificationService.createNotificationForMedecin(rv.getMedecin(),
                    "Le patient " + rv.getPatient().getNom() + " a refusé la proposition de créneau pour le " + rv.getDate(), rvId);
            messageService.sendMessage(rvId, SenderType.PATIENT, "Je refuse cette proposition.");
        }

        return rendezVousRepository.save(rv);
    }

    public RendezVous updateStatut(Long id, RendezVous.StatutRendezVous statut) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable"));

        rendezVous.setStatut(statut);
        RendezVous saved = this.save(rendezVous);

        if (statut == RendezVous.StatutRendezVous.CONFIRME) {
            notificationService.createNotification(saved.getPatient(),
                    "Votre rendez-vous du " + saved.getDate() + " à " + saved.getHeure()
                            + " a été confirmé par le Le secrétariat.",
                    saved.getId());
            notificationService.createNotificationForMedecin(saved.getMedecin(),
                    "Le rendez-vous du patient " + saved.getPatient().getNom() + " du " + saved.getDate() + " est confirmé.",
                    saved.getId());
        } else if (statut == RendezVous.StatutRendezVous.ANNULE) {
            notificationService.createNotification(saved.getPatient(),
                    "Votre rendez-vous du " + saved.getDate() + " à " + saved.getHeure()
                            + " a été annulé par le Le secrétariat.",
                    saved.getId());
            notificationService.createNotificationForMedecin(saved.getMedecin(),
                    "Le rendez-vous du patient " + saved.getPatient().getNom() + " du " + saved.getDate() + " a été annulé.",
                    saved.getId());
        }

        return saved;
    }
}