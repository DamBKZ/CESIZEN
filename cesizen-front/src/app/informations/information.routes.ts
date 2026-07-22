import { Routes } from '@angular/router';

import {
  authGuard
} from '../core/guards/auth.guard';

export const INFORMATION_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'list'
  },
  {
    path: 'list',
    loadComponent: () =>
      import('./list/list.component')
        .then(module => module.ListComponent)
  },
  {
    path: 'create',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./create/create.component')
        .then(module => module.CreateComponent)
  },
  {
    path: 'details/:slug',
    loadComponent: () =>
      import('./details/details.component')
        .then(module => module.DetailsComponent)
  },
  {
    path: 'edit/:slug',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./edit/edit.component')
        .then(module => module.EditComponent)
  }
];
