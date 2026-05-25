import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly auth = inject(AuthService);

  token = this.route.snapshot.paramMap.get('token') ?? '';

  form: FormGroup = this.fb.group({
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', Validators.required]
  });

  get passwordsDontMatch(): boolean {
    const { password, confirmPassword } = this.form.value;
    return password && confirmPassword && password !== confirmPassword;
  }

  submit(): void {
    if (this.form.invalid || this.passwordsDontMatch) {
      this.form.markAllAsTouched();
      return;
    }

    this.auth.resetPassword(this.token, this.form.value.password);
  }
}
