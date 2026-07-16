import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { UserStore } from '../stores/user.store';

function readCookie(name: string): string | null {
  const match = document.cookie.match(new RegExp('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)'));
  return match ? decodeURIComponent(match[2]) : null;
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const userStore = inject(UserStore);
  const token = userStore.token();

  const isMutatingRequest = !['GET', 'HEAD', 'OPTIONS'].includes(req.method.toUpperCase());
  const xsrf = readCookie('XSRF-TOKEN');

  let authReq = req.clone({
    withCredentials: true
  });

  if (token) {
    authReq = authReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  if (isMutatingRequest && xsrf) {
    authReq = authReq.clone({
      setHeaders: {
        'X-XSRF-TOKEN': xsrf
      }
    });
  }

  return next(authReq);
};
