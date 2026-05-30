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
  riskLevel = signal<'Faible' | 'Modéré' | 'Élevé' | ''>('');
  riskEmoji = signal<string>('');
  riskBoxClass = signal<'risk-box--low' | 'risk-box--medium' | 'risk-box--high' | ''>('');
  adviceItems = signal<string[]>([]);

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
      this.router.navigate(['/login']);
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
      this.riskLevel.set('Faible');
      this.riskEmoji.set('🙂');
      this.riskBoxClass.set('risk-box--low');
      this.interpretation.set('Niveau faible : stress léger.');
      this.adviceItems.set([
        'Respiration courte: 2 minutes de respiration lente (4-6).',
        'Micro-pauses: 3 pauses de 1 minute dans la journée.',
        'Organisation simple: noter 3 priorités maximum.'
      ]);
    } else if (score < 300) {
      this.riskLevel.set('Modéré');
      this.riskEmoji.set('😐');
      this.riskBoxClass.set('risk-box--medium');
      this.interpretation.set('Niveau modéré : stress notable.');
      this.adviceItems.set([
        "Routines anti-stress: 10 minutes par jour d'activité apaisante.",
        'Réduction de charge: identifier 1 tâche à déléguer ou reporter.',
        'Sommeil régulier: heure de coucher fixe et moins d’écrans.'
      ]);
    } else {
      this.riskLevel.set('Élevé');
      this.riskEmoji.set('😟');
      this.riskBoxClass.set('risk-box--high');
      this.interpretation.set('Niveau élevé : stress intense.');
      this.adviceItems.set([
        'Priorisation stricte: réduire au minimum les obligations non essentielles.',
        'Techniques d’ancrage: respiration profonde, cohérence cardiaque, ancrage 5-4-3-2-1.',
        'Soutien social: parler à un proche, collègue ou personne de confiance.',
        'Pause obligatoire: s’accorder un vrai temps de récupération.'
      ]);
    }
  }

  restart(): void {
    this.router.navigate(['/diagnostic/list']);
  }
}
