import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export type InformationType = 'ARTICLE' | 'VIDEO' | 'PDF';

export interface Information {
  informationId: string;
  title: string;
  type: InformationType;
  author: string;
  slug: string;
  categoryId: string;
  categoryName: string;
  tags: string[];
  createdAt: string;
  ownerId?: string;
  ownerPseudo?: string;
  content?: string;
  videoUrl?: string;
  pdfUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class InformationService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Information[]> {
    return this.http.get<Information[]>('/api/information');
  }

  search(params: { keyword?: string }): Observable<Information[]> {
    let httpParams = new HttpParams();

    if (params.keyword?.trim()) {
      httpParams = httpParams.set('keyword', params.keyword.trim());
    }

    return this.http.get<Information[]>('/api/information/search', {
      params: httpParams
    });
  }

  getById(identifier: string | number): Observable<Information> {
    return this.http.get<Information>(`/api/information/${identifier}`);
  }

  create(payload: any): Observable<Information> {
    return this.http.post<Information>('/api/information', payload);
  }

  update(identifier: string | number, payload: any): Observable<Information> {
    return this.http.put<Information>(`/api/information/${identifier}`, payload);
  }

  delete(identifier: string | number): Observable<void> {
    return this.http.delete<void>(`/api/information/${identifier}`);
  }
}
