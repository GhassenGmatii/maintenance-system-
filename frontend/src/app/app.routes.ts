import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { PannesComponent } from './pages/pannes/pannes.component';
import { InterventionsComponent } from './pages/interventions/interventions.component';
import { EquipementsComponent } from './pages/equipements/equipements.component';
import { TechniciensComponent } from './pages/techniciens/techniciens.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'tableau-de-bord', pathMatch: 'full' },
      { path: 'tableau-de-bord', component: DashboardComponent },
      { path: 'pannes', component: PannesComponent },
      { path: 'interventions', component: InterventionsComponent },
      { path: 'equipements', component: EquipementsComponent },
      { path: 'techniciens', component: TechniciensComponent }
    ]
  }
];
