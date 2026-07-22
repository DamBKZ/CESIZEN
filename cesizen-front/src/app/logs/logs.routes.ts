import { Routes } from '@angular/router';

export const LOGS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./list/list.component').then(m => m.ListComponent)
  }
];
