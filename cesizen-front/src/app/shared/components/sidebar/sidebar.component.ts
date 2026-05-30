import { Component, OnInit, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { UserStore } from '../../../core/stores/user.store';
import { HttpClient } from '@angular/common/http';
import { ToastService } from '../../services/toast.service';
import { ProfileService } from '../../../core/services/profile.service';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  private readonly userStore = inject(UserStore);
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);

  isAuthenticated = computed(() => this.userStore.isAuthenticated());
  role = computed(() => this.userStore.role());

  ngOnInit(): void {
    if (!this.userStore.isAuthenticated() || this.userStore.user()) {
      return;
    }

    this.profileService.getCurrentUser().subscribe({
      next: (user: any) => this.userStore.setUser(user)
    });
  }
  isLoggingOut = false;

  logout(): void {
    if (this.isLoggingOut) { return; }
    this.isLoggingOut = true;
    console.log('Sidebar logout clicked');
    function readCookie(name: string): string | null {
      const match = document.cookie.match(new RegExp('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)'));
      return match ? decodeURIComponent(match[2]) : null;
    }

    const xsrf = readCookie('XSRF-TOKEN');
    const options: any = { withCredentials: true };
    if (xsrf) { options.headers = { 'X-XSRF-TOKEN': xsrf }; }
    const toast = this.toast;
    const url = this.api.url('/auth/logout');

    this.http.post(url, {}, options).subscribe({
      next: () => {
        this.isLoggingOut = false;
        this.userStore.logout();
        toast.success('Déconnexion réussie');
        this.router.navigate(['/login'], { replaceUrl: true });
        history.replaceState({}, '', '/login');
      },
      error: (err) => {
        this.isLoggingOut = false;
        console.warn('Server logout failed', err);
        this.userStore.logout();
        toast.error('Échec de la déconnexion');
        this.router.navigate(['/login'], { replaceUrl: true });
        history.replaceState({}, '', '/login');
      }
    });
  }
}
