import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/components/layout/layout.component';

export const APP_ROUTES: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      // AUTH
      {
        path: 'auth',
        loadChildren: () =>
          import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
      },

      // PROFILE
      {
        path: 'profile',
        loadChildren: () =>
          import('./profile/profile.routes').then(m => m.PROFILE_ROUTES)
      },

      // DIAGNOSTIC
      {
        path: 'diagnostic',
        loadChildren: () =>
          import('./diagnostic/diagnostic.routes').then(m => m.DIAGNOSTIC_ROUTES)
      },

      // INFORMATIONS
      {
        path: 'informations',
        loadChildren: () =>
          import('./informations/information.routes').then(m => m.INFORMATION_ROUTES)
      },

      // LOGS (côté utilisateur)
      {
        path: 'logs',
        loadChildren: () =>
          import('./logs/logs.routes').then(m => m.LOGS_ROUTES)
      },

      // ADMIN
      {
        path: 'admin',
        loadChildren: () =>
          import('./admin/admin.routes').then(m => m.ADMIN_ROUTES)
      },

      // DEFAULT REDIRECT
      {
        path: '',
        redirectTo: 'informations',
        pathMatch: 'full'
      }
    ]
  },

  // 404 fallback
  {
    path: '**',
    redirectTo: ''
  }
];
