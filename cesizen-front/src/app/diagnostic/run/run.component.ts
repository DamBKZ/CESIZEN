import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { DiagnosticService } from '../diagnostic.service';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-run',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './run.component.html',
  styleUrls: ['./run.component.scss']
})
export class RunComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly diagnosticService = inject(DiagnosticService);
  private readonly ui = inject(UiStore);

  events = signal<any[]>([]);
  selectedEvents = signal<Set<string>>(new Set());
  totalScore = signal(0);
  riskLevel = signal<'LOW' | 'MEDIUM' | 'HIGH'>('LOW');
  loadingEvents = signal(true);

  ngOnInit(): void {
    this.loadEvents();
  }

  normalizeId(eventId: string | number): string {
    return String(eventId);
  }

  loadEvents(): void {
    this.ui.setLoading(true);

    this.diagnosticService.getEvents().subscribe({
      next: (events: any) => {
        this.events.set(events);
        this.loadingEvents.set(false);
        this.computeScore();
      },
      error: () => {
        this.loadingEvents.set(false);
        this.ui.showSnackbar('Erreur lors du chargement du questionnaire', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  toggleEvent(eventId: string | number): void {
    const normalizedId = String(eventId);
    const nextSelection = new Set(this.selectedEvents());

    if (nextSelection.has(normalizedId)) {
      nextSelection.delete(normalizedId);
    } else {
      nextSelection.add(normalizedId);
    }

    this.selectedEvents.set(nextSelection);
    this.computeScore();
  }

  computeScore(): void {
    const selectedIds = this.selectedEvents();
    const score = this.events()
      .filter((event: any) => selectedIds.has(this.normalizeId(event.eventId)))
      .reduce((sum: number, event: any) => sum + Number(event.lcu ?? 0), 0);

    this.totalScore.set(score);
    this.riskLevel.set(this.computeRiskLevel(score));
  }

  computeRiskLevel(score: number): 'LOW' | 'MEDIUM' | 'HIGH' {
    if (score < 150) {
      return 'LOW';
    }

    if (score < 300) {
      return 'MEDIUM';
    }

    return 'HIGH';
  }

  canSubmit(): boolean {
    return this.selectedEvents().size > 0;
  }

  submit(): void {
    if (!this.canSubmit()) {
      this.ui.showSnackbar('Sélectionnez au moins un événement pour valider le diagnostic', 'error');
      return;
    }

    const selectedIds = this.selectedEvents();

    const answers = this.events().reduce((acc: Record<string, boolean>, event: any) => {
      acc[this.normalizeId(event.eventId)] = selectedIds.has(this.normalizeId(event.eventId));
      return acc;
    }, {});

    this.ui.setLoading(true);

    this.diagnosticService.submitDiagnostic({ answers }).subscribe({
      next: (res: any) => {
        this.ui.showSnackbar('Diagnostic enregistré', 'success');

        this.router.navigate(['/diagnostic/result'], {
          state: {
            score: res.score,
            riskLevel: res.riskLevel,
            createdAt: res.createdAt,
            surveyId: res.surveyId
          }
        });
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors de la soumission', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }
}
