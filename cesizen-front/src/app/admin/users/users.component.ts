import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../admin.service';
import { ConfirmService } from '../../shared/services/confirm.service';
import { UiStore } from '../../core/stores/ui.store';
import { AdminUser } from '../models/user-admin.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

  private adminService = inject(AdminService);
  private confirmService = inject(ConfirmService);
  private ui = inject(UiStore);

  displayedColumns = ['email', 'firstname', 'lastname', 'role', 'active', 'actions'];
  users: AdminUser[] = [];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminService.getAllUsers().subscribe(data => {
      this.users = data;
    });
  }

  activate(id: string): void {
    this.adminService.activateUser(id).subscribe({
      next: () => {
        this.ui.showSnackbar('Utilisateur activé', 'success');
        this.loadUsers();
      },
      error: () => this.ui.showSnackbar('Erreur lors de l’activation', 'error')
    });
  }

  deactivate(id: string): void {
    this.adminService.deactivateUser(id).subscribe({
      next: () => {
        this.ui.showSnackbar('Utilisateur désactivé', 'success');
        this.loadUsers();
      },
      error: () => this.ui.showSnackbar('Erreur lors de la désactivation', 'error')
    });
  }

  async delete(id: string): Promise<void> {
    const ok = await this.confirmService.confirm('Confirmer la suppression de cet utilisateur ?');
    if (!ok) { return; }

    this.adminService.deleteUser(id).subscribe({
      next: () => {
        this.ui.showSnackbar('Utilisateur supprimé', 'success');
        this.loadUsers();
      },
      error: () => this.ui.showSnackbar('Erreur lors de la suppression', 'error')
    });
  }
}
