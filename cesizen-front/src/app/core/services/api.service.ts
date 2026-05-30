import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ApiService {
  url(path: string): string {
    if (!path.startsWith('/')) path = `/${path}`;
    return path;
  }
}
