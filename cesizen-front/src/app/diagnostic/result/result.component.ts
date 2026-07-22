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
  DiagnosticResponse,
  DiagnosticService
} from '../diagnostic.service';
import { UiStore } from '../../core/stores/ui.store';

type RiskLevel = DiagnosticResponse['riskLevel'];

interface ResultPresentation {
  label: 'Faible' | 'Modéré' | 'Élevé';
  emoji: string;
  cssClass:
    | 'risk-box--low'
    | 'risk-box--medium'
    | 'risk-box--high';
  interpretation: string;
  adviceItems: string[];
}

@Component({
  selector: 'app-diagnostic-result',
  standalone: true,
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.scss']
})
export class ResultComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly diagnosticService =
    inject(DiagnosticService);
  private readonly ui = inject(UiStore);

  readonly score = signal<number | null>(null);
  readonly riskLevel = signal<RiskLevel | null>(null);
  readonly loading = signal(true);

  readonly presentation = computed<ResultPresentation | null>(
    () => {
      const level = this.riskLevel();

      return level
        ? this.getPresentation(level)
        : null;
    }
  );

  ngOnInit(): void {
    const navigationState =
      window.history.state as Partial<DiagnosticResponse>;

    if (
      typeof navigationState.score === 'number' &&
      this.isRiskLevel(navigationState.riskLevel)
    ) {
      this.score.set(navigationState.score);
      this.riskLevel.set(navigationState.riskLevel);
      this.loading.set(false);
      return;
    }

    this.loadLatestHistory();
  }

  private loadLatestHistory(): void {
    this.loading.set(true);

    this.diagnosticService
      .getHistory()
      .pipe(
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: (history) => {
          const latest = history[0];

          if (!latest) {
            this.ui.showSnackbar(
              'Aucun résultat de diagnostic disponible',
              'info'
            );

            void this.router.navigate(['/diagnostic/list']);
            return;
          }

          this.score.set(latest.score);
          this.riskLevel.set(latest.riskLevel);
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors du chargement du résultat',
            'error'
          );

          void this.router.navigate(['/diagnostic/list']);
        }
      });
  }

  private isRiskLevel(
    value: unknown
  ): value is RiskLevel {
    return (
      value === 'LOW' ||
      value === 'MEDIUM' ||
      value === 'HIGH'
    );
  }

  private getPresentation(
    level: RiskLevel
  ): ResultPresentation {
    switch (level) {
      case 'LOW':
        return {
          label: 'Faible',
          emoji: '🙂',
          cssClass: 'risk-box--low',
          interpretation:
            'Niveau faible : stress léger.',
          adviceItems: [
            'Pratiquez deux minutes de respiration lente.',
            'Faites plusieurs micro-pauses dans la journée.',
            'Limitez-vous à trois priorités principales.'
          ]
        };

      case 'MEDIUM':
        return {
          label: 'Modéré',
          emoji: '😐',
          cssClass: 'risk-box--medium',
          interpretation:
            'Niveau modéré : stress notable.',
          adviceItems: [
            'Prévoyez dix minutes quotidiennes d’activité apaisante.',
            'Identifiez une tâche à déléguer ou à reporter.',
            'Conservez des horaires de sommeil réguliers.'
          ]
        };

      case 'HIGH':
        return {
          label: 'Élevé',
          emoji: '😟',
          cssClass: 'risk-box--high',
          interpretation:
            'Niveau élevé : stress intense.',
          adviceItems: [
            'Réduisez les obligations non essentielles.',
            'Pratiquez une technique de respiration ou d’ancrage.',
            'Parlez-en à une personne de confiance.',
            'Accordez-vous un véritable temps de récupération.'
          ]
        };
    }
  }

  restart(): void {
    void this.router.navigate(['/diagnostic/run']);
  }
}
