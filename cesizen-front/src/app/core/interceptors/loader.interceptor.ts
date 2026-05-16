import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { UiStore } from '../stores/ui.store';

export const loaderInterceptor: HttpInterceptorFn = (req, next) => {
  const uiStore = inject(UiStore);

  // Active le loader global
  uiStore.setLoading(true);

  return next(req).pipe(
    finalize(() => {
      // Désactive le loader global
      uiStore.setLoading(false);
    })
  );
};
