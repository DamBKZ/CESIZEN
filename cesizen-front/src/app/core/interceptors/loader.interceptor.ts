import {
  HttpInterceptorFn
} from '@angular/common/http';

import { inject } from '@angular/core';

import {
  finalize
} from 'rxjs';

import {
  UiStore
} from '../stores/ui.store';

let activeRequests = 0;

export const loaderInterceptor: HttpInterceptorFn = (
  request,
  next
) => {
  const uiStore = inject(UiStore);

  activeRequests += 1;

  if (activeRequests === 1) {
    uiStore.setLoading(true);
  }

  return next(request).pipe(
    finalize(() => {
      activeRequests = Math.max(
        0,
        activeRequests - 1
      );

      if (activeRequests === 0) {
        uiStore.setLoading(false);
      }
    })
  );
};
