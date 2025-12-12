import { Routes } from '@angular/router';
import {AuthLayoutComponent} from './layout/auth-layout/auth-layout.component';
import {LoginComponent} from './features/auth/login/login.component';
import {RegisterComponent} from './features/auth/register/register.component';
import {ForgetpasswordComponent} from './features/auth/forgetpassword/forgetpassword.component';
import {ResetpasswordComponent} from './features/auth/resetpassword/resetpassword.component';
import {MainLayoutComponent} from './layout/main-layout/main-layout.component';
import {HomeComponent} from './features/home/home.component';
import {ProblemListComponent} from './features/problems/problem-list/problem-list.component';
import {NotFoundComponent} from './features/not-found/not-found.component';

export const routes: Routes = [
  {
    path: 'auth',
    component: AuthLayoutComponent,
    children: []
  },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', component: HomeComponent},
      { path: 'problems', component: ProblemListComponent},
      { path: 'login', component: LoginComponent},
      { path: 'register', component: RegisterComponent},
      { path: 'forget-password', component: ForgetpasswordComponent},
      { path: 'reset-password', component: ResetpasswordComponent},
      { path: 'not-found', component: NotFoundComponent}
    ]
  },
  {
    path: '**',
    redirectTo: 'not-found',
  }
];
