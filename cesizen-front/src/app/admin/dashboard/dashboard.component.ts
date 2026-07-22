import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

import { AdminService } from '../admin.service';
import { AdminStats } from '../models/stats-admin.model';
import { UiStore } from '../../core/stores/ui.store';

type StatKey = keyof AdminStats;

interface DashboardStat {
  key: StatKey;
  icon: string;
  label: string;
  value: number;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    MatCardModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly router = inject(Router);
  private readonly ui = inject(UiStore);

  readonly stats = signal<DashboardStat[]>([
    {
      key: 'users',
      icon: 'group',
      label: 'Utilisateurs',
      value: 0
    },
    {
      key: 'informations',
      icon: 'article',
      label: 'Informations',
      value: 0
    },
    {
      key: 'diagnostics',
      icon: 'psychology',
      label: 'Diagnostics',
      value: 0
    },
    {
      key: 'logs',
      icon: 'list_alt',
      label: 'Logs',
      value: 0
    }
  ]);

  readonly loading = signal(true);

  ngOnInit(): void {
    this.adminService
      .getStats()
      .pipe(
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: (data) => {
          this.stats.update((stats) =>
            stats.map((stat) => ({
              ...stat,
              value: data[stat.key] ?? 0
            }))
          );
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors du chargement des statistiques',
            'error'
          );
        }
      });
  }

  goToUsers(): void {
    void this.router.navigate(['/admin/users']);
  }

  goToInformations(): void {
    void this.router.navigate(['/admin/informations']);
  }
}
