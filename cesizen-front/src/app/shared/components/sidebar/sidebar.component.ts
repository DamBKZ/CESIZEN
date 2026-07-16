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

  isLoggingOut = false;

  ngOnInit(): void {
    if (!this.userStore.isAuthenticated() || this.userStore.user()) {
      return;
    }

    this.profileService.getCurrentUser().subscribe({
      next: (user: any) => this.userStore.setUser(user),
      error: () => this.userStore.clear()
    });
  }

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
