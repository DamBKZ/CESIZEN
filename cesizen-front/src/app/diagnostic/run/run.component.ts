import {
  Component,
  computed,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import {
  DiagnosticEvent,
  DiagnosticService
} from '../diagnostic.service';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-diagnostic-run',
  standalone: true,
  templateUrl: './run.component.html',
  styleUrls: ['./run.component.scss']
})
export class RunComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly diagnosticService =
    inject(DiagnosticService);
  private readonly ui = inject(UiStore);

  readonly events = signal<DiagnosticEvent[]>([]);
  readonly selectedEvents = signal<Set<string>>(new Set());

  readonly loadingEvents = signal(true);
  readonly submitting = signal(false);

  readonly totalScore = computed(() => {
    const selectedIds = this.selectedEvents();

    return this.events()
      .filter((event) => selectedIds.has(event.eventId))
      .reduce((total, event) => total + event.lcu, 0);
  });

  readonly riskLevel = computed<
    'LOW' | 'MEDIUM' | 'HIGH'
  >(() => this.computeRiskLevel(this.totalScore()));

  readonly canSubmit = computed(() =>
    this.selectedEvents().size > 0 &&
    !this.loadingEvents() &&
    !this.submitting()
  );

  ngOnInit(): void {
    this.loadEvents();
  }

  private loadEvents(): void {
    this.loadingEvents.set(true);

    this.diagnosticService
      .getEvents()
      .pipe(
        finalize(() => this.loadingEvents.set(false))
      )
      .subscribe({
        next: (events) => {
          this.events.set(events);
        },
        error: () => {
          this.events.set([]);

          this.ui.showSnackbar(
            'Erreur lors du chargement du questionnaire',
            'error'
          );
        }
      });
  }

  toggleEvent(eventId: string): void {
    const nextSelection =
      new Set(this.selectedEvents());

    if (nextSelection.has(eventId)) {
      nextSelection.delete(eventId);
    } else {
      nextSelection.add(eventId);
    }

    this.selectedEvents.set(nextSelection);
  }

  private computeRiskLevel(
    score: number
  ): 'LOW' | 'MEDIUM' | 'HIGH' {
    if (score < 150) {
      return 'LOW';
    }

    if (score < 300) {
      return 'MEDIUM';
    }

    return 'HIGH';
  }

  submit(): void {
    if (!this.canSubmit()) {
      this.ui.showSnackbar(
        'Sélectionnez au moins un événement pour valider le diagnostic',
        'error'
      );

      return;
    }

    const selectedIds = this.selectedEvents();

    const answers = this.events().reduce<
      Record<string, boolean>
    >((result, event) => {
      result[event.eventId] =
        selectedIds.has(event.eventId);

      return result;
    }, {});

    this.submitting.set(true);

    this.diagnosticService
      .submitDiagnostic({ answers })
      .pipe(
        finalize(() => this.submitting.set(false))
      )
      .subscribe({
        next: (response) => {
          this.ui.showSnackbar(
            'Diagnostic enregistré',
            'success'
          );

          void this.router.navigate(
            ['/diagnostic/result'],
            {
              state: response
            }
          );
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors de la soumission',
            'error'
          );
        }
      });
  }
}
