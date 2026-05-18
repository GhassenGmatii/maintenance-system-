import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

const STATUT_META: Record<string, { badge: string; label: string; icon: string }> = {
  PLANIFIEE: { badge: 'badge-info',    label: 'Planifiée',    icon: '📅' },
  EN_COURS:  { badge: 'badge-warning', label: 'En cours',     icon: '⚙️' },
  TERMINEE:  { badge: 'badge-success', label: 'Terminée',     icon: '✅' },
  ANNULEE:   { badge: 'badge-danger',  label: 'Annulée',      icon: '❌' },
};

/** Mapping catégorie de panne → mots-clés dans technicien.competences */
const CAT_KEYWORDS: Record<string, string[]> = {
  HYDRAULIQUE:  ['hydraulique'],
  ELECTRIQUE:   ['électricité', 'electricit', 'electrique'],
  ELECTRONIQUE: ['électronique', 'electronique'],
  MECANIQUE:    ['mécanique', 'mecanique'],
  PNEUMATIQUE:  ['pneumatique'],
  LOGICIEL:     ['logiciel', 'informatique'],
  AUTRE:        [],
};

@Component({
  selector: 'app-interventions',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  template: `
    <div class="page-header">
      <div class="page-title">
        <div class="page-icon">🔧</div>
        <div>
          <h2>Interventions</h2>
          <p>{{ interventions.length }} bon(s) de travaux</p>
        </div>
      </div>
      <button class="btn btn-primary" id="btn-planifier-intervention" (click)="openModal()">
        ＋ Planifier une intervention
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
            <th>Équipement</th>
            <th>Technicien</th>
            <th>Statut</th>
            <th>Période</th>
            <th>Coût (DT)</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let itv of interventions; let i = index">
            <td style="color:var(--text-dim);font-size:12px">{{ i+1 }}</td>
            <td class="td-main">
              <div class="cell-icon-text">
                <span>🏭</span> {{ getEquipementNom(itv.equipementId) || '—' }}
              </div>
            </td>
            <td>
              <div class="tech-cell" *ngIf="getTechnicienNom(itv.technicienId); else noTech">
                <div class="tech-avatar">{{ getInitials(getTechnicienNom(itv.technicienId)) }}</div>
                <span>{{ getTechnicienNom(itv.technicienId) }}</span>
              </div>
              <ng-template #noTech><span style="color:var(--text-dim)">—</span></ng-template>
            </td>
            <td>
              <span class="badge" [ngClass]="getStatutMeta(itv.statut).badge">
                {{ getStatutMeta(itv.statut).icon }} {{ getStatutMeta(itv.statut).label }}
              </span>
            </td>
            <td>
              <div class="periode-cell">
                <span class="date-tag">📅 {{ itv.date | date:'dd/MM/yy' }}</span>
                <span style="color:var(--text-dim);font-size:11px">→</span>
                <span class="date-tag fin" *ngIf="itv.dateFin">🏁 {{ itv.dateFin | date:'dd/MM/yy' }}</span>
                <span style="color:var(--text-dim);font-size:11px" *ngIf="!itv.dateFin">—</span>
              </div>
            </td>
            <td>
              <span *ngIf="itv.cout != null" class="cout-val">
                {{ itv.cout | number:'1.0-2' }}
                <span style="color:var(--text-dim);font-size:11px">DT</span>
              </span>
              <span *ngIf="itv.cout == null" style="color:var(--text-dim)">—</span>
            </td>
            <td>
              <div class="action-group">
                <!-- Valider manuellement avant la fin de période -->
                <button *ngIf="itv.statut === 'EN_COURS' || itv.statut === 'PLANIFIEE'"
                        class="btn-icon valider" (click)="validerTerminaison(itv)"
                        title="Marquer comme terminée">✔</button>
                <button class="btn-icon" (click)="openModal(itv)" title="Modifier">✏️</button>
                <button class="btn-icon danger" (click)="supprimer(itv.id)" title="Supprimer">🗑️</button>
              </div>
            </td>
          </tr>
          <tr class="empty-row" *ngIf="interventions.length === 0">
            <td colspan="7">🔧 Aucune intervention planifiée</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ══════════ Modal Planifier / Modifier ══════════ -->
    <div class="modal-overlay" *ngIf="showModal" (click)="closeModal()">
      <div class="modal" style="width:560px" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <div class="modal-title">
            <div class="modal-icon">🔧</div>
            <span *ngIf="!editMode">Planifier une intervention</span>
            <span *ngIf="editMode">Modifier une intervention</span>
          </div>
          <button class="modal-close" (click)="closeModal()">✕</button>
        </div>

        <div class="modal-body">
          <div class="form-grid">

            <!-- Équipement EN_PANNE uniquement -->
            <div class="form-group full">
              <label class="form-label">
                Équipement EN PANNE *
                <span class="badge badge-danger" style="margin-left:8px;font-size:10px">
                  {{ equipementsEnPanne.length }} disponible(s)
                </span>
              </label>
              <select class="form-control" [(ngModel)]="form.equipementId"
                      id="itv-equip" (ngModelChange)="onEquipementChange($event)">
                <option value="">— Sélectionner un équipement en panne —</option>
                <option *ngFor="let eq of equipementsEnPanne" [value]="eq.id">
                  🔴 {{ eq.nom }}
                </option>
              </select>
              <div *ngIf="equipementsEnPanne.length === 0" class="form-hint warn">
                ⚠️ Aucun équipement n'est actuellement en panne.
              </div>
            </div>

            <!-- Techniciens filtrés par compétence -->
            <div class="form-group full">
              <label class="form-label">
                Technicien compétent *
                <span *ngIf="categoriesPanne.length > 0" class="competence-tag">
                  🔍 Filtré par :
                  <span *ngFor="let c of categoriesPanne" class="cat-badge">{{ c }}</span>
                </span>
              </label>
              <select class="form-control" [(ngModel)]="form.technicienId" id="itv-tech">
                <option value="">— Sélectionner —</option>
                <option *ngFor="let t of techniciensFiltres" [value]="t.id">
                  {{ t.nom }} {{ t.disponibilite ? '✅' : '🔴' }} — {{ t.competences }}
                </option>
              </select>
              <div *ngIf="form.equipementId && techniciensFiltres.length === 0" class="form-hint warn">
                ⚠️ Aucun technicien avec les compétences requises.
              </div>
              <div *ngIf="form.equipementId && techniciensFiltres.length > 0 && categoriesPanne.length > 0"
                   class="form-hint ok">
                ✅ {{ techniciensFiltres.length }} technicien(s) qualifié(s).
              </div>
            </div>

            <!-- Date de début -->
            <div class="form-group">
              <label class="form-label">Date de début *</label>
              <input class="form-control" type="date" [(ngModel)]="form.date"
                     id="itv-date" (ngModelChange)="onDateOuPeriodeChange()"/>
            </div>

            <!-- Période (durée) -->
            <div class="form-group">
              <label class="form-label">Durée de la période *</label>
              <div class="periode-input-group">
                <input class="form-control" type="number" [(ngModel)]="form.dureValeur"
                       id="itv-duree" min="1" max="365" placeholder="Ex: 7"
                       (ngModelChange)="onDateOuPeriodeChange()"/>
                <select class="form-control" [(ngModel)]="form.dureeUnite"
                        id="itv-unite" (ngModelChange)="onDateOuPeriodeChange()">
                  <option value="jours">Jours</option>
                  <option value="semaines">Semaines</option>
                  <option value="mois">Mois</option>
                </select>
              </div>
              <div class="form-hint ok" *ngIf="form.dateFin">
                📅 Fin prévue : <strong>{{ form.dateFin | date:'dd/MM/yyyy' }}</strong>
              </div>
            </div>

            <!-- Coût -->
            <div class="form-group full">
              <label class="form-label">Coût estimé (DT)</label>
              <input class="form-control" type="number" [(ngModel)]="form.cout"
                     id="itv-cout" placeholder="Ex : 250.00" min="0" step="0.01"/>
            </div>

            <!-- Info -->
            <div class="form-group full">
              <div class="info-banner banner-maintenance">
                🔧 À la planification, l'équipement passera automatiquement à <strong>En Maintenance</strong>.
                Cliquez sur ✔ pour valider la fin et remettre l'équipement à <strong>Opérationnel</strong>.
              </div>
            </div>

          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-ghost" (click)="closeModal()">Annuler</button>
          <button class="btn btn-primary" (click)="sauvegarder()" id="itv-submit">
            {{ editMode ? '💾 Enregistrer' : '📅 Planifier' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ══════════ Modal validation terminaison ══════════ -->
    <div class="modal-overlay" *ngIf="showValidation" (click)="showValidation=false">
      <div class="modal" style="width:440px;text-align:center" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <div class="modal-title">
            <div class="modal-icon">✅</div>
            Valider la fin d'intervention
          </div>
          <button class="modal-close" (click)="showValidation=false">✕</button>
        </div>
        <div class="modal-body">
          <p style="color:var(--text-secondary);margin-bottom:16px">
            Confirmez-vous que l'intervention sur<br>
            <strong>{{ getEquipementNom(interventionAValider?.equipementId) }}</strong>
            est bien <strong>terminée</strong> ?
          </p>
          <p style="font-size:13px;color:var(--text-dim)">
            ✅ L'équipement repassera automatiquement à l'état <strong>Opérationnel</strong>.
          </p>
        </div>
        <div class="modal-footer" style="justify-content:center;gap:16px">
          <button class="btn btn-ghost" (click)="showValidation=false">Annuler</button>
          <button class="btn btn-success" (click)="confirmerTerminaison()" id="btn-confirmer-terminaison">
            ✔ Confirmer la fin
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .cell-icon-text { display: flex; align-items: center; gap: 7px; }
    .tech-cell      { display: flex; align-items: center; gap: 8px; }
    .tech-avatar {
      width: 28px; height: 28px;
      background: linear-gradient(135deg, #6366f1, #8b5cf6);
      border-radius: 50%; display: flex; align-items: center;
      justify-content: center; font-size: 10px; font-weight: 700;
      color: white; flex-shrink: 0;
    }
    .cout-val    { font-weight: 700; color: #fbbf24; font-size: 14px; }
    .action-group { display: flex; gap: 6px; }
    .periode-cell { display: flex; align-items: center; gap: 5px; flex-wrap: wrap; }
    .date-tag { font-size: 11px; color: #a5b4fc;
                background: rgba(99,102,241,.1); border: 1px solid rgba(99,102,241,.2);
                border-radius: 4px; padding: 2px 6px; }
    .date-tag.fin { color: #34d399; background: rgba(52,211,153,.1); border-color: rgba(52,211,153,.2); }
    .btn-icon.valider { background: rgba(52,211,153,.15); color: #34d399;
                        border: 1px solid rgba(52,211,153,.3); font-weight: 700;
                        border-radius: 6px; padding: 4px 8px; cursor: pointer; }
    .btn-icon.valider:hover { background: rgba(52,211,153,.3); }
    .btn-success { background: linear-gradient(135deg,#10b981,#059669);
                   color: #fff; border: none; padding: 10px 24px;
                   border-radius: 8px; cursor: pointer; font-weight: 600; }
    .btn-success:hover { opacity: .9; }
    .form-hint.warn { color: #f59e0b; font-size: 12px; margin-top: 6px; }
    .form-hint.ok   { color: #34d399; font-size: 12px; margin-top: 6px; }
    .info-banner { padding: 12px 14px; border-radius: 8px; font-size: 13px; }
    .banner-maintenance { background: rgba(251,191,36,.08); border: 1px solid rgba(251,191,36,.25);
                          color: #fbbf24; }
    .competence-tag { font-size: 11px; color: var(--text-dim); margin-left: 8px; font-weight: 400; }
    .cat-badge { display: inline-block; background: rgba(99,102,241,.15);
                 border: 1px solid rgba(99,102,241,.3); color: #a5b4fc;
                 border-radius: 4px; padding: 1px 6px; font-size: 10px;
                 margin-left: 4px; font-weight: 600; }
    .periode-input-group { display: flex; gap: 8px; }
    .periode-input-group input  { flex: 1; }
    .periode-input-group select { flex: 1.2; }
  `]
})
export class InterventionsComponent implements OnInit {
  interventions:      any[] = [];
  equipementsEnPanne: any[] = [];
  tousEquipements:    any[] = [];
  tousTechniciens:    any[] = [];
  toutePannes:        any[] = [];
  techniciensFiltres: any[] = [];
  categoriesPanne:    string[] = [];

  loading        = true;
  showModal      = false;
  showValidation = false;
  editMode       = false;
  editId: number | null = null;
  interventionAValider: any = null;

  form: any = {
    equipementId: '', technicienId: '',
    date: '', dateFin: '',
    dureValeur: 7, dureeUnite: 'jours',
    cout: null
  };

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getEquipements().subscribe(d => this.tousEquipements = d);
    this.api.getEquipementsEnPanne().subscribe(d => this.equipementsEnPanne = d);
    this.api.getPannes().subscribe(d => this.toutePannes = d);
    this.api.getTechniciens().subscribe(d => {
      this.tousTechniciens = d;
      // Afficher seulement les techniciens DISPONIBLES par défaut
      this.techniciensFiltres = d.filter((t: any) => t.disponibilite === true);
      this.charger();
    });
  }

  charger() {
    this.loading = true;
    this.api.getInterventions().subscribe({
      next: (d) => { this.interventions = d; this.loading = false; },
      error: ()  => { this.loading = false; }
    });
  }

  // ─── Filtrage techniciens ─────────────────────────────────────────────
  onEquipementChange(equipementId: any) {
    this.form.technicienId = '';

    // Toujours partir des techniciens DISPONIBLES uniquement
    const disponibles = this.tousTechniciens.filter((t: any) => t.disponibilite === true);

    if (!equipementId) {
      this.techniciensFiltres = disponibles;
      this.categoriesPanne    = [];
      return;
    }

    // Trouver les catégories de pannes de l'équipement sélectionné
    const id = +equipementId;
    const pannesEq = this.toutePannes.filter(p => p.equipementId === id);
    this.categoriesPanne = [...new Set(pannesEq.map((p: any) => p.categorie))];

    if (this.categoriesPanne.length === 0) {
      this.techniciensFiltres = disponibles;
      return;
    }

    // Filtrer par compétences correspondant à la catégorie de panne
    const keywords: string[] = [];
    for (const cat of this.categoriesPanne) {
      const kws = CAT_KEYWORDS[cat] || [];
      if (kws.length === 0) { this.techniciensFiltres = disponibles; return; }
      keywords.push(...kws);
    }

    const filtresParCompetence = disponibles.filter(t =>
      keywords.some(kw => (t.competences || '').toLowerCase().includes(kw.toLowerCase()))
    );

    // Si aucun technicien qualifié disponible, afficher tous les disponibles
    this.techniciensFiltres = filtresParCompetence.length > 0 ? filtresParCompetence : disponibles;
  }

  // ─── Helpers ─────────────────────────────────────────────────────────
  getStatutMeta(s: string) {
    return STATUT_META[s] || { badge: 'badge-neutral', label: s, icon: '❓' };
  }
  getEquipementNom(id: number | null): string {
    if (!id) return '';
    const eq = this.tousEquipements.find(e => e.id === +id);
    return eq ? eq.nom : `#${id}`;
  }
  getTechnicienNom(id: number | null): string {
    if (!id) return '';
    const t = this.tousTechniciens.find(t => t.id === +id);
    return t ? t.nom : `#${id}`;
  }
  getInitials(nom: string): string {
    if (!nom) return '?';
    return nom.split(' ').map((n: string) => n[0]).join('').toUpperCase().substring(0, 2);
  }

  /** Calcule automatiquement dateFin depuis date + dureValeur + dureeUnite */
  onDateOuPeriodeChange() {
    if (!this.form.date || !this.form.dureValeur) return;
    const debut = new Date(this.form.date);
    const val   = +this.form.dureValeur;
    const fin   = new Date(debut);
    switch (this.form.dureeUnite) {
      case 'jours':    fin.setDate(fin.getDate() + val); break;
      case 'semaines': fin.setDate(fin.getDate() + val * 7); break;
      case 'mois':     fin.setMonth(fin.getMonth() + val); break;
    }
    this.form.dateFin = fin.toISOString().split('T')[0];
  }

  // ─── Modal ───────────────────────────────────────────────────────────
  openModal(itv?: any) {
    this.api.getEquipementsEnPanne().subscribe(d => this.equipementsEnPanne = d);
    this.api.getPannes().subscribe(d => this.toutePannes = d);
    this.showModal = true;

    if (itv) {
      this.editMode = true; this.editId = itv.id;
      this.form = {
        equipementId: itv.equipementId || '',
        technicienId: itv.technicienId || '',
        date:         itv.date    || '',
        dateFin:      itv.dateFin || '',
        dureValeur:   7,
        dureeUnite:   'jours',
        cout:         itv.cout
      };
      if (itv.equipementId) this.onEquipementChange(itv.equipementId);
    } else {
      this.editMode = false; this.editId = null;
      this.categoriesPanne    = [];
      this.techniciensFiltres = this.tousTechniciens;
      this.form = {
        equipementId: '',
        technicienId: '',
        date:       new Date().toISOString().split('T')[0],
        dateFin:    '',
        dureValeur: 7,
        dureeUnite: 'jours',
        cout:       null
      };
      // Calculer la dateFin par défaut (7 jours)
      this.onDateOuPeriodeChange();
    }
  }

  closeModal() { this.showModal = false; }

  sauvegarder() {
    if (!this.form.equipementId) { alert('Veuillez sélectionner un équipement'); return; }
    if (!this.form.technicienId) { alert('Veuillez sélectionner un technicien'); return; }
    if (!this.form.date)         { alert('La date de début est obligatoire'); return; }
    if (!this.form.dureValeur || this.form.dureValeur < 1) {
      alert('La durée doit être au moins 1'); return;
    }
    // S'assurer que dateFin est calculé
    this.onDateOuPeriodeChange();
    if (!this.form.dateFin) { alert('Erreur de calcul de la date de fin'); return; }

    // Statut toujours PLANIFIEE à la création — géré automatiquement ensuite
    const payload = {
      statut:       'PLANIFIEE',
      date:         this.form.date,
      dateFin:      this.form.dateFin,
      cout:         this.form.cout,
      equipementId: +this.form.equipementId,
      technicienId: +this.form.technicienId
    };

    const obs = this.editMode && this.editId
      ? this.api.updateIntervention(this.editId, payload)
      : this.api.createIntervention(payload);

    obs.subscribe({
      next: () => {
        this.charger();
        this.api.getEquipements().subscribe(d => this.tousEquipements = d);
        this.api.getEquipementsEnPanne().subscribe(d => this.equipementsEnPanne = d);
        this.closeModal();
      },
      error: (e) => alert('Erreur : ' + e.message)
    });
  }

  // ─── Validation manuelle (avant fin de période) ───────────────────────
  validerTerminaison(itv: any) {
    this.interventionAValider = itv;
    this.showValidation = true;
  }

  confirmerTerminaison() {
    if (!this.interventionAValider) return;
    const payload = { ...this.interventionAValider, statut: 'TERMINEE' };
    this.api.updateIntervention(this.interventionAValider.id, payload).subscribe({
      next: () => {
        this.showValidation       = false;
        this.interventionAValider = null;
        this.charger();
        this.api.getEquipements().subscribe(d => this.tousEquipements = d);
        this.api.getEquipementsEnPanne().subscribe(d => this.equipementsEnPanne = d);
      },
      error: (e) => alert('Erreur : ' + e.message)
    });
  }

  supprimer(id: number) {
    if (confirm('Supprimer cette intervention ?'))
      this.api.deleteIntervention(id).subscribe(() => this.charger());
  }
}
