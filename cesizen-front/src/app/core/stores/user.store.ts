import { Injectable, signal, computed } from '@angular/core';

export interface User {
  userId: string;
  email: string;
  pseudo: string;
  userCreatedAt: string;
  active: boolean;
  role: {
    roleId: string;
    roleName: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class UserStore {

  // --- Token ---
  private readonly _token = signal<string | null>(localStorage.getItem('access_token'));
  readonly token = this._token.asReadonly();

  // --- User ---
  private readonly _user = signal<User | null>(null);
  readonly user = this._user.asReadonly();

  // --- Authenticated ---
  readonly isAuthenticated = computed(() => !!this._token());

  // --- Role ---
  readonly role = computed(() => this._user()?.role ?? null);

  // --- Login ---
  login(token: string, user: User): void {
    localStorage.setItem('access_token', token);
    this._token.set(token);
    this._user.set(user);
  }

  // --- Logout ---
  logout(): void {
    localStorage.removeItem('access_token');
    this._token.set(null);
    this._user.set(null);
  }

  // --- Set user (après /me) ---
  setUser(user: User): void {
    this._user.set(user);
  }

clear() {
  this._user.set(null);
  this._token.set(null);
  localStorage.removeItem('access_token');
}


}
