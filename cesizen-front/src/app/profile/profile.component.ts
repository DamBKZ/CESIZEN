import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { ProfileService } from '../core/services/profile.service';
import { UserStore } from '../core/stores/user.store';
import { UiStore } from '../core/stores/ui.store';
import { DeleteAccountDialogComponent } from './dialogs/delete-account-dialog.component';

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
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly profileService = inject(ProfileService);
  private readonly userStore = inject(UserStore);
  private readonly ui = inject(UiStore);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  user = this.userStore.user();

  form = this.fb.group({
    pseudo: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email]]
  });

  openChangePassword(): void {
    this.router.navigate(['change-password'], { relativeTo: this.route });
  }

  openHistory(): void {
    this.router.navigate(['history'], { relativeTo: this.route });
  }

  backToInformations(): void {
    this.router.navigate(['/informations/list']);
  }

  ngOnInit(): void {
    const cachedUser = this.userStore.user();

    if (cachedUser) {
      this.user = cachedUser;
      this.form.patchValue({
        pseudo: cachedUser.pseudo,
        email: cachedUser.email
      });
      return;
    }

    this.profileService.getCurrentUser().subscribe({
      next: (user: any) => {
        this.userStore.setUser(user);
        this.user = user;
        this.form.patchValue({
          pseudo: user.pseudo,
          email: user.email
        });
      },
      error: () => {
        this.userStore.clear();
        this.ui.showSnackbar('Session expirée, reconnectez-vous', 'error');
        this.router.navigate(['/login']);
      }
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.profileService.updateProfile(this.form.value).subscribe({
      next: (updatedUser: any) => {
        this.userStore.setUser(updatedUser);
        this.user = updatedUser;
        this.ui.showSnackbar('Profil mis à jour', 'success');
      },
      error: () => this.ui.showSnackbar('Erreur lors de la mise à jour', 'error')
    });
  }

  deleteAccount(): void {
    const user = this.userStore.user();

    if (!user) {
      return;
    }

    const dialogRef = this.dialog.open(DeleteAccountDialogComponent, {
      width: '420px',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) {
        return;
      }

      this.profileService.deleteAccount().subscribe({
        next: () => {
          this.userStore.clear();
          this.ui.showSnackbar('Votre compte a bien été supprimé', 'success');
          this.router.navigate(['/register']);
        },
        error: () => {
          this.ui.showSnackbar('Erreur lors de la suppression du compte', 'error');
        }
      });
    });
  }
}
