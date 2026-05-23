import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ProfileService } from './profile.service';
import { UserStore } from '../core/stores/user.store';
import { UiStore } from '../core/stores/ui.store';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {

  private fb = inject(FormBuilder);
  private profileService = inject(ProfileService);
  private userStore = inject(UserStore);
  private ui = inject(UiStore);

  user = this.userStore.user();

  form = this.fb.group({
    pseudo: [this.user?.pseudo, [Validators.required, Validators.minLength(2), Validators.maxLength(30)]],
    email: [this.user?.email, [Validators.required, Validators.email]]
  });

  save() {
    if (this.form.invalid) return;

    this.profileService.updateProfile(this.form.value).subscribe({
      next: () => this.ui.showSnackbar('Profil mis à jour', 'success'),
      error: () => this.ui.showSnackbar('Erreur lors de la mise à jour', 'error')
    });
  }
}
