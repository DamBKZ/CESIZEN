import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UiStore } from '../core/stores/ui.store';

@Injectable({ providedIn: 'root' })
export class InformationService {
  private readonly http = inject(HttpClient);
  private readonly ui = inject(UiStore);

  getAll() {
    return this.http.get('/api/information');
  }

search(params: any) {
  return this.http.get('/api/information/search', { params });
}

  getById(id: number) {
    return this.http.get(`/api/information/${id}`);
  }

  update(id: number, payload: any) {
  return this.http.put(`/api/information/${id}`, payload);
}


create(payload: any) {
  this.ui.setLoading(true);

  return this.http.post('/api/information', payload);
}

}
