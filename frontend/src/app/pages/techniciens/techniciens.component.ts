import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

/** Liste des compétences disponibles avec icône et catégorie */
const COMPETENCES_LISTE = [
  { label: 'Électricité',            icon: '⚡', cat: 'ELECTRIQUE'   },
  { label: 'Automatisme / PLC',      icon: '🖥️', cat: 'ELECTRIQUE'   },
  { label: 'Hydraulique',            icon: '💧', cat: 'HYDRAULIQUE'  },
  { label: 'Pneumatique',            icon: '🌬️', cat: 'PNEUMATIQUE'  },
  { label: 'Mécanique générale',     icon: '⚙️', cat: 'MECANIQUE'    },
  { label: 'Soudure',                icon: '🔥', cat: 'MECANIQUE'    },
  { label: 'Électronique',           icon: '📡', cat: 'ELECTRONIQUE' },
  { label: 'Informatique industrielle', icon: '💻', cat: 'LOGICIEL'  },
  { label: 'Chaudronnerie',          icon: '🛠️', cat: 'MECANIQUE'    },
  { label: 'Froid & Climatisation',  icon: '❄️', cat: 'AUTRE'        },
];

@Component({
  selector: 'app-techniciens',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-header">
      <div class="page-title">
        <div class="page-icon">👷</div>
        <div>
          <h2>Techniciens</h2>
          <p>{{ techniciens.length }} technicien(s) enregistré(s)</p>
        </div>
      </div>
      <button class="btn btn-primary" id="btn-add-tech" (click)="openModal()">
        ＋ Ajouter un technicien
      </button>
    </div>

    <div class="loading-state" *ngIf="loading">
      <div class="spinner"></div><span>Chargement...</span>
    </div>

    <!-- Cards Grid -->
    <div class="tech-grid" *ngIf="!loading && techniciens.length > 0">
      <div class="tech-card" *ngFor="let t of techniciens">
        <div class="tech-card-header">
          <div class="tech-big-avatar" [class.dispo]="t.disponibilite" [class.occupe]="!t.disponibilite">
            {{ getInitials(t.nom) }}
          </div>
          <div class="tech-status-dot" [class.green]="t.disponibilite" [class.red]="!t.disponibilite"></div>
        </div>
        <div class="tech-card-body">
          <div class="tech-name">{{ t.nom }}</div>
          <div class="tech-dispo-badge">
            <span class="badge" [ngClass]="t.disponibilite ? 'badge-success' : 'badge-danger'">
              {{ t.disponibilite ? '✅ Disponible' : '🔴 Occupé' }}
            </span>
          </div>
          <!-- Compétences en badges colorés -->
          <div class="tech-skills" *ngIf="t.competences">
            <div class="skills-title">Compétences</div>
            <div class="skills-badges">
              <span class="skill-badge" *ngFor="let sk of parseCompetences(t.competences)">
                {{ getCompetenceIcon(sk) }} {{ sk }}
              </span>
            </div>
          </div>
        </div>
        <div class="tech-card-footer">
          <button class="btn btn-ghost btn-sm" (click)="openModal(t)">✏️ Modifier</button>
          <button class="btn btn-danger btn-sm" (click)="supprimer(t.id)">🗑️</button>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div class="empty-card" *ngIf="!loading && techniciens.length === 0">
      <div class="empty-icon">👷</div>
      <h3>Aucun technicien enregistré</h3>
      <p>Commencez par ajouter votre premier technicien</p>
      <button class="btn btn-primary" (click)="openModal()">＋ Ajouter</button>
    </div>

    <!-- ══════════ Modal Ajouter / Modifier ══════════ -->
    <div class="modal-overlay" *ngIf="showModal" (click)="closeModal()">
      <div class="modal" style="width:520px" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <div class="modal-title">
            <div class="modal-icon">👷</div>
            {{ editMode ? 'Modifier le technicien' : 'Nouveau technicien' }}
          </div>
          <button class="modal-close" (click)="closeModal()">✕</button>
        </div>

        <div class="modal-body">
          <div class="form-grid">

            <!-- Nom -->
            <div class="form-group full">
              <label class="form-label">Nom complet *</label>
              <input class="form-control" type="text" [(ngModel)]="form.nom"
                     id="tech-nom" placeholder="Ex : Ahmed Ben Ali"/>
            </div>

            <!-- Compétences — cases à cocher -->
            <div class="form-group full">
              <label class="form-label">
                Compétences
                <span class="badge badge-info" style="margin-left:8px;font-size:10px">
                  {{ selectedCount() }} sélectionnée(s)
                </span>
              </label>
              <div class="competences-grid">
                <label class="comp-checkbox" *ngFor="let c of competencesList"
                       [class.checked]="isChecked(c.label)">
                  <input type="checkbox" [checked]="isChecked(c.label)"
                         (change)="toggleCompetence(c.label)"/>
                  <span class="comp-icon">{{ c.icon }}</span>
                  <span class="comp-label">{{ c.label }}</span>
                </label>
              </div>
              <div class="form-hint ok" *ngIf="selectedCount() > 0">
                Compétences : {{ form.competences }}
              </div>
            </div>

            <!-- Disponibilité -->
            <div class="form-group full">
              <label class="form-label">Disponibilité</label>
              <div class="toggle-group">
                <button class="toggle-btn" [class.active-green]="form.disponibilite === true"
                        (click)="form.disponibilite = true" id="tech-dispo-true">
                  ✅ Disponible
                </button>
                <button class="toggle-btn" [class.active-red]="form.disponibilite === false"
                        (click)="form.disponibilite = false" id="tech-dispo-false">
                  🔴 Occupé
                </button>
              </div>
            </div>

          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-ghost" (click)="closeModal()">Annuler</button>
          <button class="btn btn-primary" (click)="sauvegarder()" id="tech-submit">
            {{ editMode ? '💾 Modifier' : '＋ Ajouter' }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    /* Cards Grid */
    .tech-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(270px, 1fr));
      gap: 18px;
    }
    .tech-card {
      background: var(--surface); border: 1px solid var(--border);
      border-radius: var(--radius-lg); overflow: hidden;
      transition: var(--transition); display: flex; flex-direction: column;
    }
    .tech-card:hover {
      border-color: var(--border-hover);
      transform: translateY(-3px);
      box-shadow: 0 8px 30px rgba(0,0,0,0.3);
    }
    .tech-card-header {
      position: relative;
      background: linear-gradient(135deg, var(--surface-2), var(--surface-3));
      padding: 28px 20px 20px;
      display: flex; justify-content: center;
      border-bottom: 1px solid var(--border);
    }
    .tech-big-avatar {
      width: 70px; height: 70px; border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      font-size: 22px; font-weight: 800; color: white;
      box-shadow: 0 4px 20px rgba(0,0,0,0.4);
    }
    .tech-big-avatar.dispo  { background: linear-gradient(135deg, #059669, #10b981); }
    .tech-big-avatar.occupe { background: linear-gradient(135deg, #b91c1c, #ef4444); }
    .tech-status-dot {
      position: absolute; top: 16px; right: 16px;
      width: 10px; height: 10px; border-radius: 50%;
      border: 2px solid var(--surface);
    }
    .tech-status-dot.green { background: #10b981; box-shadow: 0 0 8px rgba(16,185,129,0.6); }
    .tech-status-dot.red   { background: #ef4444; box-shadow: 0 0 8px rgba(239,68,68,0.6); }
    .tech-card-body  { padding: 16px 20px; flex: 1; }
    .tech-name       { font-size: 16px; font-weight: 700; color: var(--text); margin-bottom: 8px; }
    .tech-dispo-badge { margin-bottom: 12px; }
    .skills-title { font-size: 11px; font-weight: 600; color: var(--text-dim);
                    text-transform: uppercase; letter-spacing: 0.06em; margin-bottom: 6px; }
    /* Badges compétences sur la carte */
    .skills-badges { display: flex; flex-wrap: wrap; gap: 4px; }
    .skill-badge {
      font-size: 11px; padding: 3px 8px;
      background: rgba(99,102,241,.12);
      border: 1px solid rgba(99,102,241,.25);
      color: #a5b4fc;
      border-radius: 20px;
    }
    .tech-card-footer {
      display: flex; justify-content: space-between; align-items: center;
      padding: 12px 16px;
      border-top: 1px solid var(--border);
      background: var(--surface-2);
    }
    /* Empty */
    .empty-card {
      display: flex; flex-direction: column; align-items: center;
      justify-content: center; padding: 80px 20px; text-align: center;
      background: var(--surface); border: 1px dashed var(--border);
      border-radius: var(--radius-lg); gap: 12px;
    }
    .empty-icon { font-size: 56px; }
    .empty-card h3 { font-size: 18px; color: var(--text); }
    .empty-card p  { font-size: 13px; color: var(--text-dim); }
    /* Toggle */
    .toggle-group { display: flex; gap: 10px; }
    .toggle-btn {
      flex: 1; padding: 10px 16px;
      background: var(--surface-2); border: 1px solid var(--border);
      border-radius: var(--radius); color: var(--text-muted);
      font-size: 13px; font-weight: 500; cursor: pointer;
      transition: var(--transition);
    }
    .toggle-btn.active-green { background: rgba(16,185,129,0.15); border-color: rgba(16,185,129,0.4); color: #34d399; }
    .toggle-btn.active-red   { background: rgba(239,68,68,0.15);  border-color: rgba(239,68,68,0.4);  color: #f87171; }
    /* Compétences checkboxes grid */
    .competences-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 8px;
      margin-top: 6px;
    }
    .comp-checkbox {
      display: flex; align-items: center; gap: 8px;
      padding: 9px 12px;
      background: var(--surface-2);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      cursor: pointer;
      transition: var(--transition);
      user-select: none;
    }
    .comp-checkbox:hover {
      border-color: rgba(99,102,241,.4);
      background: rgba(99,102,241,.06);
    }
    .comp-checkbox.checked {
      background: rgba(99,102,241,.15);
      border-color: rgba(99,102,241,.5);
    }
    .comp-checkbox input { display: none; }
    .comp-icon  { font-size: 16px; }
    .comp-label { font-size: 12px; font-weight: 500; color: var(--text-secondary); }
    .comp-checkbox.checked .comp-label { color: #a5b4fc; }
    .form-hint.ok { color: #34d399; font-size: 11px; margin-top: 8px;
                    background: rgba(52,211,153,.06); border: 1px solid rgba(52,211,153,.15);
                    border-radius: 6px; padding: 6px 10px; }
  `]
})
export class TechniciensComponent implements OnInit {
  techniciens:     any[]   = [];
  competencesList          = COMPETENCES_LISTE;
  selectedCompetences: string[] = [];
  loading   = true;
  showModal = false;
  editMode  = false;
  editId: number | null = null;
  form: any = { nom: '', competences: '', disponibilite: true };

  constructor(private api: ApiService) {}

  ngOnInit() { this.charger(); }

  charger() {
    this.loading = true;
    this.api.getTechniciens().subscribe({
      next: (d) => { this.techniciens = d; this.loading = false; },
      error: ()  => { this.loading = false; }
    });
  }

  getInitials(nom: string): string {
    if (!nom) return '?';
    return nom.split(' ').map((n: string) => n[0]).join('').toUpperCase().substring(0, 2);
  }

  // ─── Compétences checkboxes ─────────────────────────────────────────
  isChecked(label: string): boolean {
    return this.selectedCompetences.includes(label);
  }

  toggleCompetence(label: string) {
    const idx = this.selectedCompetences.indexOf(label);
    if (idx >= 0) {
      this.selectedCompetences.splice(idx, 1);
    } else {
      this.selectedCompetences.push(label);
    }
    // Synchroniser avec form.competences (chaîne séparée par virgules)
    this.form.competences = this.selectedCompetences.join(', ');
  }

  selectedCount(): number {
    return this.selectedCompetences.length;
  }

  /** Parse une chaîne "A, B, C" en tableau pour l'affichage badges */
  parseCompetences(comp: string): string[] {
    if (!comp) return [];
    return comp.split(',').map(s => s.trim()).filter(s => s.length > 0);
  }

  /** Retourne l'icône correspondant à une compétence */
  getCompetenceIcon(label: string): string {
    const found = COMPETENCES_LISTE.find(c =>
      label.toLowerCase().includes(c.label.toLowerCase()) ||
      c.label.toLowerCase().includes(label.toLowerCase())
    );
    return found ? found.icon : '🔧';
  }

  // ─── Modal ──────────────────────────────────────────────────────────
  openModal(t?: any) {
    this.showModal = true;
    if (t) {
      this.editMode = true; this.editId = t.id;
      // Reconstruire les cases cochées depuis la chaîne existante
      this.selectedCompetences = this.parseCompetences(t.competences || '');
      this.form = {
        nom: t.nom,
        competences: t.competences || '',
        disponibilite: t.disponibilite
      };
    } else {
      this.editMode = false; this.editId = null;
      this.selectedCompetences = [];
      this.form = { nom: '', competences: '', disponibilite: true };
    }
  }

  closeModal() { this.showModal = false; }

  sauvegarder() {
    if (!this.form.nom?.trim()) return alert('Le nom est obligatoire');
    const obs = this.editMode && this.editId
      ? this.api.updateTechnicien(this.editId, this.form)
      : this.api.createTechnicien(this.form);
    obs.subscribe({
      next: () => { this.charger(); this.closeModal(); },
      error: (e) => alert('Erreur : ' + e.message)
    });
  }

  supprimer(id: number) {
    if (confirm('Supprimer ce technicien ?'))
      this.api.deleteTechnicien(id).subscribe(() => this.charger());
  }
}
