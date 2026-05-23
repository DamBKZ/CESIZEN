import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

export const RoleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data?.['roles'] as string[];

  const user = authService.getCurrentUser();

  if (!user) {
    router.navigate(['/auth/login']);
    return false;
  }

  if (!expectedRoles || expectedRoles.length === 0) {
    return true;
  }

  if (expectedRoles.includes(user.role)) {
    return true;
  }

  router.navigate(['/auth/login']);
  return false;
};
