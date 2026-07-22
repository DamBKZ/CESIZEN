import {
  Component,
  Input
} from '@angular/core';

export type ToastType =
  | 'success'
  | 'error'
  | 'info';

@Component({
  selector: 'app-toast',
  standalone: true,
  template: `
    <div
      class="toast"
      [class.toast--success]="type === 'success'"
      [class.toast--error]="type === 'error'"
      [class.toast--info]="type === 'info'"
      role="status"
      aria-live="polite"
      aria-atomic="true"
    >
      {{ message }}
    </div>
  `,
  styles: [
    `
      :host {
        position: fixed;
        top: 16px;
        right: 16px;
        z-index: 3000;
        display: block;
        max-width: min(420px, calc(100vw - 32px));
        pointer-events: none;
      }

      .toast {
        padding: 12px 16px;
        border: 1px solid transparent;
        border-radius: 8px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        font-weight: 600;
        overflow-wrap: anywhere;
        pointer-events: auto;
      }

      .toast--success {
        color: #062a0b;
        background: #f0fff5;
        border-color: #a8ddb5;
      }

      .toast--error {
        color: #3a0b0b;
        background: #fff0f0;
        border-color: #e6abab;
      }

      .toast--info {
        color: #12385e;
        background: #eef6ff;
        border-color: #a8c8e8;
      }
    `
  ]
})
export class ToastComponent {
  @Input() message = '';

  @Input() type: ToastType = 'info';
}
