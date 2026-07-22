import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import {
  DiagnosticHistory,
  ProfileService
} from '../../core/services/profile.service';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-profile-history',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);
  private readonly ui = inject(UiStore);

  readonly history = signal<DiagnosticHistory[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.loadHistory();
  }

  private loadHistory(): void {
    this.loading.set(true);

    this.profileService
      .getHistory()
      .pipe(
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: (history) => {
          this.history.set(history);
        },
        error: () => {
          this.history.set([]);

          this.ui.showSnackbar(
            'Erreur lors du chargement de l’historique',
            'error'
          );
        }
      });
  }

  backToProfile(): void {
    void this.router.navigate(['/profile']);
  }
}
