import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn, Validators
} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { DividerModule } from 'primeng/divider';

@Component({
  selector: 'app-register',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    DividerModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent implements OnInit {
    registerForm!: FormGroup;
    loading: boolean = false;

    constructor(private router: Router) {}

    ngOnInit(): void {
        this.registerForm = new FormGroup({
          firstName: new FormControl('', Validators.required),
          lastName: new FormControl('', Validators.required),
          email: new FormControl('', [Validators.required, Validators.email]),
          password: new FormControl('', [Validators.required, Validators.minLength(6)]),
          confirmPassword: new FormControl('', [Validators.required, Validators.minLength(6)])
        }, { validators: this.passwordMatchValidator}
        );
    }

  passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
      const password = control.get("password");
      const confirmPassword = control.get("confirmPassword");

      if(password && confirmPassword && password.value !== confirmPassword.value) {
        confirmPassword.setErrors({ passwordMismatch: true});

        return { passwordMismatch: true }
      }
      return null;
  }

  onSubmit(): void {
      if(this.registerForm.valid) {
        this.loading = true;
        console.log("Register Data:", this.registerForm.value);

        // stimulation
        setTimeout(() => {
          this.loading = false;

          this.router.navigate(['/auth/login']);
        }, 2000);
      } else {
        this.registerForm.markAllAsTouched();
      }
  }

}
