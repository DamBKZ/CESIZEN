import { DatePipe } from '@angular/common';
import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';

import { AdminService } from '../admin.service';
import { AdminLog } from '../models/log-admin.model';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-admin-logs',
  standalone: true,
  imports: [
    DatePipe,
    MatTableModule
  ],
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.scss']
})
export class LogsComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly ui = inject(UiStore);

  readonly displayedColumns = [
    'user',
    'content',
    'date'
  ];

  readonly logs = signal<AdminLog[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.loadLogs();
  }

  private loadLogs(): void {
    this.loading.set(true);

    this.adminService
      .getAllLogs()
      .pipe(
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: (logs) => {
          this.logs.set(logs);
        },
        error: () => {
          this.logs.set([]);

          this.ui.showSnackbar(
            'Erreur lors du chargement des logs',
            'error'
          );
        }
      });
  }
}
