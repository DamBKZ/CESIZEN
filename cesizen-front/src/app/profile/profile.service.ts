import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);

  updateProfile(data: any) {
    return this.http.put('/api/users/me', data);
  }

  changePassword(data: any) {
    return this.http.put('/api/users/me/password', data);
  }

  getHistory(userId: string) {
  return this.http.get<any[]>(`/api/diagnostic/history/${userId}`);
}

}
