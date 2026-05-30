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

  private adminService = inject(AdminService);
  private confirmService = inject(ConfirmService);
  private router = inject(Router);
  private ui = inject(UiStore);

  displayedColumns = ['title', 'type', 'category', 'createdAt', 'actions'];
  informations: AdminInformation[] = [];

  ngOnInit(): void {
    this.loadInformations();
  }

  loadInformations(): void {
    this.adminService.getAllInformations().subscribe({
      next: data => this.informations = data,
      error: err => console.error('Erreur chargement informations', err)
    });
  }

  async delete(id: string): Promise<void> {
    const ok = await this.confirmService.confirm('Confirmer la suppression de cette information ?');
    if (!ok) { return; }

    this.adminService.deleteInformation(id).subscribe({
      next: () => {
        this.ui.showSnackbar('Information supprimée', 'success');
        this.loadInformations();
      },
      error: () => this.ui.showSnackbar('Erreur lors de la suppression', 'error')
    });
  }

  edit(id: string): void {
    this.router.navigate(['/informations/edit', id]);
  }
}
