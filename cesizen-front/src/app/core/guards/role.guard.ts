import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserStore } from '../stores/user.store';
import { ProfileService } from '../services/profile.service';
import { catchError, map, of } from 'rxjs';

export const roleGuard: CanActivateFn = (route) => {
  const userStore = inject(UserStore);
  const profileService = inject(ProfileService);
  const router = inject(Router);

  const expectedRoles = route.data?.['roles'] as string[] | undefined;

  if (!userStore.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const cachedUser = userStore.user();

  if (cachedUser) {
    if (!expectedRoles?.length || expectedRoles.includes(cachedUser.role?.roleName)) {
      return true;
    }

    router.navigate(['/login']);
    return false;
  }

  return profileService.getCurrentUser().pipe(
    map((currentUser: any) => {
      userStore.setUser(currentUser);

      if (!expectedRoles?.length || expectedRoles.includes(currentUser.role?.roleName)) {
        return true;
      }

      router.navigate(['/login']);
      return false;
    }),
    catchError(() => {
      userStore.clear();
      router.navigate(['/login']);
      return of(false);
    })
  );
};
