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

type ApplicationRole =
  | 'USER'
  | 'ADMIN';

export const roleGuard: CanActivateFn = route => {
  const userStore = inject(UserStore);
  const profileService = inject(ProfileService);
  const router = inject(Router);

  const expectedRoles =
    route.data?.['roles'] as
      ApplicationRole[] | undefined;

  if (!userStore.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  const hasExpectedRole = (
    roleName: string | undefined
  ): boolean => {
    return !expectedRoles?.length
      || (
        roleName !== undefined
        && expectedRoles.includes(
          roleName as ApplicationRole
        )
      );
  };

  const cachedUser = userStore.user();

  if (cachedUser) {
    return hasExpectedRole(
      cachedUser.role?.roleName
    )
      ? true
      : router.createUrlTree([
          '/informations/list'
        ]);
  }

  return profileService.getCurrentUser().pipe(
    map(user => {
      userStore.setUser(user);

      return hasExpectedRole(
        user.role?.roleName
      )
        ? true
        : router.createUrlTree([
            '/informations/list'
          ]);
    }),

    catchError(() => {
      userStore.clear();

      return of(
        router.createUrlTree(['/login'])
      );
    })
  );
};
