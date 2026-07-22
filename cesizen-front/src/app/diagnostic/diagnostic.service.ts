import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DiagnosticEvent {
  eventId: string;
  label: string;
  lcu: number;
}


export interface DiagnosticSubmitRequest {
  answers: Record<string, boolean>;
}

export interface DiagnosticResponse {
  surveyId: string;
  score: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  createdAt: string;
}

export interface DiagnosticHistory {
  surveyId: string;
  score: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class DiagnosticService {
  private readonly http = inject(HttpClient);

  getEvents(): Observable<DiagnosticEvent[]> {
    return this.http.get<DiagnosticEvent[]>(
      '/api/diagnostic/events'
    );
  }

  submitDiagnostic(
    payload: DiagnosticSubmitRequest
  ): Observable<DiagnosticResponse> {
    return this.http.post<DiagnosticResponse>(
      '/api/diagnostic/submit',
      payload
    );
  }

  getHistory(): Observable<DiagnosticHistory[]> {
    return this.http.get<DiagnosticHistory[]>(
      '/api/diagnostic/history/me'
    );
  }
}
