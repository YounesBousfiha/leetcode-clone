import {Component, OnInit} from '@angular/core';
import { CommonModule} from '@angular/common';
import {FormGroup, Validators, ReactiveFormsModule, FormControl} from '@angular/forms';
import { Router, RouterLink} from '@angular/router';


import { ButtonModule} from 'primeng/button';
import { InputTextModule} from 'primeng/inputtext';
import { PasswordModule} from 'primeng/password';
import { CheckboxModule} from 'primeng/checkbox';
import { DividerModule} from 'primeng/divider';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    CheckboxModule,
    DividerModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup
  loading: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)])
    });
  }

  onSubmit(): void {
    if(this.loginForm.valid) {
      this.loading = true;



      console.log("Login Attemp:", this.loginForm.value);

      // stimulate API Call Delay
      setTimeout(() => {
        this.loading = false;

        this.router.navigate(['/']);
      }, 2000);
    } else {
      this.loginForm.markAllAsTouched();
    }
  }
}
