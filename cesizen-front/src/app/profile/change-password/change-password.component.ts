import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { finalize } from 'rxjs';

import { ProfileService } from '../../core/services/profile.service';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly profileService = inject(ProfileService);
  private readonly ui = inject(UiStore);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    currentPassword: [
      '',
      Validators.required
    ],
    newPassword: [
      '',
      [
        Validators.required,
        Validators.minLength(10)
      ]
    ]
  });

  readonly submitting = false;

  backToProfile(): void {
    void this.router.navigate(['/profile']);
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.form.getRawValue();

    this.profileService
      .changePassword(payload)
      .subscribe({
        next: () => {
          this.form.reset();

          this.ui.showSnackbar(
            'Mot de passe modifié',
            'success'
          );

          void this.router.navigate(['/profile']);
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors du changement de mot de passe',
            'error'
          );
        }
      });
  }
}
