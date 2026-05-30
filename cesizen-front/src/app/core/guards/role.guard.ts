import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserStore } from '../stores/user.store';
import { ProfileService } from '../services/profile.service';
import { catchError, map, of } from 'rxjs';

export const RoleGuard: CanActivateFn = (route, state) => {
  const userStore = inject(UserStore);
  const profileService = inject(ProfileService);
  const router = inject(Router);

  const expectedRoles = route.data?.['roles'] as string[];

  const user = userStore.user();

  if (!user) {
    return profileService.getCurrentUser().pipe(
      map((currentUser: any) => {
        userStore.setUser(currentUser);

        if (!expectedRoles || expectedRoles.length === 0) {
          return true;
        }

        if (expectedRoles.includes(currentUser.role.roleName)) {
          return true;
        }

        router.navigate(['/login']);
        return false;
      }),
      catchError(() => {
        router.navigate(['/login']);
        return of(false);
      })
    );
  }

  if (!expectedRoles || expectedRoles.length === 0) {
    return true;
  }

  if (expectedRoles.includes(user.role.roleName)) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
