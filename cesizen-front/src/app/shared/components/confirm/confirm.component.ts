import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="confirm-overlay">
      <div class="confirm-panel">
        <div class="confirm-message">{{ message }}</div>
        <div class="confirm-actions">
          <button class="btn btn-cancel" (click)="onResult(false)">Annuler</button>
          <button class="btn btn-confirm" (click)="onResult(true)">Confirmer</button>
        </div>
      </div>
    </div>
  `,
  styles: [
    `:host { position: fixed; inset: 0; display: block; z-index: 2000; }
    .confirm-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.36); display:flex; align-items:center; justify-content:center; }
    .confirm-panel { background: #fff; padding: 18px; border-radius: 10px; width: 360px; max-width: 92%; box-shadow: 0 12px 40px rgba(0,0,0,0.18); }
    .confirm-message { margin-bottom: 16px; font-weight:600; color:#122; }
    .confirm-actions { display:flex; justify-content:flex-end; gap:8px; }
    .btn { padding:8px 12px; border-radius:8px; border: none; cursor:pointer; }
    .btn-cancel { background: #f2f3f5; color:#213; }
    .btn-confirm { background: #d9534f; color: #fff; }
    `
  ]
})
export class ConfirmComponent {
  @Input() message = 'Confirmer ?';
  @Output() result = new EventEmitter<boolean>();

  onResult(v: boolean) {
    this.result.emit(v);
  }
}
