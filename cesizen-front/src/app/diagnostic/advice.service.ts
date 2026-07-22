import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdviceService {

  private http = inject(HttpClient);

  getByLevel(level: string): Observable<string[]> {
    return this.http.get<string[]>(`/api/advice/${level}`);
  }
}
