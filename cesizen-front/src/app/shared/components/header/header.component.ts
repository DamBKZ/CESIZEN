import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserStore } from '../../../core/stores/user.store';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  private readonly userStore = inject(UserStore);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);

  isLoggingOut = false;

  isAuthenticated = computed(() => this.userStore.isAuthenticated());
  user = computed(() => this.userStore.user());

  logout(): void {
    if (this.isLoggingOut) {
      return;
    }

    this.isLoggingOut = true;

    const url = this.api.url('/auth/logout');

    this.http.post(url, {}, { withCredentials: true }).subscribe({
      next: () => {
        this.isLoggingOut = false;
        this.userStore.logout();
        this.toast.success('Déconnexion réussie');
        this.router.navigate(['/login'], { replaceUrl: true });
      },
      error: () => {
        this.isLoggingOut = false;
        this.userStore.logout();
        this.toast.error('Déconnexion locale effectuée');
        this.router.navigate(['/login'], { replaceUrl: true });
      }
    });
  }
}
