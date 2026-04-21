package com.clinique.clinique.service;

import com.clinique.clinique.entity.Consultation;
import com.clinique.clinique.entity.Facture;
import com.clinique.clinique.entity.StatutFacture;
import com.clinique.clinique.repository.ConsultationRepository;
import com.clinique.clinique.repository.FactureRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FactureService {

    private static final Logger log = LoggerFactory.getLogger(FactureService.class);

    private final FactureRepository factureRepository;
    private final ConsultationRepository consultationRepository;
    private final NotificationService notificationService;

    @Autowired
    public FactureService(FactureRepository factureRepository,
                          ConsultationRepository consultationRepository,
                          NotificationService notificationService) {
        this.factureRepository = factureRepository;
        this.consultationRepository = consultationRepository;
        this.notificationService = notificationService;
    }

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    @Transactional
    public Facture genererFacture(Long consultationId) {
        log.info("Génération de la facture pour la consultation ID : {}", consultationId);

        // 1. Vérifier si une facture existe déjà (évite les doublons)
        if (factureRepository.findByConsultationId(consultationId).isPresent()) {
            log.warn("Une facture existe déjà pour la consultation {}", consultationId);
            return factureRepository.findByConsultationId(consultationId).get();
        }

        // 2. Récupérer la consultation
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation non trouvée avec l'ID : " + consultationId));

        if (consultation.getDossierMedical() == null || consultation.getDossierMedical().getPatient() == null) {
            throw new RuntimeException("Données patient manquantes pour la consultation : " + consultationId);
        }

        // 3. Créer la facture
        Facture facture = new Facture();
        facture.setConsultation(consultation);
        facture.setPatient(consultation.getDossierMedical().getPatient());
        
        // Numéro de facture unique : FAC-YYYYMMDD-CONSULTID
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        facture.setNumeroFacture("FAC-" + datePart + "-" + consultationId);

        // --- Réplication des données (Verrouillage) ---
        facture.setPatientNom(consultation.getDossierMedical().getPatient().getNom());
        facture.setMedecinNom(consultation.getCreePar() != null ? consultation.getCreePar().getNom() : "Inconnu");
        facture.setMedecinSpecialite(consultation.getCreePar() != null ? consultation.getCreePar().getSpecialite() : "N/A");
        
        facture.setDateConsultation(consultation.getDateConsultation());
        facture.setDiagnostic(consultation.getDiagnostic());
        facture.setObservations(consultation.getObservations());
        facture.setOrdonnance(consultation.getOrdonnance());
        facture.setPrix(consultation.getPrix());
        
        facture.setStatut(StatutFacture.EN_ATTENTE);
        facture.setCreePar("SYSTEM_AUTO"); // Peut être mis à jour si un utilisateur déclenche l'action

        // 4. Sauvegarder
        Facture savedFacture = factureRepository.save(facture);
        log.info("Facture générée avec succès : {}", savedFacture.getNumeroFacture());

        // 5. Notifier les parties prenantes
        notifierFacture(savedFacture);

        return savedFacture;
    }

    public void notifierFacture(Facture facture) {
        log.info("Envoi des notifications pour la facture {}", facture.getNumeroFacture());

        // Notification Patient (Réelle via SSE)
        String patientMsg = "SANTÉ INFO : Votre facture du " + 
                facture.getDateConsultation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                " pour la consultation avec Dr " + facture.getMedecinNom() + 
                " est disponible au format PDF.";
        
        notificationService.createNotification(facture.getPatient(), patientMsg, facture.getConsultation().getRendezVous().getId());
        log.info("[SMS SIMULATION] To: {} | Msg: {}", facture.getPatient().getTel(), patientMsg);

        // Notification Médecin (Réelle via SSE)
        String medecinMsg = "SYSTÈME : La facture pour le patient " + facture.getPatientNom() + 
                " (Consultation du " + facture.getDateConsultation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                ") a été générée.";
        
        notificationService.createNotificationForMedecin(facture.getConsultation().getCreePar(), medecinMsg, facture.getConsultation().getRendezVous().getId());
        log.info("[NOTIF DOCTEUR] Dr : {} | Msg: {}", facture.getMedecinNom(), medecinMsg);
    }

    public byte[] generateFacturePdf(Long factureId) {
        Facture facture = getFactureById(factureId);
        log.info("Génération du rapport PDF pour la facture : {}", facture.getNumeroFacture());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.BOLD);
            Paragraph title = new Paragraph("FACTURE MÉDICALE - ProHealth", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            // Info Facture
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("Numéro de facture : " + facture.getNumeroFacture(), boldFont));
            document.add(new Paragraph("Date d'émission : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("Statut : " + facture.getStatut()));
            document.add(new Paragraph(" ")); // Spacer

            // Info Patient / Médecin
            document.add(new Paragraph("DÉTAILS DU PATIENT :", boldFont));
            document.add(new Paragraph("Nom : " + facture.getPatientNom()));
            document.add(new Paragraph("ID Patient : " + facture.getPatient().getId()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("DÉTAILS DU MÉDECIN :", boldFont));
            document.add(new Paragraph("Nom : Dr. " + facture.getMedecinNom()));
            document.add(new Paragraph("Spécialité : " + facture.getMedecinSpecialite()));
            document.add(new Paragraph(" "));

            // Contenu Consultation
            document.add(new Paragraph("DETAILS DE LA CONSULTATION (DU " + facture.getDateConsultation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ") :", boldFont));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Diagnostic :", boldFont));
            document.add(new Paragraph(facture.getDiagnostic()));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Ordonnance :", boldFont));
            document.add(new Paragraph(facture.getOrdonnance()));
            document.add(new Paragraph(" "));

            // Prix Final
            Paragraph total = new Paragraph("MONTANT TOTAL À RÉGLER : " + facture.getPrix() + " DT", titleFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(30);
            document.add(total);

            // Footer
            Paragraph footer = new Paragraph("\n\nMerci de votre confiance. Clinique ProHealth.", 
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            log.info("PDF généré avec succès pour la facture {}", facture.getNumeroFacture());
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF pour la facture {}", factureId, e);
            throw new RuntimeException("Échec de génération du PDF");
        }
    }

    public Facture getFactureById(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID : " + id));
    }

    public List<Facture> getFacturesByPatient(Long patientId) {
        return factureRepository.findByPatientIdOrderByDateCreationDesc(patientId);
    }

    public List<Facture> getFacturesByMedecin(Long medecinId) {
        return factureRepository.findByConsultationCreeParIdOrderByDateCreationDesc(medecinId);
    }

    public Facture getFactureByConsultationId(Long consultationId) {
        return factureRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Aucune facture trouvée pour la consultation : " + consultationId));
    }

    @Transactional
    public Facture payerFacture(Long id) {
        Facture facture = getFactureById(id);
        
        // Récupérer l'utilisateur connecté (Secrétaire)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = (auth != null) ? auth.getName() : "SYSTEM_SEC";

        facture.setStatut(StatutFacture.PAYE);
        facture.setDatePaiement(LocalDateTime.now());
        facture.setPayePar(currentUserName);
        facture.setDateModification(LocalDateTime.now());
        
        log.info("Facture {} marquée comme payée par {}", facture.getNumeroFacture(), currentUserName);
        
        Facture saved = factureRepository.save(facture);
        
        // Notify Doctor
        String medecinMsg = "PAIEMENT : Le paiement de la facture " + saved.getNumeroFacture() + 
                " pour le patient " + saved.getPatientNom() + " a été validé par le secrétariat.";
        notificationService.createNotificationForMedecin(saved.getConsultation().getCreePar(), medecinMsg, saved.getConsultation().getRendezVous().getId());
        
        return saved;
    }
}
