import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { UserStore } from '../core/stores/user.store';
import { UiStore } from '../core/stores/ui.store';
import { ApiService } from '../core/services/api.service';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly userStore = inject(UserStore);
  private readonly ui = inject(UiStore);
  private readonly api = inject(ApiService);

  login(email: string, password: string) {
    this.ui.setLoading(true);

    return this.http.post('/auth/login', { email, password }, { withCredentials: true }).subscribe({
      next: (res: any) => {
        const accessToken = res.accessToken;

        if (!accessToken) {
          this.ui.showSnackbar('Connexion réussie mais token manquant', 'error');
          this.ui.setLoading(false);
          return;
        }

        this.userStore.login(accessToken);
        this.ui.showSnackbar('Connexion réussie', 'success');
        this.router.navigate(['/informations/list']);
        this.ui.setLoading(false);
      },
      error: () => {
        this.ui.showSnackbar('Identifiants invalides', 'error');
        this.ui.setLoading(false);
      }
    });
  }

  refreshToken() {
    const url = this.api.url('/auth/refresh');
    return this.http.post(url, {} as any, { withCredentials: true });
  }

  async refreshOnStart(): Promise<void> {
    try {
      const res: any = await firstValueFrom(this.refreshToken());
      const accessToken = res?.accessToken;
      if (accessToken) {
        this.userStore.login(accessToken);
      }
    } catch (err) {
    }
  }

  register(data: any) {
    this.ui.setLoading(true);

    return this.http.post('/api/users/register', data).subscribe({
      next: () => {
        this.ui.showSnackbar('Compte créé avec succès', 'success');
        this.router.navigate(['/login']);
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors de la création du compte', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  forgotPassword(email: string) {
    this.ui.setLoading(true);

    return this.http.post('/auth/reset-password/request', { email }).subscribe({
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

    return this.http.post('/auth/reset-password/confirm', { token, newPassword: password }).subscribe({
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
