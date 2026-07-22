import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';

import {
  catchError,
  map,
  of
} from 'rxjs';

import {
  ProfileService
} from '../services/profile.service';

import {
  UserStore
} from '../stores/user.store';

export const adminGuard: CanActivateFn = () => {
  const userStore = inject(UserStore);
  const profileService = inject(ProfileService);
  const router = inject(Router);

  if (!userStore.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  const cachedUser = userStore.user();

  if (cachedUser) {
    return cachedUser.role?.roleName === 'ADMIN'
      ? true
      : router.createUrlTree(['/informations/list']);
  }

  return profileService.getCurrentUser().pipe(
    map(user => {
      userStore.setUser(user);

      return user.role?.roleName === 'ADMIN'
        ? true
        : router.createUrlTree(['/informations/list']);
    }),

    catchError(() => {
      userStore.clear();

      return of(
        router.createUrlTree(['/login'])
      );
    })
  );
};
