import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../admin.service';
import { AdminDiagnostic } from '../models/diagnostic-admin.model';
import { UiStore } from '../../core/stores/ui.store';
import { ConfirmService } from '../../shared/services/confirm.service';

@Component({
  selector: 'app-admin-diagnostics',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './diagnostics.component.html',
  styleUrls: ['./diagnostics.component.scss']
})
export class DiagnosticsComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly ui = inject(UiStore);
  private readonly confirmService = inject(ConfirmService);

  displayedColumns = ['user', 'score', 'risk', 'date', 'actions'];
  diagnostics: AdminDiagnostic[] = [];

  ngOnInit(): void {
    this.loadDiagnostics();
  }

  loadDiagnostics(): void {
    this.adminService.getAllDiagnostics().subscribe({
      next: data => this.diagnostics = data,
      error: () => this.ui.showSnackbar('Erreur chargement diagnostics', 'error')
    });
  }

  async delete(id: string): Promise<void> {
    const ok = await this.confirmService.confirm('Confirmer la suppression de ce diagnostic ?');

    if (!ok) {
      return;
    }

    this.adminService.deleteDiagnostic(id).subscribe({
      next: () => {
        this.ui.showSnackbar('Diagnostic supprimé', 'success');
        this.loadDiagnostics();
      },
      error: () => this.ui.showSnackbar('Erreur lors de la suppression', 'error')
    });
  }
}
