import {
  Component,
  computed,
  inject,
  signal
} from '@angular/core';

import { AuthService } from '../../../auth/auth.service';
import { UserStore } from '../../../core/stores/user.store';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  private readonly auth = inject(AuthService);
  private readonly userStore = inject(UserStore);
  private readonly toast = inject(ToastService);

  readonly isLoggingOut = signal(false);
  readonly isAuthenticated = this.userStore.isAuthenticated;
  readonly user = this.userStore.user;

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
        this.toast.info(
          'Session locale fermée'
        );
      },
      complete: () => {
        this.isLoggingOut.set(false);
      }
    });
  }
}
