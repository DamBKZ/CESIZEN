import { Routes } from '@angular/router';
import { RoleGuard } from '../core/guards/role.guard';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN'] },
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./users/users.component').then(m => m.UsersComponent)
      },
      {
        path: 'informations',
        loadComponent: () =>
          import('./informations/informations.component').then(m => m.InformationsComponent)
      },
      {
        path: 'diagnostics',
        loadComponent: () =>
          import('./diagnostics/diagnostics.component').then(m => m.DiagnosticsComponent)
      },
      {
        path: 'logs',
        loadComponent: () =>
          import('./logs/logs.component').then(m => m.LogsComponent)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  }
];
