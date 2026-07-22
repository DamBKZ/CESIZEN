import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';

import { AdminService } from '../admin.service';
import { AdminUser } from '../models/user-admin.model';
import { UiStore } from '../../core/stores/ui.store';
import { ConfirmService } from '../../shared/services/confirm.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly confirmService = inject(ConfirmService);
  private readonly ui = inject(UiStore);

  readonly displayedColumns = [
    'email',
    'pseudo',
    'role',
    'active',
    'actions'
  ];

  readonly users = signal<AdminUser[]>([]);
  readonly loading = signal(true);
  readonly processingUserId = signal<string | null>(null);

  ngOnInit(): void {
    this.loadUsers();
  }

  private loadUsers(): void {
    this.loading.set(true);

    this.adminService
      .getAllUsers()
      .pipe(
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: (users) => {
          this.users.set(users);
        },
        error: () => {
          this.users.set([]);

          this.ui.showSnackbar(
            'Erreur lors du chargement des utilisateurs',
            'error'
          );
        }
      });
  }

  activate(userId: string): void {
    if (this.processingUserId()) {
      return;
    }

    this.processingUserId.set(userId);

    this.adminService
      .activateUser(userId)
      .pipe(
        finalize(() => this.processingUserId.set(null))
      )
      .subscribe({
        next: () => {
          this.updateUserStatus(userId, true);

          this.ui.showSnackbar(
            'Utilisateur activé',
            'success'
          );
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors de l’activation',
            'error'
          );
        }
      });
  }

  deactivate(userId: string): void {
    if (this.processingUserId()) {
      return;
    }

    this.processingUserId.set(userId);

    this.adminService
      .deactivateUser(userId)
      .pipe(
        finalize(() => this.processingUserId.set(null))
      )
      .subscribe({
        next: () => {
          this.updateUserStatus(userId, false);

          this.ui.showSnackbar(
            'Utilisateur désactivé',
            'success'
          );
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors de la désactivation',
            'error'
          );
        }
      });
  }

  async deleteUser(userId: string): Promise<void> {
    if (this.processingUserId()) {
      return;
    }

    const confirmed = await this.confirmService.confirm(
      'Confirmer la suppression de cet utilisateur ?'
    );

    if (!confirmed) {
      return;
    }

    this.processingUserId.set(userId);

    this.adminService
      .deleteUser(userId)
      .pipe(
        finalize(() => this.processingUserId.set(null))
      )
      .subscribe({
        next: () => {
          this.users.update((users) =>
            users.filter((user) => user.userId !== userId)
          );

          this.ui.showSnackbar(
            'Utilisateur supprimé',
            'success'
          );
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors de la suppression',
            'error'
          );
        }
      });
  }

  private updateUserStatus(
    userId: string,
    active: boolean
  ): void {
    this.users.update((users) =>
      users.map((user) =>
        user.userId === userId
          ? { ...user, active }
          : user
      )
    );
  }
}
