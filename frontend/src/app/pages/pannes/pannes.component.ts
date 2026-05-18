import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

const CAT_META: Record<string, { badge: string; icon: string }> = {
  MECANIQUE:    { badge: 'badge-warning', icon: '⚙️' },
  ELECTRIQUE:   { badge: 'badge-danger',  icon: '⚡' },
  ELECTRONIQUE: { badge: 'badge-info',    icon: '🔌' },
  HYDRAULIQUE:  { badge: 'badge-purple',  icon: '💧' },
  PNEUMATIQUE:  { badge: 'badge-neutral', icon: '🌀' },
  LOGICIEL:     { badge: 'badge-info',    icon: '💻' },
  AUTRE:        { badge: 'badge-neutral', icon: '❓' },
};

@Component({
  selector: 'app-pannes',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  template: `
    <div class="page-header">
      <div class="page-title">
        <div class="page-icon">⚠️</div>
        <div>
          <h2>Pannes signalées</h2>
          <p>{{ pannes.length }} panne(s) enregistrée(s)</p>
        </div>
      </div>
      <button class="btn btn-primary" id="btn-signaler-panne" (click)="openModal()">
        ＋ Signaler une panne
      </button>
    </div>

    <div class="loading-state" *ngIf="loading">
      <div class="spinner"></div><span>Chargement...</span>
    </div>

    <div class="pro-table-wrap" *ngIf="!loading">
      <table class="pro-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Description</th>
            <th>Catégorie</th>
            <th>Équipement</th>
            <th>Date signalement</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let p of pannes; let i = index">
            <td style="color:var(--text-dim);font-size:12px">{{ i+1 }}</td>
            <td class="td-main" style="max-width:280px">{{ p.description }}</td>
            <td>
              <span class="badge" [ngClass]="getCatMeta(p.categorie).badge">
                {{ getCatMeta(p.categorie).icon }} {{ p.categorie }}
              </span>
            </td>
            <td>
              <span class="equip-chip" *ngIf="getEquipementNom(p.equipementId); else noEquip">
                🏭 {{ getEquipementNom(p.equipementId) }}
              </span>
              <ng-template #noEquip><span style="color:var(--text-dim)">—</span></ng-template>
            </td>
            <td>{{ p.dateSignalement | date:'dd/MM/yyyy' }}</td>
            <td>
              <div class="action-group">
                <button class="btn-icon" (click)="openModal(p)" title="Modifier">✏️</button>
                <button class="btn-icon danger" (click)="supprimer(p.id)" title="Supprimer">🗑️</button>
              </div>
            </td>
          </tr>
          <tr class="empty-row" *ngIf="pannes.length === 0">
            <td colspan="6">⚠️ Aucune panne signalée</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div class="modal-overlay" *ngIf="showModal" (click)="closeModal()">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <div class="modal-title">
            <div class="modal-icon">⚠️</div>
            {{ editMode ? 'Modifier la panne' : 'Signaler une panne' }}
          </div>
          <button class="modal-close" (click)="closeModal()">✕</button>
        </div>

        <div class="modal-body">
          <div class="form-grid">
            <div class="form-group full">
              <label class="form-label">Description *</label>
              <textarea class="form-control" [(ngModel)]="form.description" rows="3"
                        id="panne-desc"
                        placeholder="Décrivez la panne observée..."></textarea>
            </div>
            <div class="form-group">
              <label class="form-label">Catégorie *</label>
              <select class="form-control" [(ngModel)]="form.categorie" id="panne-cat">
                <option *ngFor="let c of categories" [value]="c">{{ c }}</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">
                Équipement OPERATIONNEL *
                <span class="badge badge-success" style="margin-left:8px;font-size:10px">
                  {{ equipementsOperationnels.length }} disponible(s)
                </span>
              </label>
              <select class="form-control" [(ngModel)]="form.equipementId" id="panne-equip">
                <option value="">— Sélectionner un équipement opérationnel —</option>
                <option *ngFor="let eq of equipementsOperationnels" [value]="eq.id">
                  ✅ {{ eq.nom }}
                </option>
              </select>
              <div *ngIf="equipementsOperationnels.length === 0" class="form-hint warn">
                ⚠️ Aucun équipement opérationnel disponible.
              </div>
            </div>
            <div class="form-group full">
              <label class="form-label">Date de signalement</label>
              <input class="form-control" type="date" [(ngModel)]="form.dateSignalement" id="panne-date"/>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-ghost" (click)="closeModal()">Annuler</button>
          <button class="btn btn-primary" (click)="sauvegarder()" id="panne-submit">
            {{ editMode ? '💾 Modifier' : '＋ Signaler' }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .equip-chip {
      display: inline-flex; align-items: center; gap: 5px;
      background: rgba(99,102,241,0.1); border: 1px solid rgba(99,102,241,0.2);
      border-radius: 6px; padding: 3px 8px; font-size: 12px; color: #a5b4fc;
    }
    .form-hint.warn { color: #f59e0b; font-size: 12px; margin-top: 6px; }
  `]
})
export class PannesComponent implements OnInit {
  pannes:    any[] = [];
  equipements: any[] = [];             // tous les équipements (pour affichage tableau)
  equipementsOperationnels: any[] = []; // filtrés OPERATIONNEL (pour le modal)
  loading   = true;
  showModal = false;
  editMode  = false;
  editId: number | null = null;
  categories = ['MECANIQUE','ELECTRIQUE','ELECTRONIQUE','HYDRAULIQUE','PNEUMATIQUE','LOGICIEL','AUTRE'];
  form: any = { description: '', categorie: 'MECANIQUE', equipementId: '', dateSignalement: '' };

  constructor(private api: ApiService) {}

  ngOnInit() {
    // Charger tous les équipements (pour résoudre les noms dans le tableau)
    this.api.getEquipements().subscribe(data => this.equipements = data);
    // Charger les équipements opérationnels (pour le modal)
    this.api.getEquipementsOperationnels().subscribe(data => this.equipementsOperationnels = data);
    this.charger();
  }

  charger() {
    this.loading = true;
    this.api.getPannes().subscribe({
      next: (d) => { this.pannes = d; this.loading = false; },
      error: ()  => { this.loading = false; }
    });
  }

  getCatMeta(cat: string) { return CAT_META[cat] || { badge: 'badge-neutral', icon: '❓' }; }

  getEquipementNom(equipementId: number | null): string {
    if (!equipementId) return '';
    const eq = this.equipements.find(e => e.id === +equipementId);
    return eq ? eq.nom : `Équipement #${equipementId}`;
  }

  openModal(p?: any) {
    // Rafraîchir la liste à chaque ouverture
    this.api.getEquipementsOperationnels().subscribe(data => this.equipementsOperationnels = data);
    this.showModal = true;
    if (p) {
      this.editMode = true; this.editId = p.id;
      this.form = {
        description:     p.description,
        categorie:       p.categorie,
        equipementId:    p.equipementId || '',
        dateSignalement: p.dateSignalement || ''
      };
    } else {
      this.editMode = false; this.editId = null;
      this.form = {
        description:     '',
        categorie:       'MECANIQUE',
        equipementId:    this.equipementsOperationnels[0]?.id || '',
        dateSignalement: new Date().toISOString().split('T')[0]
      };
    }
  }

  closeModal() { this.showModal = false; }

  sauvegarder() {
    if (!this.form.description?.trim()) return alert('La description est obligatoire');
    if (!this.form.equipementId)        return alert('Veuillez sélectionner un équipement');
    const payload = {
      description:     this.form.description,
      categorie:       this.form.categorie,
      dateSignalement: this.form.dateSignalement,
      equipementId:    +this.form.equipementId
    };
    const obs = this.editMode && this.editId
      ? this.api.updatePanne(this.editId, payload)
      : this.api.createPanne(payload);
    obs.subscribe({
      next: () => { this.charger(); this.closeModal(); },
      error: (e) => alert('Erreur : ' + e.message)
    });
  }

  supprimer(id: number) {
    if (confirm('Supprimer cette panne ?'))
      this.api.deletePanne(id).subscribe(() => this.charger());
  }
}
