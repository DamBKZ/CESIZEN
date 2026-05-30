import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UiStore } from '../core/stores/ui.store';
import { catchError, of } from 'rxjs';
import { DIAGNOSTIC_EVENTS_MOCK } from './diagnostic.mock';

@Injectable({ providedIn: 'root' })
export class DiagnosticService {
  private readonly http = inject(HttpClient);
  private readonly ui = inject(UiStore);

  getEvents() {
    return this.http.get('/api/diagnostic/events').pipe(
      catchError(() => of(DIAGNOSTIC_EVENTS_MOCK))
    );
  }

  submitDiagnostic(payload: any) {
    this.ui.setLoading(true);

    return this.http.post('/api/diagnostic/submit', payload).subscribe({
      next: () => {
        this.ui.showSnackbar('Diagnostic enregistré', 'success');
      },
      error: () => {
        this.ui.showSnackbar('Erreur lors de la soumission', 'error');
      },
      complete: () => this.ui.setLoading(false)
    });
  }

  getHistory(userId: string) {
    return this.http.get(`/api/diagnostic/history/${userId}`);
  }
}
