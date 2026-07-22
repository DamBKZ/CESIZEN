import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);

  readonly form = this.fb.nonNullable.group({
    pseudo: [
      '',
      [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(30)
      ]
    ],
    email: [
      '',
      [
        Validators.required,
        Validators.email
      ]
    ],
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
    if (this.form.invalid || this.passwordsDontMatch) {
      this.form.markAllAsTouched();
      return;
    }

    const {
      pseudo,
      email,
      password
    } = this.form.getRawValue();

    this.auth.register({
      pseudo,
      email,
      password
    });
  }
}
