import { Injectable, signal, inject } from '@angular/core';
import { ToastService } from '../../shared/services/toast.service';

@Injectable({
  providedIn: 'root'
})
export class UiStore {
  private readonly _loading = signal(false);
  readonly loading = this._loading.asReadonly();

  private readonly _snackbar = signal<{ message: string; type: 'success' | 'error' | 'info' } | null>(null);
  readonly snackbar = this._snackbar.asReadonly();

  setLoading(value: boolean): void {
    this._loading.set(value);
  }

  showSnackbar(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
    this._snackbar.set({ message, type });

    try {
      const toast = inject(ToastService);
      if (type === 'success') toast.success(message);
      else if (type === 'error') toast.error(message);
      else toast.success(message);
    } catch (_) {
    }

    setTimeout(() => this.clearSnackbar(), 3000);
  }

  clearSnackbar(): void {
    this._snackbar.set(null);
  }
}
