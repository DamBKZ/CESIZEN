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
    if (this.isLoggingOut) { return; }
    this.isLoggingOut = true;
    function readCookie(name: string): string | null {
      const match = document.cookie.match(new RegExp('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)'));
      return match ? decodeURIComponent(match[2]) : null;
    }

    const xsrf = readCookie('XSRF-TOKEN');
    const options: any = { withCredentials: true };
    if (xsrf) { options.headers = { 'X-XSRF-TOKEN': xsrf }; }

    const url = this.api.url('/auth/logout');
    this.http.post(url, {}, options).subscribe({
      next: () => {
        this.isLoggingOut = false;
        this.userStore.logout();
        this.toast.success('Déconnexion réussie');
        this.router.navigate(['/login'], { replaceUrl: true });
        history.replaceState({}, '', '/login');
      },
      error: () => {
        this.isLoggingOut = false;
        this.userStore.logout();
        this.toast.error('Échec de la déconnexion');
        this.router.navigate(['/login'], { replaceUrl: true });
        history.replaceState({}, '', '/login');
      }
    });
  }
}
