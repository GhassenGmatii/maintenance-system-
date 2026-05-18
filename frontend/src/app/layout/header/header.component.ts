import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

interface NavItem {
  path: string;
  label: string;
  icon: string;
  badge?: string;
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  template: `
    <header class="topbar">
      <div class="topbar-left">
        <div class="logo">
          <div class="logo-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" fill="currentColor"/>
            </svg>
          </div>
          <div class="logo-text">
            <span class="logo-name">MaintenanceMedis</span>
            <span class="logo-sub">Gestion des opérations</span>
          </div>
        </div>
      </div>

      <nav class="topbar-nav">
        <a *ngFor="let item of navItems"
           [routerLink]="item.path"
           routerLinkActive="active"
           class="nav-link">
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-label">{{ item.label }}</span>
        </a>
      </nav>

      <div class="topbar-right">
        <div class="status-dot">
          <span class="dot-pulse"></span>
          <span class="status-label">Système actif</span>
        </div>
        <div class="user-chip">
          <div class="user-avatar">GH</div>
          <span class="user-name">Ghassen</span>
        </div>
      </div>
    </header>
  `,
  styles: [`
    .topbar {
      position: fixed;
      top: 0; left: 0; right: 0;
      height: 62px;
      background: rgba(26, 29, 39, 0.95);
      backdrop-filter: blur(20px);
      border-bottom: 1px solid rgba(255,255,255,0.07);
      display: flex;
      align-items: center;
      padding: 0 24px;
      gap: 24px;
      z-index: 100;
      box-shadow: 0 4px 24px rgba(0,0,0,0.3);
    }

    /* Logo */
    .topbar-left { display: flex; align-items: center; min-width: 220px; }
    .logo { display: flex; align-items: center; gap: 12px; }
    .logo-icon {
      width: 38px; height: 38px;
      background: linear-gradient(135deg, #6366f1, #8b5cf6);
      border-radius: 10px;
      display: flex; align-items: center; justify-content: center;
      color: white;
      box-shadow: 0 4px 12px rgba(99,102,241,0.5);
    }
    .logo-name { display: block; font-size: 15px; font-weight: 700; color: #f1f5f9; }
    .logo-sub  { display: block; font-size: 10px; color: #64748b; }

    /* Nav */
    .topbar-nav {
      display: flex;
      align-items: center;
      gap: 2px;
      flex: 1;
      justify-content: center;
    }

    .nav-link {
      display: flex; align-items: center; gap: 7px;
      padding: 7px 14px;
      border-radius: 8px;
      font-size: 13px;
      font-weight: 500;
      color: #64748b;
      transition: all 0.2s;
      white-space: nowrap;
    }
    .nav-link:hover { background: rgba(255,255,255,0.06); color: #94a3b8; }
    .nav-link.active {
      background: rgba(99,102,241,0.15);
      color: #a5b4fc;
      border: 1px solid rgba(99,102,241,0.3);
    }
    .nav-icon { font-size: 15px; }

    /* Right */
    .topbar-right { display: flex; align-items: center; gap: 16px; min-width: 220px; justify-content: flex-end; }

    .status-dot { display: flex; align-items: center; gap: 6px; }
    .dot-pulse {
      width: 8px; height: 8px;
      background: #10b981;
      border-radius: 50%;
      box-shadow: 0 0 0 0 rgba(16,185,129,0.4);
      animation: pulse 2s infinite;
    }
    @keyframes pulse {
      0%   { box-shadow: 0 0 0 0 rgba(16,185,129,0.4); }
      70%  { box-shadow: 0 0 0 8px rgba(16,185,129,0); }
      100% { box-shadow: 0 0 0 0 rgba(16,185,129,0); }
    }
    .status-label { font-size: 11px; color: #64748b; }

    .user-chip {
      display: flex; align-items: center; gap: 8px;
      background: rgba(255,255,255,0.05);
      border: 1px solid rgba(255,255,255,0.08);
      border-radius: 99px;
      padding: 4px 12px 4px 4px;
    }
    .user-avatar {
      width: 28px; height: 28px;
      background: linear-gradient(135deg, #6366f1, #8b5cf6);
      border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      font-size: 11px;
      font-weight: 700;
      color: white;
    }
    .user-name { font-size: 13px; font-weight: 500; color: #94a3b8; }
  `]
})
export class HeaderComponent {
  navItems: NavItem[] = [
    { path: '/tableau-de-bord', label: 'Tableau de bord', icon: '📊' },
    { path: '/equipements',     label: 'Équipements',     icon: '🏭' },
    { path: '/pannes',          label: 'Pannes',          icon: '⚠️' },
    { path: '/interventions',   label: 'Interventions',   icon: '🔧' },
    { path: '/techniciens',     label: 'Techniciens',     icon: '👷' },
  ];
}
