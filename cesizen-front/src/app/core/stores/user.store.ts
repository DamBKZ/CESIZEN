import { Injectable, signal, computed } from '@angular/core';

export interface User {
  userId: string;
  email: string;
  pseudo: string;
  userCreatedAt: string;
  active: boolean;
  role: {
    roleId: number;
    roleName: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class UserStore {
  private readonly _token = signal<string | null>(null);
  readonly token = this._token.asReadonly();

  private readonly _user = signal<User | null>(null);
  readonly user = this._user.asReadonly();

  readonly isAuthenticated = computed(() => !!this._token());

  readonly role = computed(() => this._user()?.role ?? null);

  login(token: string, user?: User | null): void {
    this._token.set(token);

    if (user !== undefined) {
      this._user.set(user);
    }
  }

  logout(): void {
    this._token.set(null);
    this._user.set(null);
  }

  setUser(user: User): void {
    this._user.set(user);
  }

  clear(): void {
    this._user.set(null);
    this._token.set(null);
  }
}
