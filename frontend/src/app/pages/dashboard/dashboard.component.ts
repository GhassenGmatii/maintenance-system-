import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-title">
        <div class="page-icon">📊</div>
        <div>
          <h2>Tableau de bord</h2>
          <p>Vue d'ensemble des opérations de maintenance</p>
        </div>
      </div>
      <div class="header-actions">
        <span class="date-badge">{{ today }}</span>
        <button class="btn btn-primary btn-sm" (click)="refresh()">
          <span>↻</span> Actualiser
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div class="loading-state" *ngIf="loading">
      <div class="spinner"></div>
      <span>Chargement des statistiques...</span>
    </div>

    <!-- KPI Cards -->
    <div class="kpi-grid" *ngIf="!loading && stats">
      <div class="kpi-card" *ngFor="let card of kpiCards">
        <div class="kpi-glow" [style.background]="card.glow"></div>
        <div class="kpi-icon" [style.background]="card.bg">{{ card.icon }}</div>
        <div class="kpi-body">
          <div class="kpi-value" [style.color]="card.color">{{ card.value }}</div>
          <div class="kpi-label">{{ card.label }}</div>
        </div>
        <div class="kpi-trend" [style.color]="card.color">{{ card.suffix }}</div>
      </div>
    </div>

    <!-- Quick Nav Links -->
    <div class="quick-links" *ngIf="!loading && stats">
      <h3 class="section-title">Accès rapide</h3>
      <div class="quick-grid">
        <a routerLink="/equipements" class="quick-card">
          <div class="quick-icon">🏭</div>
          <div>
            <div class="quick-name">Équipements</div>
            <div class="quick-desc">Gérer le parc matériel</div>
          </div>
          <div class="quick-arrow">→</div>
        </a>
        <a routerLink="/pannes" class="quick-card">
          <div class="quick-icon">⚠️</div>
          <div>
            <div class="quick-name">Pannes</div>
            <div class="quick-desc">Signaler &amp; suivre les pannes</div>
          </div>
          <div class="quick-arrow">→</div>
        </a>
        <a routerLink="/interventions" class="quick-card">
          <div class="quick-icon">🔧</div>
          <div>
            <div class="quick-name">Interventions</div>
            <div class="quick-desc">Planifier les bons de travaux</div>
          </div>
          <div class="quick-arrow">→</div>
        </a>
        <a routerLink="/techniciens" class="quick-card">
          <div class="quick-icon">👷</div>
          <div>
            <div class="quick-name">Techniciens</div>
            <div class="quick-desc">Affecter &amp; gérer l'équipe</div>
          </div>
          <div class="quick-arrow">→</div>
        </a>
      </div>
    </div>

    <!-- Status Bars -->
    <div class="status-section" *ngIf="!loading && stats">
      <div class="status-card">
        <div class="status-header">
          <h4>Répartition des interventions</h4>
        </div>
        <div class="bar-list">
          <div class="bar-item">
            <div class="bar-info">
              <span class="bar-label">Planifiées</span>
              <span class="bar-val" style="color:#60a5fa">{{ stats.interventionsPlanifiees }}</span>
            </div>
            <div class="bar-track">
              <div class="bar-fill" style="background:#3b82f6"
                   [style.width]="getPct(stats.interventionsPlanifiees)+'%'"></div>
            </div>
          </div>
          <div class="bar-item">
            <div class="bar-info">
              <span class="bar-label">En cours</span>
              <span class="bar-val" style="color:#fbbf24">{{ stats.interventionsEnCours }}</span>
            </div>
            <div class="bar-track">
              <div class="bar-fill" style="background:#f59e0b"
                   [style.width]="getPct(stats.interventionsEnCours)+'%'"></div>
            </div>
          </div>
          <div class="bar-item">
            <div class="bar-info">
              <span class="bar-label">Terminées</span>
              <span class="bar-val" style="color:#34d399">{{ stats.interventionsTerminees }}</span>
            </div>
            <div class="bar-track">
              <div class="bar-fill" style="background:#10b981"
                   [style.width]="getPct(stats.interventionsTerminees)+'%'"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="status-card">
        <div class="status-header">
          <h4>État des équipements</h4>
        </div>
        <div class="doughnut-stats">
          <div class="d-stat">
            <div class="d-circle" style="border-color:#10b981"></div>
            <span class="d-label">Opérationnels</span>
            <span class="d-val" style="color:#34d399">{{ stats.equipementsOperationnels }}</span>
          </div>
          <div class="d-stat">
            <div class="d-circle" style="border-color:#ef4444"></div>
            <span class="d-label">En panne</span>
            <span class="d-val" style="color:#f87171">{{ stats.equipementsEnPanne }}</span>
          </div>
          <div class="d-stat">
            <div class="d-circle" style="border-color:#6366f1"></div>
            <span class="d-label">Techniciens dispos</span>
            <span class="d-val" style="color:#a5b4fc">{{ stats.techniciensDisponibles }}</span>
          </div>
          <div class="d-stat">
            <div class="d-circle" style="border-color:#f59e0b"></div>
            <span class="d-label">Pannes ce mois</span>
            <span class="d-val" style="color:#fbbf24">{{ stats.pannesCeMois }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .header-actions { display: flex; align-items: center; gap: 12px; }
    .date-badge {
      background: rgba(255,255,255,0.05);
      border: 1px solid rgba(255,255,255,0.08);
      border-radius: 99px;
      padding: 5px 14px;
      font-size: 12px;
      color: #64748b;
    }

    /* KPI Grid */
    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      margin-bottom: 28px;
    }
    @media (max-width: 1100px) { .kpi-grid { grid-template-columns: repeat(2,1fr); } }
    @media (max-width: 600px)  { .kpi-grid { grid-template-columns: 1fr; } }

    .kpi-card {
      position: relative;
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      padding: 22px 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      overflow: hidden;
      transition: var(--transition);
    }
    .kpi-card:hover { border-color: var(--border-hover); transform: translateY(-2px); }

    .kpi-glow {
      position: absolute;
      top: -30px; right: -30px;
      width: 100px; height: 100px;
      border-radius: 50%;
      opacity: 0.12;
      pointer-events: none;
    }

    .kpi-icon {
      width: 50px; height: 50px;
      border-radius: var(--radius);
      display: flex; align-items: center; justify-content: center;
      font-size: 24px;
      flex-shrink: 0;
    }

    .kpi-body { flex: 1; }
    .kpi-value { font-size: 28px; font-weight: 800; line-height: 1; }
    .kpi-label { font-size: 12px; color: var(--text-dim); margin-top: 4px; }
    .kpi-trend { font-size: 11px; font-weight: 600; opacity: 0.7; }

    /* Section title */
    .section-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 0.08em;
      margin-bottom: 14px;
    }

    /* Quick Nav */
    .quick-links { margin-bottom: 28px; }
    .quick-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 14px;
    }
    @media (max-width: 900px) { .quick-grid { grid-template-columns: repeat(2,1fr); } }

    .quick-card {
      display: flex;
      align-items: center;
      gap: 14px;
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      padding: 18px 16px;
      transition: var(--transition);
      cursor: pointer;
    }
    .quick-card:hover {
      border-color: var(--border-hover);
      background: var(--surface-2);
      transform: translateY(-2px);
    }
    .quick-icon { font-size: 24px; flex-shrink: 0; }
    .quick-name { font-size: 14px; font-weight: 600; color: var(--text); }
    .quick-desc { font-size: 11px; color: var(--text-dim); margin-top: 2px; }
    .quick-arrow { margin-left: auto; color: var(--text-dim); font-size: 18px; transition: var(--transition); }
    .quick-card:hover .quick-arrow { color: var(--primary); transform: translateX(3px); }

    /* Status Section */
    .status-section {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
    }
    @media (max-width: 900px) { .status-section { grid-template-columns: 1fr; } }

    .status-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      padding: 22px;
    }
    .status-header { margin-bottom: 20px; }
    .status-header h4 { font-size: 14px; font-weight: 600; color: var(--text); }

    .bar-list { display: flex; flex-direction: column; gap: 16px; }
    .bar-item {}
    .bar-info { display: flex; justify-content: space-between; margin-bottom: 6px; }
    .bar-label { font-size: 12px; color: var(--text-muted); }
    .bar-val { font-size: 12px; font-weight: 700; }
    .bar-track { height: 6px; background: var(--surface-3); border-radius: 99px; overflow: hidden; }
    .bar-fill { height: 100%; border-radius: 99px; transition: width 0.8s ease; }

    .doughnut-stats { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .d-stat {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 14px;
      background: var(--surface-2);
      border-radius: var(--radius);
      border: 1px solid var(--border);
    }
    .d-circle { width: 10px; height: 10px; border-radius: 50%; border: 2px solid; flex-shrink: 0; }
    .d-label { font-size: 11px; color: var(--text-dim); flex: 1; }
    .d-val { font-size: 18px; font-weight: 800; }
  `]
})
export class DashboardComponent implements OnInit {
  stats: any = null;
  loading = true;
  today = new Date().toLocaleDateString('fr-FR', { weekday:'long', day:'numeric', month:'long', year:'numeric' });
  kpiCards: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit() { this.refresh(); }

  refresh() {
    this.loading = true;
    this.api.getDashboardStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.buildCards();
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  buildCards() {
    const s = this.stats;
    this.kpiCards = [
      { icon:'🔧', label:'BT Planifiées',          value: s.interventionsPlanifiees, color:'#60a5fa', bg:'rgba(59,130,246,0.15)',  glow:'#3b82f6', suffix:'interventions' },
      { icon:'⚙️', label:'BT En cours',            value: s.interventionsEnCours,    color:'#fbbf24', bg:'rgba(245,158,11,0.15)', glow:'#f59e0b', suffix:'en cours' },
      { icon:'✅', label:'BT Terminées',            value: s.interventionsTerminees,  color:'#34d399', bg:'rgba(16,185,129,0.15)', glow:'#10b981', suffix:'réalisées' },
      { icon:'🚨', label:'Pannes ce mois',          value: s.pannesCeMois,            color:'#f87171', bg:'rgba(239,68,68,0.15)',  glow:'#ef4444', suffix:'pannes' },
      { icon:'🏭', label:'Équip. opérationnels',   value: s.equipementsOperationnels, color:'#34d399', bg:'rgba(16,185,129,0.15)', glow:'#10b981', suffix:'actifs' },
      { icon:'⚠️', label:'Équip. en panne',        value: s.equipementsEnPanne,       color:'#f87171', bg:'rgba(239,68,68,0.15)',  glow:'#ef4444', suffix:'en panne' },
      { icon:'👷', label:'Techniciens disponibles', value: s.techniciensDisponibles,   color:'#a5b4fc', bg:'rgba(99,102,241,0.15)', glow:'#6366f1', suffix:'dispos' },
      { icon:'💰', label:'Coût total',              value: (s.coutTotalInterventions||0).toLocaleString('fr-FR'), color:'#fbbf24', bg:'rgba(245,158,11,0.15)', glow:'#f59e0b', suffix:'DT' },
    ];
  }

  getPct(val: number): number {
    const total = (this.stats.interventionsPlanifiees || 0)
                + (this.stats.interventionsEnCours || 0)
                + (this.stats.interventionsTerminees || 0);
    return total > 0 ? Math.round((val / total) * 100) : 0;
  }
}
