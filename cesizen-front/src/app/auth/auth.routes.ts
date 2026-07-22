import { Routes } from '@angular/router';

export const AUTH_ROUTES: Routes = [
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./forgot-password/forgot-password.component').then(
        (module) => module.ForgotPasswordComponent
      )
  },
  {
    path: 'reset-password/:token',
    loadComponent: () =>
      import('./reset-password/reset-password.component').then(
        (module) => module.ResetPasswordComponent
      )
  }
];
