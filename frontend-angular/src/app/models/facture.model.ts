export interface Facture {
  id: number;
  numeroFacture: string;
  patientNom: string;
  medecinNom: string;
  medecinSpecialite: string;
  dateConsultation: string;
  diagnostic: string;
  observations: string;
  ordonnance: string;
  prix: number;
  statut: 'EN_ATTENTE' | 'PAYE';
  dateCreation: string;
  dateModification?: string;
  datePaiement?: string;
  payePar?: string;
  creePar: string;
}
