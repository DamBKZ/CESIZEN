import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast" [class.success]="type==='success'" [class.error]="type==='error'">
      {{ message }}
    </div>
  `,
  styles: [
    `:host { position: fixed; right: 16px; top: 16px; z-index: 3000; }
     .toast { padding: 10px 14px; border-radius: 8px; color: #072; background: #e6fff0; box-shadow: 0 8px 24px rgba(0,0,0,0.12); font-weight:600 }
     .toast.error { color: #3a0b0b; background: #fff0f0 }
     .toast.success { color: #062a0b; background: #f0fff5 }
    `
  ]
})
export class ToastComponent {
  @Input() message = '';
  @Input() type: 'success' | 'error' = 'success';
}
