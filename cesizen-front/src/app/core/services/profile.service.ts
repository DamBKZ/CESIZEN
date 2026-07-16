import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly http = inject(HttpClient);

  getCurrentUser() {
    return this.http.get('/api/users/me');
  }

  updateProfile(data: any) {
    return this.http.put('/api/users/me', data);
  }

  changePassword(data: any) {
    return this.http.put('/api/users/me/password', data);
  }

  getHistory() {
    return this.http.get<any[]>('/api/diagnostic/history/me');
  }

  deleteAccount() {
    return this.http.delete('/api/users/me');
  }
}
