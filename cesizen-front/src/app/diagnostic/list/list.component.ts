import { Component, inject, signal } from '@angular/core';
import { DiagnosticService } from '../diagnostic.service';
import { UiStore } from '../../core/stores/ui.store';
import { Router } from '@angular/router';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [NgClass],
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent {
  private readonly diagnosticService = inject(DiagnosticService);
  private readonly ui = inject(UiStore);
  private readonly router = inject(Router);

  events = signal<any[]>([]);
  selected = signal<Set<number>>(new Set());

  constructor() {
    this.loadEvents();
  }

  loadEvents(): void {
    this.ui.setLoading(true);

    this.diagnosticService.getEvents().subscribe({
      next: (res: any) => {
        this.events.set(res);
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors du chargement des événements', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  toggleEvent(id: number): void {
    const set = new Set(this.selected());
    set.has(id) ? set.delete(id) : set.add(id);
    this.selected.set(set);
  }

  startDiagnostic(): void {
    if (this.selected().size === 0) {
      this.ui.showSnackbar('Sélectionnez au moins un événement', 'info');
      return;
    }

    this.router.navigate(['/diagnostic/run'], {
      state: { selectedEvents: Array.from(this.selected()) }
    });
  }
}
