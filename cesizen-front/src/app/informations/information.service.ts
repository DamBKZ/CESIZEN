import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { INFORMATION_MOCKS, InformationMock } from './information.mock';

@Injectable({ providedIn: 'root' })
export class InformationService {
  private readonly http = inject(HttpClient);
  private informations: InformationMock[] = [...INFORMATION_MOCKS];

  getAll(): Observable<InformationMock[]> {
    return of([...this.informations]);
  }

  search(params: { keyword?: string }): Observable<InformationMock[]> {
    const keyword = (params.keyword ?? '').trim().toLowerCase();

    if (!keyword) {
      return of([]);
    }

    const results = this.informations.filter((information) => {
      const haystack = [
        information.title,
        information.author,
        information.slug,
        information.categoryName,
        information.content,
        ...(information.tags ?? [])
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      return haystack.includes(keyword);
    });

    return of(results);
}

  getById(identifier: string | number): Observable<InformationMock | null> {
    const normalized = String(identifier);
    return of(
      this.informations.find(
        (information) => information.informationId === normalized || information.slug === normalized
      ) ?? null
    );
  }

  update(identifier: string | number, payload: any): Observable<InformationMock> {
    const normalized = String(identifier);
    const index = this.informations.findIndex(
      (information) => information.informationId === normalized || information.slug === normalized
    );

    if (index === -1) {
      throw new Error('Information introuvable');
    }

    const current = this.informations[index];
    const updated: InformationMock = {
      ...current,
      ...payload,
      informationId: current.informationId,
      createdAt: current.createdAt,
      tags: payload.tags ?? current.tags,
      categoryName: payload.categoryName ?? current.categoryName
    };

    this.informations = this.informations.map((information) =>
      information.informationId === current.informationId ? updated : information
    );

    return of(updated);
  }

  create(payload: any): Observable<InformationMock> {
    const created: InformationMock = {
      informationId: `info-${Date.now()}`,
      title: payload.title,
      type: payload.type,
      author: payload.author,
      slug: payload.slug,
      categoryId: payload.categoryId,
      categoryName: payload.categoryName ?? 'Catégorie',
      tags: payload.tags ?? [],
      createdAt: new Date().toISOString(),
      content: payload.content,
      videoUrl: payload.videoUrl,
      pdfUrl: payload.pdfUrl
    };

    this.informations = [created, ...this.informations];

    return of(created);
  }

  delete(identifier: string | number): Observable<void> {
    const normalized = String(identifier);
    this.informations = this.informations.filter(
      (information) => information.informationId !== normalized && information.slug !== normalized
    );

    return of(void 0);
  }

}
