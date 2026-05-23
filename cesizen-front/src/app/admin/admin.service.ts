import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminDiagnostic } from './models/diagnostic-admin.model';
import { AdminInformation } from './models/information-admin.model';
import { AdminLog } from './models/log-admin.model';
import { AdminUser } from './models/user-admin.model';
import { AdminStats } from './models/stats-admin.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private http = inject(HttpClient);
  private baseUrl = '/api/admin';

  getStats(){
    return this.http.get<AdminStats>(`${this.baseUrl}/stats`);
  }

  getAllUsers() {
  return this.http.get<AdminUser[]>('/api/admin/users');
}

activateUser(id: string) {
  return this.http.patch(`/api/admin/users/${id}/activate`, {});
}

deactivateUser(id: string) {
  return this.http.patch(`/api/admin/users/${id}/deactivate`, {});
}

deleteUser(id: string) {
  return this.http.delete(`/api/admin/users/${id}`);
}

getAllInformations() {
  return this.http.get<AdminInformation[]>('/api/admin/informations');
}

deleteInformation(id: string) {
  return this.http.delete(`/api/admin/informations/${id}`);
}

getAllDiagnostics() {
  return this.http.get<AdminDiagnostic[]>('/api/admin/diagnostics');
}

deleteDiagnostic(id: string) {
  return this.http.delete(`/api/admin/diagnostics/${id}`);
}

getAllLogs() {
  return this.http.get<AdminLog[]>('/api/admin/logs');
}

deleteLog(id: string) {
  return this.http.delete(`/api/admin/logs/${id}`);
}

}
