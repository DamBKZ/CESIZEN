import { Routes } from '@angular/router';

export const INFORMATION_ROUTES: Routes = [
  {
    path: 'list',
    loadComponent: () =>
      import('./list/list.component').then(m => m.ListComponent)
  },
  {
    path: 'create',
    loadComponent: () =>
      import('./create/create.component').then(m => m.CreateComponent)
  },
  {
    path: 'details/:id',
    loadComponent: () =>
      import('./details/details.component').then(m => m.DetailsComponent)
  },
{
  path: 'edit/:id',
  loadComponent: () =>
    import('./edit/edit.component').then(m => m.EditComponent)
}

];
