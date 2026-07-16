import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { LayoutComponent } from './shared/components/layout/layout.component';

export const APP_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'informations'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'auth',
    loadChildren: () =>
      import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: 'profile',
        canActivate: [authGuard],
        loadChildren: () =>
          import('./profile/profile.routes').then(m => m.PROFILE_ROUTES)
      },
      {
        path: 'diagnostic',
        canActivate: [authGuard],
        loadChildren: () =>
          import('./diagnostic/diagnostic.routes').then(m => m.DIAGNOSTIC_ROUTES)
      },
      {
        path: 'informations',
        loadChildren: () =>
          import('./informations/information.routes').then(m => m.INFORMATION_ROUTES)
      },
      {
        path: 'information',
        redirectTo: 'informations',
        pathMatch: 'full'
      },
      {
        path: 'information/:rest',
        redirectTo: 'informations/:rest'
      },
      {
        path: 'admin',
        canActivate: [adminGuard],
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
