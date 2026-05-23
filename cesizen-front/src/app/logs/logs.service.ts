import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class LogsService {
  private http = inject(HttpClient);

  getLogsForUser(userId: string) {
    return this.http.get<any[]>(`/api/logs/user/${userId}`);
  }
}
