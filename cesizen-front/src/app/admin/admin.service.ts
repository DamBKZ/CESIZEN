import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AdminDiagnostic } from './models/diagnostic-admin.model';
import { AdminInformation } from './models/information-admin.model';
import { AdminLog } from './models/log-admin.model';
import { AdminUser } from './models/user-admin.model';
import { AdminStats } from './models/stats-admin.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/admin';

  getStats() {
    return this.http.get<AdminStats>(`${this.baseUrl}/stats`);
  }

  getAllUsers() {
    return this.http.get<AdminUser[]>(`${this.baseUrl}/users`);
  }

  activateUser(id: string) {
    return this.http.patch(`${this.baseUrl}/users/${id}/activate`, {});
  }

  deactivateUser(id: string) {
    return this.http.patch(`${this.baseUrl}/users/${id}/deactivate`, {});
  }

  deleteUser(id: string) {
    return this.http.delete(`${this.baseUrl}/users/${id}`);
  }

  getAllInformations() {
    return this.http.get<AdminInformation[]>(`${this.baseUrl}/informations`);
  }

  deleteInformation(id: string) {
    return this.http.delete(`${this.baseUrl}/informations/${id}`);
  }

  getAllDiagnostics() {
    return this.http.get<AdminDiagnostic[]>(`${this.baseUrl}/diagnostics`);
  }

  deleteDiagnostic(id: string) {
    return this.http.delete(`${this.baseUrl}/diagnostics/${id}`);
  }

  getAllLogs() {
    return this.http.get<AdminLog[]>(`${this.baseUrl}/logs`);
  }
}
