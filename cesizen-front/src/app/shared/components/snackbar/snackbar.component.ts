import { Component, computed, inject } from '@angular/core';
import { UiStore } from '../../../core/stores/ui.store';

@Component({
  selector: 'app-snackbar',
  standalone: true,
  templateUrl: './snackbar.component.html',
  styleUrls: ['./snackbar.component.scss']
})
export class SnackbarComponent {
  private readonly ui = inject(UiStore);

  snackbar = computed(() => this.ui.snackbar());
}
