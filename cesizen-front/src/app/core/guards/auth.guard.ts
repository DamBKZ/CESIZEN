import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserStore } from '../stores/user.store';

export const authGuard: CanActivateFn = () => {
  const userStore = inject(UserStore);
  const router = inject(Router);

  // Si pas authentifié → redirection login
  if (!userStore.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
