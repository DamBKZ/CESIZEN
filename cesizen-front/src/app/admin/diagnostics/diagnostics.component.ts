import { DatePipe } from '@angular/common';
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
import { AdminDiagnostic } from '../models/diagnostic-admin.model';
import { UiStore } from '../../core/stores/ui.store';
import { ConfirmService } from '../../shared/services/confirm.service';

@Component({
  selector: 'app-admin-diagnostics',
  standalone: true,
  imports: [
    DatePipe,
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

  readonly displayedColumns = [
    'user',
    'score',
    'risk',
    'date',
    'actions'
  ];

  readonly diagnostics = signal<AdminDiagnostic[]>([]);
  readonly loading = signal(true);
  readonly processingDiagnosticId =
    signal<string | null>(null);

  ngOnInit(): void {
    this.loadDiagnostics();
  }

  private loadDiagnostics(): void {
    this.loading.set(true);

    this.adminService
      .getAllDiagnostics()
      .pipe(
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: (diagnostics) => {
          this.diagnostics.set(diagnostics);
        },
        error: () => {
          this.diagnostics.set([]);

          this.ui.showSnackbar(
            'Erreur lors du chargement des diagnostics',
            'error'
          );
        }
      });
  }

  async deleteDiagnostic(
    surveyId: string
  ): Promise<void> {
    if (this.processingDiagnosticId()) {
      return;
    }

    const confirmed = await this.confirmService.confirm(
      'Confirmer la suppression de ce diagnostic ?'
    );

    if (!confirmed) {
      return;
    }

    this.processingDiagnosticId.set(surveyId);

    this.adminService
      .deleteDiagnostic(surveyId)
      .pipe(
        finalize(() =>
          this.processingDiagnosticId.set(null)
        )
      )
      .subscribe({
        next: () => {
          this.diagnostics.update((diagnostics) =>
            diagnostics.filter(
              (diagnostic) =>
                diagnostic.surveyId !== surveyId
            )
          );

          this.ui.showSnackbar(
            'Diagnostic supprimé',
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
}
