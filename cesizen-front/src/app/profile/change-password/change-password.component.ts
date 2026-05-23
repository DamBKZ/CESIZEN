import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ProfileService } from '../profile.service';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent {

  private fb = inject(FormBuilder);
  private profileService = inject(ProfileService);
  private ui = inject(UiStore);

  form = this.fb.group({
    oldPassword: ['', Validators.required],
    newPassword: ['', Validators.required]
  });

  save() {
    if (this.form.invalid) return;

    this.profileService.changePassword(this.form.value).subscribe({
      next: () => this.ui.showSnackbar('Mot de passe modifié', 'success'),
      error: () => this.ui.showSnackbar('Erreur lors du changement', 'error')
    });
  }
}
