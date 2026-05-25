import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/components/layout/layout.component';

export const APP_ROUTES: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: 'auth',
        loadChildren: () =>
          import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
      },

      {
        path: 'profile',
        loadChildren: () =>
          import('./profile/profile.routes').then(m => m.PROFILE_ROUTES)
      },

      {
        path: 'diagnostic',
        loadChildren: () =>
          import('./diagnostic/diagnostic.routes').then(m => m.DIAGNOSTIC_ROUTES)
      },

      {
        path: 'informations',
        loadChildren: () =>
          import('./informations/information.routes').then(m => m.INFORMATION_ROUTES)
      },

      {
        path: 'logs',
        loadChildren: () =>
          import('./logs/logs.routes').then(m => m.LOGS_ROUTES)
      },

      {
        path: 'admin',
        loadChildren: () =>
          import('./admin/admin.routes').then(m => m.ADMIN_ROUTES)
      },

      {
        path: '',
        redirectTo: 'informations',
        pathMatch: 'full'
      }
    ]
  },

{
  path: '**',
  loadComponent: () =>
    import('./not-found/not-found.component').then(m => m.NotFoundComponent)
}
];
