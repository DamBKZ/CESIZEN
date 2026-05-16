import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { DiagnosticService } from '../diagnostic.service';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-run',
  standalone: true,
  templateUrl: './run.component.html',
  styleUrls: ['./run.component.scss']
})
export class RunComponent {
  private readonly router = inject(Router);
  private readonly diagnosticService = inject(DiagnosticService);
  private readonly ui = inject(UiStore);

  selectedEvents = signal<any[]>([]);
  totalScore = signal(0);

  constructor() {
    const state = history.state;

    if (!state || !state.selectedEvents) {
      this.ui.showSnackbar('Aucun événement sélectionné', 'info');
      this.router.navigate(['/diagnostic/list']);
      return;
    }

    this.loadSelectedEvents(state.selectedEvents);
  }

  loadSelectedEvents(ids: number[]): void {
    this.ui.setLoading(true);

    this.diagnosticService.getEvents().subscribe({
      next: (events: any) => {
        const filtered = events.filter((e: any) => ids.includes(e.id));
        this.selectedEvents.set(filtered);
        this.computeScore();
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors du chargement', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  computeScore(): void {
    const score = this.selectedEvents().reduce((sum, e) => sum + e.score, 0);
    this.totalScore.set(score);
  }

  submit(): void {
    const payload = {
      events: this.selectedEvents().map(e => e.id),
      score: this.totalScore()
    };

    this.diagnosticService.submitDiagnostic(payload);

    this.router.navigate(['/diagnostic/result'], {
      state: { score: this.totalScore() }
    });
  }
}
