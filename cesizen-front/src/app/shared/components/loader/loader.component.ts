import { Component, computed, inject } from '@angular/core';
import { UiStore } from '../../../core/stores/ui.store';

@Component({
  selector: 'app-loader',
  standalone: true,
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.scss']
})
export class LoaderComponent {
  private readonly ui = inject(UiStore);

  loading = computed(() => this.ui.loading());
}
