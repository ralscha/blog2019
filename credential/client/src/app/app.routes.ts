import {inject} from '@angular/core';
import {Routes} from '@angular/router';
import {LoginPage} from './login/login.page';
import {AuthGuard} from './auth.guard';
import {HomePage} from "./home/home.page";

export const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {
    path: 'home',
    canActivate: [() => inject(AuthGuard).canActivate()],
    component: HomePage
  },
  {path: 'login', component: LoginPage}
];
