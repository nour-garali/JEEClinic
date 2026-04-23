import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { MedecinService, Medecin } from '../../../services/medecin.service';
import { AlertService } from '../../../services/alert.service';
import { SecteurService, Secteur } from '../../../services/secteur.service';
import { SecretaireService, Secretaire } from '../../../services/secretaire.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboard implements OnInit {
  currentView: 'overview' | 'medecins' | 'secteurs' | 'secretaires' = 'overview';
  
  // Data arrays
  medecins: Medecin[] = [];
  secteurs: Secteur[] = [];
  secretaires: Secretaire[] = [];

  // Filtered data
  filteredMedecins: Medecin[] = [];
  filteredSecteurs: Secteur[] = [];
  filteredSecretaires: Secretaire[] = [];
  
  searchQuery: string = '';

  // Modals state
  isModalOpen: boolean = false;
  modalType: 'medecin' | 'secteur' | 'secretaire' = 'medecin';
  editingMode: boolean = false;
  
  // Current editing objects
  currentMedecin: Partial<Medecin> = {};
  currentSecteur: Partial<Secteur> = {};
  currentSecretaire: Partial<Secretaire> = {};

  selectedMedecinsForSecteur: number[] = [];

  constructor(
    private authService: AuthService, 
    private router: Router,
    private medecinService: MedecinService,
    private secteurService: SecteurService,
    private secretaireService: SecretaireService,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAllData();
  }

  loadAllData(): void {
    this.loadMedecins();
    this.loadSecteurs();
    this.loadSecretaires();
  }

  async logout() {
    const result = await this.alertService.confirm(
      'Déconnexion',
      'Êtes-vous sûr de vouloir vous déconnecter ?',
      'Oui, déconnecter',
      'Annuler'
    );

    if (result.isConfirmed) {
      this.authService.logout();
      this.alertService.success('Vous avez été déconnecté avec succès');
      this.router.navigate(['/login']);
    }
  }

  setView(view: 'overview' | 'medecins' | 'secteurs' | 'secretaires') {
    this.currentView = view;
    this.searchQuery = '';
    this.loadAllData();
  }

  // --- Load Methods ---

  loadMedecins(): void {
    this.medecinService.getAllMedecins().subscribe({
      next: (data) => {
        this.medecins = data;
        this.filterData();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur chargement médecins', err)
    });
  }

  loadSecteurs(): void {
    this.secteurService.getAllSecteurs().subscribe({
      next: (data) => {
        this.secteurs = data;
        this.filterData();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur chargement secteurs', err)
    });
  }

  loadSecretaires(): void {
    this.secretaireService.getAllSecretaires().subscribe({
      next: (data) => {
        this.secretaires = data;
        this.filterData();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur chargement secrétaires', err)
    });
  }

  // --- Filter Methods ---

  filterData(): void {
    const query = this.searchQuery.toLowerCase();
    
    if (this.currentView === 'medecins') {
      this.filteredMedecins = this.medecins.filter(m => 
        m.nom.toLowerCase().includes(query) || 
        m.specialite.toLowerCase().includes(query) ||
        (m.email && m.email.toLowerCase().includes(query))
      );
    } else if (this.currentView === 'secteurs') {
      this.filteredSecteurs = this.secteurs.filter(s => 
        s.nom.toLowerCase().includes(query) || 
        (s.description && s.description.toLowerCase().includes(query))
      );
    } else if (this.currentView === 'secretaires') {
      this.filteredSecretaires = this.secretaires.filter(s => 
        s.nom.toLowerCase().includes(query) || 
        s.prenom.toLowerCase().includes(query) ||
        s.email.toLowerCase().includes(query)
      );
    }
  }

  get groupedMedecins(): { secteurName: string, medecins: Medecin[] }[] {
    const groups = new Map<string, Medecin[]>();
    
    // Always show established sectors
    this.secteurs.forEach(s => {
      groups.set(s.nom, []);
    });
    
    groups.set('Non assigné', []);

    for (let med of this.filteredMedecins) {
      if (med.secteurs && med.secteurs.length > 0) {
        for (let s of med.secteurs) {
          if (!groups.has(s.nom)) {
            groups.set(s.nom, []);
          }
          groups.get(s.nom)!.push(med);
        }
      } else {
        groups.get('Non assigné')!.push(med);
      }
    }

    const result = Array.from(groups.entries()).map(([name, medecins]) => ({
      secteurName: name,
      medecins: medecins
    })).filter(g => g.medecins.length > 0 || g.secteurName !== 'Non assigné');

    return result.sort((a, b) => {
      if (a.secteurName === 'Non assigné') return 1;
      if (b.secteurName === 'Non assigné') return -1;
      return a.secteurName.localeCompare(b.secteurName);
    });
  }

  // --- Modal Management ---

  openModal(type: 'medecin' | 'secteur' | 'secretaire', data?: any): void {
    this.modalType = type;
    this.isModalOpen = true;
    this.editingMode = !!data;

    if (type === 'medecin') {
      this.currentMedecin = data ? { ...data } : { disponibilite: true, secteurs: [] };
      if (data && data.secteurs) {
        this.selectedSecteurIdsForMedecin = data.secteurs.map((s: any) => s.id);
      } else {
        this.selectedSecteurIdsForMedecin = [];
      }
    } else if (type === 'secteur') {
      this.currentSecteur = data ? { ...data } : {};
      if (data) {
        this.selectedMedecinsForSecteur = this.medecins.filter(m => m.secteurs && m.secteurs.some((s: any) => s.id === data.id)).map(m => m.id!);
      } else {
        this.selectedMedecinsForSecteur = [];
      }
    } else if (type === 'secretaire') {
      this.currentSecretaire = data ? { ...data } : {};
    }
  }

  closeModal(): void {
    setTimeout(() => {
      this.isModalOpen = false;
      this.cdr.detectChanges();
    });
  }

  selectedSecteurIdsForMedecin: number[] = [];

  toggleSecteurForMedecin(secteurId: number): void {
    const idx = this.selectedSecteurIdsForMedecin.indexOf(secteurId);
    if (idx > -1) {
      this.selectedSecteurIdsForMedecin.splice(idx, 1);
    } else {
      this.selectedSecteurIdsForMedecin.push(secteurId);
    }
    this.currentMedecin.secteurs = this.secteurs.filter(s => this.selectedSecteurIdsForMedecin.includes(s.id!));
  }

  toggleMedecinSecteur(medecinId: number): void {
    const idx = this.selectedMedecinsForSecteur.indexOf(medecinId);
    if (idx > -1) {
      this.selectedMedecinsForSecteur.splice(idx, 1);
    } else {
      this.selectedMedecinsForSecteur.push(medecinId);
    }
  }

  // --- Save Methods ---

  saveMedecin(): void {
    if (!this.editingMode) {
      if (!this.currentMedecin.email) {
        let usernameBase = this.currentMedecin.nom ? this.currentMedecin.nom.toLowerCase().replace(/ /g, '') : 'docteur';
        this.currentMedecin.email = `${usernameBase}@prohealth.com`;
      }
      this.medecinService.createMedecin(this.currentMedecin as Medecin).subscribe({
        next: () => {
          this.loadMedecins();
          this.closeModal();
          this.alertService.success('Médecin créé et invité.');
        },
        error: (err) => this.alertService.error('Erreur: ' + (err.error?.message || 'Serveur indisponible'))
      });
    } else {
      this.medecinService.updateMedecin(this.currentMedecin.id!, this.currentMedecin as Medecin).subscribe({
        next: () => {
          this.loadMedecins();
          this.closeModal();
          this.alertService.success('Médecin mis à jour.');
        },
        error: (err) => {
          console.error(err);
          this.alertService.error('Erreur', 'Impossible de mettre à jour le médecin.');
        }
      });
    }
  }

  saveSecteur(): void {
    if (!this.currentSecteur.nom) {
      this.alertService.error('Erreur', 'Le nom du secteur est obligatoire.');
      return;
    }

    if (!this.editingMode) {
      this.secteurService.createSecteur(this.currentSecteur as Secteur).subscribe({
        next: async (savedSecteur) => {
          await this.updateMedecinsForSecteur(savedSecteur);
          this.loadAllData();
          this.closeModal();
          this.alertService.success('Secteur créé avec succès.');
        },
        error: (err) => {
          console.error(err);
          this.alertService.error('Erreur', 'Impossible de créer le secteur.');
        }
      });
    } else {
      this.secteurService.updateSecteur(this.currentSecteur.id!, this.currentSecteur as Secteur).subscribe({
        next: async (savedSecteur) => {
          await this.updateMedecinsForSecteur(savedSecteur);
          this.loadAllData();
          this.closeModal();
          this.alertService.success('Secteur mis à jour.');
        },
        error: (err) => {
          console.error(err);
          this.alertService.error('Erreur', 'Impossible de mettre à jour le secteur.');
        }
      });
    }
  }

  async updateMedecinsForSecteur(secteur: Secteur) {
    const promises: Promise<any>[] = [];
    for (let med of this.medecins) {
      let currentSecteurs = med.secteurs || [];
      let currentlyInSecteur = currentSecteurs.some(s => s.id === secteur.id);
      let shouldBeInSecteur = this.selectedMedecinsForSecteur.includes(med.id!);

      if (shouldBeInSecteur && !currentlyInSecteur) {
        let newSecteurs = [...currentSecteurs, secteur];
        let updatedMed = { ...med, secteurs: newSecteurs };
        promises.push(new Promise(resolve => this.medecinService.updateMedecin(med.id!, updatedMed).subscribe({
          next: () => resolve(true),
          error: () => resolve(false)
        })));
      } else if (!shouldBeInSecteur && currentlyInSecteur) {
        let newSecteurs = currentSecteurs.filter(s => s.id !== secteur.id);
        let updatedMed = { ...med, secteurs: newSecteurs };
        promises.push(new Promise(resolve => this.medecinService.updateMedecin(med.id!, updatedMed).subscribe({
          next: () => resolve(true),
          error: () => resolve(false)
        })));
      }
    }
    if (promises.length > 0) {
      await Promise.all(promises);
    }
  }

  saveSecretaire(): void {
    if (!this.editingMode) {
      if (!this.currentSecretaire.email) {
        let usernameBase = this.currentSecretaire.nom ? this.currentSecretaire.nom.toLowerCase().replace(/ /g, '') : 'secretaire';
        this.currentSecretaire.email = `${usernameBase}@prohealth.com`;
      }
      this.secretaireService.createSecretaire(this.currentSecretaire as Secretaire).subscribe({
        next: () => {
          this.loadSecretaires();
          this.closeModal();
          this.alertService.success('Secrétaire créé et invité.');
        },
        error: (err) => this.alertService.error('Erreur: ' + (err.error?.message || 'Serveur indisponible'))
      });
    } else {
      this.secretaireService.updateSecretaire(this.currentSecretaire.id!, this.currentSecretaire as Secretaire).subscribe({
        next: () => {
          this.loadSecretaires();
          this.closeModal();
          this.alertService.success('Secrétaire mis à jour.');
        },
        error: (err) => {
          console.error(err);
          this.alertService.error('Erreur', 'Impossible de mettre à jour le secrétaire.');
        }
      });
    }
  }

  // --- Delete Methods ---

  async deleteMedecin(id?: number) {
    if (!id) return;
    const result = await this.alertService.confirm('Suppression', 'Supprimer ce médecin ?');
    if (result.isConfirmed) {
      this.medecinService.deleteMedecin(id).subscribe(() => {
        this.loadMedecins();
        this.alertService.success('Médecin supprimé.');
      });
    }
  }

  async deleteSecteur(id?: number) {
    if (!id) return;
    const result = await this.alertService.confirm('Suppression', 'Supprimer ce secteur ?');
    if (result.isConfirmed) {
      this.secteurService.deleteSecteur(id).subscribe(() => {
        this.loadSecteurs();
        this.alertService.success('Secteur supprimé.');
      });
    }
  }

  async deleteSecretaire(id?: number) {
    if (!id) return;
    const result = await this.alertService.confirm('Suppression', 'Supprimer ce secrétaire ?');
    if (result.isConfirmed) {
      this.secretaireService.deleteSecretaire(id).subscribe(() => {
        this.loadSecretaires();
        this.alertService.success('Secrétaire supprimé.');
      });
    }
  }

  resendMedecinInvitation(id?: number): void {
    if (!id) return;
    this.medecinService.resendInvitation(id).subscribe({
      next: () => this.alertService.success("Invitation renvoyée."),
      error: () => this.alertService.error("Échec de l'envoi.")
    });
  }

  resendSecretaireInvitation(id?: number): void {
    if (!id) return;
    this.secretaireService.resendInvitation(id).subscribe({
      next: () => this.alertService.success("Invitation renvoyée."),
      error: () => this.alertService.error("Échec de l'envoi.")
    });
  }

  // Helper for ngModel with objects
  compareSecteurs(s1: Secteur, s2: Secteur): boolean {
    return s1 && s2 ? s1.id === s2.id : s1 === s2;
  }

  getMedecinCountBySecteur(secteurId: number | undefined): number {
    if (!secteurId) return 0;
    return this.medecins.filter(m => m.secteurs && m.secteurs.some(s => s.id === secteurId)).length;
  }
}
