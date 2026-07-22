import {

  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';
import { DatePipe } from '@angular/common';

import { AdminService } from '../admin.service';
import { AdminInformation } from '../models/information-admin.model';
import { UiStore } from '../../core/stores/ui.store';
import { ConfirmService } from '../../shared/services/confirm.service';

@Component({
  selector: 'app-admin-informations',
  standalone: true,
  imports: [
    DatePipe,
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

  readonly displayedColumns = [
    'title',
    'type',
    'category',
    'createdAt',
    'actions'
  ];

  readonly informations = signal<AdminInformation[]>([]);
  readonly loading = signal(true);
  readonly processingInformationId =
    signal<string | null>(null);

  ngOnInit(): void {
    this.loadInformations();
  }

  private loadInformations(): void {
    this.loading.set(true);

    this.adminService
      .getAllInformations()
      .pipe(
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: (informations) => {
          this.informations.set(informations);
        },
        error: () => {
          this.informations.set([]);

          this.ui.showSnackbar(
            'Erreur lors du chargement des informations',
            'error'
          );
        }
      });
  }

  async deleteInformation(
    informationId: string
  ): Promise<void> {
    if (this.processingInformationId()) {
      return;
    }

    const confirmed = await this.confirmService.confirm(
      'Confirmer la suppression de cette information ?'
    );

    if (!confirmed) {
      return;
    }

    this.processingInformationId.set(informationId);

    this.adminService
      .deleteInformation(informationId)
      .pipe(
        finalize(() =>
          this.processingInformationId.set(null)
        )
      )
      .subscribe({
        next: () => {
          this.informations.update((informations) =>
            informations.filter(
              (information) =>
                information.informationId !== informationId
            )
          );

          this.ui.showSnackbar(
            'Information supprimée',
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

  edit(slug: string): void {
    void this.router.navigate([
      '/informations/edit',
      slug
    ]);
  }
}
