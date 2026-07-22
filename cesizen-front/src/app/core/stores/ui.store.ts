import {
  Injectable,
  signal
} from '@angular/core';

import {
  ToastService
} from '../../shared/services/toast.service';

export type SnackbarType =
  | 'success'
  | 'error'
  | 'info';

export interface SnackbarMessage {
  message: string;
  type: SnackbarType;
}

@Injectable({
  providedIn: 'root'
})
export class UiStore {
  private readonly _loading = signal(false);

  readonly loading =
    this._loading.asReadonly();

  private readonly _snackbar =
    signal<SnackbarMessage | null>(null);

  readonly snackbar =
    this._snackbar.asReadonly();

  private clearSnackbarTimer:
    ReturnType<typeof setTimeout> | null = null;

  constructor(
    private readonly toast: ToastService
  ) {}

  setLoading(value: boolean): void {
    this._loading.set(value);
  }

  showSnackbar(
    message: string,
    type: SnackbarType = 'info'
  ): void {
    const normalizedMessage = message.trim();

    if (!normalizedMessage) {
      return;
    }

    if (this.clearSnackbarTimer !== null) {
      clearTimeout(this.clearSnackbarTimer);
    }

    this._snackbar.set({
      message: normalizedMessage,
      type
    });

    switch (type) {
      case 'success':
        this.toast.success(normalizedMessage);
        break;

      case 'error':
        this.toast.error(normalizedMessage);
        break;

      case 'info':
        this.toast.info(normalizedMessage);
        break;
    }

    this.clearSnackbarTimer = setTimeout(
      () => this.clearSnackbar(),
      3000
    );
  }

  clearSnackbar(): void {
    if (this.clearSnackbarTimer !== null) {
      clearTimeout(this.clearSnackbarTimer);
      this.clearSnackbarTimer = null;
    }

    this._snackbar.set(null);
  }
}
