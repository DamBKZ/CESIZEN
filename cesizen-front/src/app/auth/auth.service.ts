import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserStore } from '../core/stores/user.store';
import { UiStore } from '../core/stores/ui.store';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly userStore = inject(UserStore);
  private readonly ui = inject(UiStore);

  login(email: string, password: string) {
    this.ui.setLoading(true);

    return this.http.post('/api/auth/login', { email, password }).subscribe({
      next: (res: any) => {
        this.userStore.login(res.token, res.user);
        this.ui.showSnackbar('Connexion réussie', 'success');
      },
      error: () => {
        this.ui.showSnackbar('Identifiants invalides', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  register(data: any) {
    this.ui.setLoading(true);

    return this.http.post('/api/auth/register', data).subscribe({
      next: () => {
        this.ui.showSnackbar('Compte créé avec succès', 'success');
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors de la création du compte', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  forgotPassword(email: string) {
    this.ui.setLoading(true);

    return this.http.post('/api/auth/forgot-password', { email }).subscribe({
      next: () => {
        this.ui.showSnackbar('Email envoyé', 'success');
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors de la demande', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  resetPassword(token: string, password: string) {
    this.ui.setLoading(true);

    return this.http.post(`/api/auth/reset-password/${token}`, { password }).subscribe({
      next: () => {
        this.ui.showSnackbar('Mot de passe réinitialisé', 'success');
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors de la réinitialisation', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }
}
