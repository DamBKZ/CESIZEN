import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, of } from 'rxjs';
import { DIAGNOSTIC_EVENTS_MOCK } from './diagnostic.mock';

@Injectable({ providedIn: 'root' })
export class DiagnosticService {
  private readonly http = inject(HttpClient);

  getEvents() {
    return this.http.get('/api/diagnostic/events').pipe(
      catchError(() => of(DIAGNOSTIC_EVENTS_MOCK))
    );
  }

  submitDiagnostic(payload: any) {
    return this.http.post('/api/diagnostic/submit', payload);
  }

  getHistory() {
    return this.http.get<any[]>('/api/diagnostic/history/me');
  }
}
