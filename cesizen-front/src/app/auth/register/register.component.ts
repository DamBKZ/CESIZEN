import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);

  form: FormGroup = this.fb.group({
    pseudo: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(10)]],
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

    this.auth.register(this.form.value);
  }
}
