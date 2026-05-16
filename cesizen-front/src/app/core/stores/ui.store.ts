import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UiStore {

  // Loader global
  private readonly _loading = signal(false);
  readonly loading = this._loading.asReadonly();

  // Snackbar global
  private readonly _snackbar = signal<{ message: string; type: 'success' | 'error' | 'info' } | null>(null);
  readonly snackbar = this._snackbar.asReadonly();

  // --- Loader ---
  setLoading(value: boolean): void {
    this._loading.set(value);
  }

  // --- Snackbar ---
  showSnackbar(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
    this._snackbar.set({ message, type });

    // Auto-hide après 3 secondes
    setTimeout(() => this.clearSnackbar(), 3000);
  }

  clearSnackbar(): void {
    this._snackbar.set(null);
  }
}
