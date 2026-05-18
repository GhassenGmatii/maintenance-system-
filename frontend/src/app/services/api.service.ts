import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API = '/api';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  // ─── DASHBOARD ───────────────────────────────────────
  getDashboardStats(): Observable<any> {
    return this.http.get(`${API}/dashboard/stats`);
  }

  // ─── ÉQUIPEMENTS ─────────────────────────────────────
  getEquipements(): Observable<any[]> { return this.http.get<any[]>(`${API}/equipements`); }
  getEquipementsEnPanne(): Observable<any[]> { return this.http.get<any[]>(`${API}/equipements/en-panne`); }
  getEquipementsOperationnels(): Observable<any[]> { return this.http.get<any[]>(`${API}/equipements/operationnel`); }
  getEquipement(id: number): Observable<any> { return this.http.get(`${API}/equipements/${id}`); }
  createEquipement(data: any): Observable<any> { return this.http.post(`${API}/equipements`, data); }
  updateEquipement(id: number, data: any): Observable<any> { return this.http.put(`${API}/equipements/${id}`, data); }
  deleteEquipement(id: number): Observable<any> { return this.http.delete(`${API}/equipements/${id}`); }

  // ─── PANNES ──────────────────────────────────────────
  getPannes(): Observable<any[]> { return this.http.get<any[]>(`${API}/pannes`); }
  getPanne(id: number): Observable<any> { return this.http.get(`${API}/pannes/${id}`); }
  createPanne(data: any): Observable<any> { return this.http.post(`${API}/pannes`, data); }
  updatePanne(id: number, data: any): Observable<any> { return this.http.put(`${API}/pannes/${id}`, data); }
  deletePanne(id: number): Observable<any> { return this.http.delete(`${API}/pannes/${id}`); }

  // ─── INTERVENTIONS ───────────────────────────────────
  getInterventions(): Observable<any[]> { return this.http.get<any[]>(`${API}/interventions`); }
  getIntervention(id: number): Observable<any> { return this.http.get(`${API}/interventions/${id}`); }
  createIntervention(data: any): Observable<any> { return this.http.post(`${API}/interventions`, data); }
  updateIntervention(id: number, data: any): Observable<any> { return this.http.put(`${API}/interventions/${id}`, data); }
  deleteIntervention(id: number): Observable<any> { return this.http.delete(`${API}/interventions/${id}`); }

  // ─── TECHNICIENS ─────────────────────────────────────
  getTechniciens(): Observable<any[]> { return this.http.get<any[]>(`${API}/techniciens`); }
  getTechnicien(id: number): Observable<any> { return this.http.get(`${API}/techniciens/${id}`); }
  createTechnicien(data: any): Observable<any> { return this.http.post(`${API}/techniciens`, data); }
  updateTechnicien(id: number, data: any): Observable<any> { return this.http.put(`${API}/techniciens/${id}`, data); }
  deleteTechnicien(id: number): Observable<any> { return this.http.delete(`${API}/techniciens/${id}`); }
}
