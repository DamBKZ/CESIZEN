import { inject, Injectable } from '@angular/core';
import {
  HttpClient,
  HttpParams
} from '@angular/common/http';
import {
  map,
  Observable
} from 'rxjs';

export type InformationType =
  | 'ARTICLE'
  | 'VIDEO'
  | 'PDF';

export interface InformationCategory {
  categoryId: string;
  name: string;
  description?: string;
  createdAt?: string;
}

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

export interface InformationRequest {
  type: InformationType;
  title: string;
  author?: string;
  slug: string;
  tags: string[];
  categoryId: string;
  content?: string;
  videoUrl?: string;
  pdfUrl?: string;
}


export interface InformationPage {
  content: Information[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
  numberOfElements: number;
}

@Injectable({
  providedIn: 'root'
})
export class InformationService {
  private readonly http = inject(HttpClient);

  /**
   * Récupère les informations depuis la réponse paginée du backend,
   * puis renvoie uniquement le tableau contenu dans `content`.
   */
  getAll(): Observable<Information[]> {
    return this.http
      .get<InformationPage>('/api/information')
      .pipe(
        map(response =>
          Array.isArray(response?.content)
            ? response.content
            : []
        )
      );
  }

  getBySlug(slug: string): Observable<Information> {
  return this.http.get<Information>(
    `/api/information/slug/${encodeURIComponent(slug)}`
  );
}


  /**
   * Recherche des informations et extrait le contenu de la page.
   */
  search(
    params: {
      keyword?: string;
    }
  ): Observable<Information[]> {
    let httpParams = new HttpParams();

    const keyword = params.keyword?.trim();

    if (keyword) {
      httpParams = httpParams.set(
        'keyword',
        keyword
      );
    }

    return this.http
      .get<InformationPage>(
        '/api/information/search',
        {
          params: httpParams
        }
      )
      .pipe(
        map(response =>
          Array.isArray(response?.content)
            ? response.content
            : []
        )
      );
  }

  /**
   * Récupère une information par son UUID.
   */
  getById(
    identifier: string
  ): Observable<Information> {
    return this.http.get<Information>(
      `/api/information/${encodeURIComponent(identifier)}`
    );
  }

  /**
   * Récupère les catégories disponibles.
   */
  getCategories(): Observable<InformationCategory[]> {
    return this.http
      .get<InformationCategory[]>('/api/category')
      .pipe(
        map(response =>
          Array.isArray(response)
            ? response
            : []
        )
      );
  }

  /**
   * Crée une information.
   */
  create(
  payload: InformationRequest
): Observable<Information> {
  return this.http.post<Information>(
    '/api/information',
    payload
  );
}


  /**
   * Met à jour une information.
   */
update(
  identifier: string,
  payload: InformationRequest
): Observable<Information> {
  return this.http.put<Information>(
    `/api/information/${encodeURIComponent(identifier)}`,
    payload
  );
}


  /**
   * Supprime une information.
   */
  delete(
    identifier: string
  ): Observable<void> {
    return this.http.delete<void>(
      `/api/information/${encodeURIComponent(identifier)}`
    );
  }
}
