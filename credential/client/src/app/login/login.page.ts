import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../auth.service';
import { noop } from 'rxjs';
import { FormField, FormRoot, form } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import {
  IonButton,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonRouterLink,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';

interface LoginForm {
  username: string;
  password: string;
}

@Component({
  selector: 'app-login',
  imports: [
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    FormField,
    FormRoot,
    IonList,
    IonItem,
    IonInput,
    IonButton,
    RouterLink,
    IonRouterLink,
  ],
  templateUrl: './login.page.html',
})
export class LoginPage {
  readonly loginModel = signal<LoginForm>({
    username: '',
    password: '',
  });
  readonly loginForm = form(this.loginModel);

  private readonly authService = inject(AuthService);

  login(): void {
    const { username, password } = this.loginModel();
    this.authService.login(username, password).subscribe(noop);
  }
}
