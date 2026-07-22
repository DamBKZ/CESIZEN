import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User } from '../stores/user.store';

export interface UpdateProfileRequest {
  email: string;
  pseudo: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface DiagnosticHistory {
  surveyId: string;
  score: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  createdAt: string;
}


@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly http = inject(HttpClient);

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(
      '/api/users/me'
    );
  }

  updateProfile(
    data: UpdateProfileRequest
  ): Observable<User> {
    return this.http.put<User>(
      '/api/users/me',
      data
    );
  }

  changePassword(
    data: ChangePasswordRequest
  ): Observable<void> {
    return this.http.put<void>(
      '/api/users/me/password',
      data
    );
  }

  getHistory(): Observable<DiagnosticHistory[]> {
    return this.http.get<DiagnosticHistory[]>(
      '/api/diagnostic/history/me'
    );
  }

  deleteAccount(): Observable<void> {
    return this.http.delete<void>(
      '/api/users/me'
    );
  }
}
