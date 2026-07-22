import {
  Component,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';

import {
  ProfileService,
  UpdateProfileRequest
} from '../core/services/profile.service';

import {
  User,
  UserStore
} from '../core/stores/user.store';

import { UiStore } from '../core/stores/ui.store';

import {
  DeleteAccountDialogComponent
} from './dialogs/delete-account-dialog.component';

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
  private readonly profileService = inject(ProfileService);
  private readonly userStore = inject(UserStore);
  private readonly ui = inject(UiStore);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  user: User | null = null;

  readonly form = new FormGroup({
    pseudo: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(30)
      ]
    }),

    email: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.email
      ]
    })
  });

  ngOnInit(): void {
    const cachedUser = this.userStore.user();

    if (cachedUser) {
      this.applyUser(cachedUser);
      return;
    }

    this.loadCurrentUser();
  }

  openChangePassword(): void {
    void this.router.navigate(
      ['change-password'],
      {
        relativeTo: this.route
      }
    );
  }

  openHistory(): void {
    void this.router.navigate(
      ['history'],
      {
        relativeTo: this.route
      }
    );
  }

  backToInformations(): void {
    void this.router.navigate([
      '/informations/list'
    ]);
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();

      this.ui.showSnackbar(
        'Veuillez corriger les champs du formulaire',
        'error'
      );

      return;
    }

    const payload: UpdateProfileRequest =
      this.form.getRawValue();

    this.ui.setLoading(true);

    this.profileService
      .updateProfile(payload)
      .subscribe({
        next: updatedUser => {
          this.userStore.setUser(updatedUser);
          this.applyUser(updatedUser);

          this.ui.showSnackbar(
            'Profil mis à jour',
            'success'
          );
        },

        error: () => {
          this.ui.setLoading(false);

          this.ui.showSnackbar(
            'Erreur lors de la mise à jour',
            'error'
          );
        },

        complete: () => {
          this.ui.setLoading(false);
        }
      });
  }

  deleteAccount(): void {
    if (!this.userStore.user()) {
      return;
    }

    const dialogRef = this.dialog.open(
      DeleteAccountDialogComponent,
      {
        width: '420px',
        disableClose: true
      }
    );

    dialogRef.afterClosed().subscribe(
      (confirmed: boolean) => {
        if (!confirmed) {
          return;
        }

        this.ui.setLoading(true);

        this.profileService
          .deleteAccount()
          .subscribe({
            next: () => {
              this.userStore.clear();

              this.ui.showSnackbar(
                'Votre compte a bien été supprimé',
                'success'
              );

              void this.router.navigate(['/register']);
            },

            error: () => {
              this.ui.setLoading(false);

              this.ui.showSnackbar(
                'Erreur lors de la suppression du compte',
                'error'
              );
            },

            complete: () => {
              this.ui.setLoading(false);
            }
          });
      }
    );
  }

  private loadCurrentUser(): void {
    this.ui.setLoading(true);

    this.profileService
      .getCurrentUser()
      .subscribe({
        next: user => {
          this.userStore.setUser(user);
          this.applyUser(user);
        },

        error: () => {
          this.ui.setLoading(false);
          this.userStore.clear();

          this.ui.showSnackbar(
            'Session expirée, reconnectez-vous',
            'error'
          );

          void this.router.navigate(['/login']);
        },

        complete: () => {
          this.ui.setLoading(false);
        }
      });
  }

  private applyUser(user: User): void {
    this.user = user;

    this.form.setValue({
      pseudo: user.pseudo,
      email: user.email
    });

    this.form.markAsPristine();
    this.form.markAsUntouched();
  }
}
