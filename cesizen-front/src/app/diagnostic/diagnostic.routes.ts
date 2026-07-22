import { Routes } from '@angular/router';

export const DIAGNOSTIC_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'list'
  },
  {
    path: 'list',
    loadComponent: () =>
      import('./list/list.component').then(m => m.ListComponent)
  },
  {
    path: 'run',
    loadComponent: () =>
      import('./run/run.component').then(m => m.RunComponent)
  },
  {
    path: 'result',
    loadComponent: () =>
      import('./result/result.component').then(m => m.ResultComponent)
  }
];
