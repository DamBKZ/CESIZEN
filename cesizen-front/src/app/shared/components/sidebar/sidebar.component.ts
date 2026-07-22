import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import {
  RouterLink,
  RouterLinkActive
} from '@angular/router';

import { AuthService } from '../../../auth/auth.service';
import { ProfileService } from '../../../core/services/profile.service';
import { UserStore } from '../../../core/stores/user.store';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly userStore = inject(UserStore);
  private readonly profileService = inject(ProfileService);
  private readonly toast = inject(ToastService);

  readonly isAuthenticated = this.userStore.isAuthenticated;
  readonly role = this.userStore.role;
  readonly isLoggingOut = signal(false);

  ngOnInit(): void {
    if (
      !this.userStore.isAuthenticated() ||
      this.userStore.user()
    ) {
      return;
    }

    this.profileService.getCurrentUser().subscribe({
      next: (user) => {
        this.userStore.setUser(user);
      },
      error: () => {
        this.userStore.clear();
      }
    });
  }

  logout(): void {
    if (this.isLoggingOut()) {
      return;
    }

    this.isLoggingOut.set(true);

    this.auth.logout().subscribe({
      next: () => {
        this.toast.success('Déconnexion réussie');
      },
      error: () => {
        this.toast.info('Session locale fermée');
      },
      complete: () => {
        this.isLoggingOut.set(false);
      }
    });
  }
}
