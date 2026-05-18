import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

const ETAT_META: Record<string, { badge: string; label: string }> = {
  OPERATIONNEL:    { badge: 'badge-success', label: '✅ Opérationnel' },
  EN_PANNE:        { badge: 'badge-danger',  label: '🔴 En panne' },
  EN_MAINTENANCE:  { badge: 'badge-warning', label: '🔧 En maintenance' },
  HORS_SERVICE:    { badge: 'badge-neutral', label: '⛔ Hors service' },
};

@Component({
  selector: 'app-equipements',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <!-- Header -->
    <div class="page-header">
      <div class="page-title">
        <div class="page-icon">🏭</div>
        <div>
          <h2>Équipements</h2>
          <p>{{ equipements.length }} équipement(s) enregistré(s)</p>
        </div>
      </div>
      <button class="btn btn-primary" id="btn-create-equipement" (click)="openModal()">
        ＋ Nouvel équipement
      </button>
    </div>

    <!-- Loading -->
    <div class="loading-state" *ngIf="loading">
      <div class="spinner"></div><span>Chargement...</span>
    </div>

    <!-- Table -->
    <div class="pro-table-wrap" *ngIf="!loading">
      <table class="pro-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Nom / Désignation</th>
            <th>État</th>
            <th>Date d'acquisition</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let eq of equipements; let i = index">
            <td style="color:var(--text-dim); font-size:12px">{{ i + 1 }}</td>
            <td class="td-main">{{ eq.nom }}</td>
            <td>
              <span class="badge" [ngClass]="getMeta(eq.etat).badge">
                {{ getMeta(eq.etat).label }}
              </span>
            </td>
            <td>{{ eq.dateAcquisition ? (eq.dateAcquisition | date:'dd/MM/yyyy') : '—' }}</td>
            <td>
              <div class="action-group">
                <button class="btn-icon" title="Modifier" (click)="openModal(eq)">✏️</button>
                <button class="btn-icon danger" title="Supprimer" (click)="supprimer(eq.id)">🗑️</button>
              </div>
            </td>
          </tr>
          <tr class="empty-row" *ngIf="equipements.length === 0">
            <td colspan="5">🏭 Aucun équipement enregistré</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div class="modal-overlay" *ngIf="showModal" (click)="closeModal()">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <div class="modal-title">
            <div class="modal-icon">🏭</div>
            <span *ngIf="!editMode">Nouvel équipement</span>
            <span *ngIf="editMode">Modifier l&apos;équipement</span>
          </div>
          <button class="modal-close" (click)="closeModal()">✕</button>
        </div>

        <div class="modal-body">
          <div class="form-grid">
            <div class="form-group full">
              <label class="form-label">Nom / Désignation *</label>
              <input class="form-control" type="text" [(ngModel)]="form.nom"
                     placeholder="Ex : Compresseur A1 — Hall 3" id="eq-nom"/>
            </div>
            <div class="form-group">
              <label class="form-label">État *</label>
              <select class="form-control" [(ngModel)]="form.etat" id="eq-etat">
                <option value="OPERATIONNEL">Opérationnel</option>
                <option value="EN_PANNE">En panne</option>
                <option value="EN_MAINTENANCE">En maintenance</option>
                <option value="HORS_SERVICE">Hors service</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Date d'acquisition</label>
              <input class="form-control" type="date" [(ngModel)]="form.dateAcquisition" id="eq-date"/>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-ghost" (click)="closeModal()">Annuler</button>
          <button class="btn btn-primary" (click)="sauvegarder()" id="eq-submit">
            {{ editMode ? '💾 Modifier' : '＋ Créer' }}
          </button>
        </div>
      </div>
    </div>
  `
})
export class EquipementsComponent implements OnInit {
  equipements: any[] = [];
  loading = true;
  showModal = false;
  editMode = false;
  editId: number | null = null;
  form: any = { nom: '', etat: 'OPERATIONNEL', dateAcquisition: '' };

  constructor(private api: ApiService) {}

  ngOnInit() { this.charger(); }

  charger() {
    this.loading = true;
    this.api.getEquipements().subscribe({
      next: (data) => { this.equipements = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  getMeta(etat: string) { return ETAT_META[etat] || { badge: 'badge-neutral', label: etat }; }

  openModal(eq?: any) {
    this.showModal = true;
    if (eq) {
      this.editMode = true; this.editId = eq.id;
      this.form = { nom: eq.nom, etat: eq.etat, dateAcquisition: eq.dateAcquisition || '' };
    } else {
      this.editMode = false; this.editId = null;
      this.form = { nom: '', etat: 'OPERATIONNEL', dateAcquisition: '' };
    }
  }

  closeModal() { this.showModal = false; }

  sauvegarder() {
    if (!this.form.nom?.trim()) return alert('Le nom est obligatoire');
    const payload = { ...this.form };
    if (!payload.dateAcquisition) delete payload.dateAcquisition;
    const obs = this.editMode && this.editId
      ? this.api.updateEquipement(this.editId, payload)
      : this.api.createEquipement(payload);
    obs.subscribe({ next: () => { this.charger(); this.closeModal(); }, error: (e) => alert('Erreur : ' + e.message) });
  }

  supprimer(id: number) {
    if (confirm('Supprimer cet équipement ?'))
      this.api.deleteEquipement(id).subscribe(() => this.charger());
  }
}
