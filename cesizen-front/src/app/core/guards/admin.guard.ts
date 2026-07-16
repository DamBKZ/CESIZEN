import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserStore } from '../stores/user.store';
import { ProfileService } from '../services/profile.service';
import { catchError, map, of } from 'rxjs';

export const adminGuard: CanActivateFn = () => {
  const userStore = inject(UserStore);
  const profileService = inject(ProfileService);
  const router = inject(Router);

  if (!userStore.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const cachedUser = userStore.user();

  if (cachedUser) {
    if (cachedUser.role?.roleName === 'ADMIN') {
      return true;
    }

    router.navigate(['/login']);
    return false;
  }

  return profileService.getCurrentUser().pipe(
    map((user: any) => {
      userStore.setUser(user);

      if (user.role?.roleName === 'ADMIN') {
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
