import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { DiagnosticService } from '../diagnostic.service';
import { UserStore } from '../../core/stores/user.store';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-result',
  standalone: true,
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.scss']
})
export class ResultComponent {
  private readonly router = inject(Router);
  private readonly diagnosticService = inject(DiagnosticService);
  private readonly userStore = inject(UserStore);
  private readonly ui = inject(UiStore);

  score = signal<number | null>(null);
  interpretation = signal<string>('');

  constructor() {
    const state = history.state;

    if (state && state.score !== undefined) {
      this.score.set(state.score);
      this.computeInterpretation(state.score);
    } else {
      this.loadLatestHistory();
    }
  }

  loadLatestHistory(): void {
    const user = this.userStore.user();

    if (!user) {
      this.ui.showSnackbar('Utilisateur non connecté', 'error');
      this.router.navigate(['/auth/login']);
      return;
    }

    this.ui.setLoading(true);

    this.diagnosticService.getHistory(user.userId).subscribe({
      next: (res: any) => {
        if (res && res.length > 0) {
          const last = res[0];
          this.score.set(last.score);
          this.computeInterpretation(last.score);
        }
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors du chargement du résultat', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  computeInterpretation(score: number): void {
    if (score < 150) {
      this.interpretation.set("Risque faible : stress maîtrisé.");
    } else if (score < 300) {
      this.interpretation.set("Risque modéré : attention à votre équilibre.");
    } else {
      this.interpretation.set("Risque élevé : surcharge de stress probable.");
    }
  }

  restart(): void {
    this.router.navigate(['/diagnostic/list']);
  }
}
