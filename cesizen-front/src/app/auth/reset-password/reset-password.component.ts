import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {
  ActivatedRoute,
  RouterLink
} from '@angular/router';

import { AuthService } from '../auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly auth = inject(AuthService);

  readonly token =
    this.route.snapshot.paramMap.get('token')?.trim() ?? '';

  readonly form = this.fb.nonNullable.group({
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(10)
      ]
    ],
    confirmPassword: [
      '',
      Validators.required
    ]
  });

  get passwordsDontMatch(): boolean {
    const { password, confirmPassword } =
      this.form.getRawValue();

    return Boolean(
      password &&
      confirmPassword &&
      password !== confirmPassword
    );
  }

  submit(): void {
    if (
      !this.token ||
      this.form.invalid ||
      this.passwordsDontMatch
    ) {
      this.form.markAllAsTouched();
      return;
    }

    const { password } = this.form.getRawValue();

    this.auth.resetPassword(
      this.token,
      password
    );
  }
}
