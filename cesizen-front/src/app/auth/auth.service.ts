import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import {
  catchError,
  finalize,
  firstValueFrom,
  Observable,
  of,
  switchMap,
} from 'rxjs';

import { UserStore } from '../core/stores/user.store';
import { UiStore } from '../core/stores/ui.store';
import { ProfileService } from '../core/services/profile.service';

interface AuthTokenResponse {
  accessToken: string;
}

interface RegisterData {
  pseudo: string;
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly userStore = inject(UserStore);
  private readonly ui = inject(UiStore);
  private readonly profileService = inject(ProfileService);

  login(email: string, password: string): void {
    const normalizedEmail = email.trim().toLowerCase();

    if (!normalizedEmail || !password) {
      this.ui.showSnackbar(
        'Email et mot de passe obligatoires',
        'error'
      );
      return;
    }

    this.ui.setLoading(true);

    this.http
      .post<AuthTokenResponse>(
        '/auth/login',
        {
          email: normalizedEmail,
          password
        },
        {
          withCredentials: true
        }
      )
      .pipe(
        switchMap(response => {
          if (!response.accessToken) {
            throw new Error('Token d’accès manquant');
          }

          this.userStore.login(response.accessToken);

          return this.profileService.getCurrentUser();
        }),
        finalize(() => this.ui.setLoading(false))
      )
      .subscribe({
        next: user => {
          this.userStore.setUser(user);

          this.ui.showSnackbar(
            'Connexion réussie',
            'success'
          );

          void this.router.navigate([
            '/informations/list'
          ]);
        },
        error: () => {
          this.userStore.clear();

          this.ui.showSnackbar(
            'Identifiants invalides',
            'error'
          );
        }
      });
  }

  logout(): Observable<void> {
  return this.http
    .post<void>(
      '/auth/logout',
      {},
      {
        withCredentials: true
      }
    )
    .pipe(
      finalize(() => {
        this.userStore.logout();

        void this.router.navigate(
          ['/login'],
          {
            replaceUrl: true
          }
        );
      })
    );
}


  refreshToken(): Observable<AuthTokenResponse> {
    return this.http.post<AuthTokenResponse>(
      '/auth/refresh',
      {},
      {
        withCredentials: true
      }
    );
  }

  async refreshOnStart(): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.refreshToken()
      );

      if (!response.accessToken) {
        this.userStore.clear();
        return;
      }

      this.userStore.login(response.accessToken);

      const user = await firstValueFrom(
        this.profileService.getCurrentUser()
      );

      this.userStore.setUser(user);
    } catch {
      /*
       * L’absence de refresh token au premier chargement est normale
       * pour un visiteur non connecté.
       */
      this.userStore.clear();
    }
  }

  register(data: RegisterData): void {
    const payload = {
      pseudo: data.pseudo.trim(),
      email: data.email.trim().toLowerCase(),
      password: data.password
    };

    if (
      !payload.pseudo
      || !payload.email
      || !payload.password
    ) {
      this.ui.showSnackbar(
        'Tous les champs sont obligatoires',
        'error'
      );
      return;
    }

    this.ui.setLoading(true);

    this.http
      .post<void>(
        '/api/users/register',
        payload
      )
      .pipe(
        finalize(() => this.ui.setLoading(false))
      )
      .subscribe({
        next: () => {
          this.ui.showSnackbar(
            'Compte créé avec succès',
            'success'
          );

          void this.router.navigate(['/login']);
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors de la création du compte',
            'error'
          );
        }
      });
  }

  forgotPassword(email: string): void {
    const normalizedEmail = email.trim().toLowerCase();

    if (!normalizedEmail) {
      this.ui.showSnackbar(
        'L’adresse email est obligatoire',
        'error'
      );
      return;
    }

    this.ui.setLoading(true);

    this.http
      .post<void>(
        '/auth/reset-password/request',
        {
          email: normalizedEmail
        }
      )
      .pipe(
        /*
         * Le même message est volontairement affiché en cas d’erreur
         * pour ne pas révéler l’existence d’un compte.
         */
        catchError(() => of(undefined)),
        finalize(() => this.ui.setLoading(false))
      )
      .subscribe(() => {
        this.ui.showSnackbar(
          'Si le compte existe, un email a été envoyé',
          'success'
        );
      });
  }

  resetPassword(
    token: string,
    password: string
  ): void {
    const normalizedToken = token.trim();

    if (!normalizedToken || !password) {
      this.ui.showSnackbar(
        'Token et nouveau mot de passe obligatoires',
        'error'
      );
      return;
    }

    this.ui.setLoading(true);

    this.http
      .post<void>(
        '/auth/reset-password/confirm',
        {
          token: normalizedToken,
          newPassword: password
        }
      )
      .pipe(
        finalize(() => this.ui.setLoading(false))
      )
      .subscribe({
        next: () => {
          this.ui.showSnackbar(
            'Mot de passe réinitialisé',
            'success'
          );

          void this.router.navigate(['/login']);
        },
        error: () => {
          this.ui.showSnackbar(
            'Lien invalide ou expiré',
            'error'
          );
        }
      });
  }
}
