import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserStore } from '../stores/user.store';

export const adminGuard: CanActivateFn = () => {
  const userStore = inject(UserStore);
  const router = inject(Router);

  // 1) Vérifie si l'utilisateur est connecté
  if (!userStore.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  // 2) Vérifie le rôle ADMIN
  if (userStore.role() !== 'ADMIN') {
    router.navigate(['/forbidden']);
    return false;
  }

  return true;
};
