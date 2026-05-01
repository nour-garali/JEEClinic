import { Component, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FactureService } from '../../../services/facture.service';
import { Facture } from '../../../models/facture.model';
import { AlertService } from '../../../services/alert.service';

@Component({
  selector: 'app-facture-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './facture-list.html',
  styleUrl: './facture-list.css'
})
export class FactureListComponent implements OnInit {
  @Input() patientId: number | null = null;
  @Input() medecinId: number | null = null;

  factures: Facture[] = [];
  loading: boolean = false;
  downloadingId: number | null = null;
  expandedRowId: number | null = null;

  constructor(
    private factureService: FactureService,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadFactures();
  }

  loadFactures() {
    this.loading = true;
    if (this.patientId) {
      this.factureService.getFacturesByPatient(this.patientId).subscribe({
        next: (data) => {
          this.factures = data;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.alertService.error('Erreur', 'Impossible de charger vos factures.');
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
    } else if (this.medecinId) {
      this.factureService.getFacturesByMedecin(this.medecinId).subscribe({
        next: (data) => {
          this.factures = data;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.alertService.error('Erreur', 'Impossible de charger les factures.');
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  toggleRow(id: number) {
    this.expandedRowId = this.expandedRowId === id ? null : id;
  }

  downloadPdf(facture: Facture) {
    this.downloadingId = facture.id;
    this.factureService.downloadFacturePdf(facture.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `facture-${facture.numeroFacture}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        this.downloadingId = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.alertService.error('Erreur', 'Échec du téléchargement du PDF.');
        this.downloadingId = null;
        this.cdr.detectChanges();
      }
    });
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  }
}
