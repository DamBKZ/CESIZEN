import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../admin.service';
import { ConfirmService } from '../../shared/services/confirm.service';
import { UiStore } from '../../core/stores/ui.store';
import { Router } from '@angular/router';
import { AdminInformation } from '../models/information-admin.model';

@Component({
  selector: 'app-admin-informations',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './informations.component.html',
  styleUrls: ['./informations.component.scss']
})
export class InformationsComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly confirmService = inject(ConfirmService);
  private readonly router = inject(Router);
  private readonly ui = inject(UiStore);

  displayedColumns = ['title', 'type', 'category', 'createdAt', 'actions'];
  informations: AdminInformation[] = [];

  ngOnInit(): void {
    this.loadInformations();
  }

  loadInformations(): void {
    this.adminService.getAllInformations().subscribe({
      next: data => this.informations = data,
      error: () => this.ui.showSnackbar('Erreur chargement informations', 'error')
    });
  }

  async delete(id: string): Promise<void> {
    const ok = await this.confirmService.confirm('Confirmer la suppression de cette information ?');

    if (!ok) {
      return;
    }

    this.adminService.deleteInformation(id).subscribe({
      next: () => {
        this.ui.showSnackbar('Information supprimée', 'success');
        this.loadInformations();
      },
      error: () => this.ui.showSnackbar('Erreur lors de la suppression', 'error')
    });
  }

  edit(slug: string): void {
    this.router.navigate(['/informations/edit', slug]);
  }
}
