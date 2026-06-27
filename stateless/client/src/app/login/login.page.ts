import { Component, inject, signal } from '@angular/core';
import {
  IonButton,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonTitle,
  IonToolbar,
  NavController,
} from '@ionic/angular/standalone';
import { AuthService } from '../service/auth.service';
import { MessagesService } from '../service/messages.service';
import { FormField, FormRoot, form } from '@angular/forms/signals';

interface LoginForm {
  email: string;
  password: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  imports: [
    FormField,
    FormRoot,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonList,
    IonItem,
    IonInput,
    IonButton,
  ],
})
export class LoginPage {
  readonly loginModel = signal<LoginForm>({
    email: '',
    password: '',
  });
  readonly loginForm = form(this.loginModel);

  private readonly navCtrl = inject(NavController);
  private readonly authService = inject(AuthService);
  private readonly messagesService = inject(MessagesService);

  async login(): Promise<void> {
    const { email, password } = this.loginModel();
    const loading = await this.messagesService.showLoading('Logging in');

    this.authService.login(email, password).subscribe(
      async (authenticated) => {
        await loading.dismiss();

        if (authenticated) {
          this.navCtrl.navigateRoot('/home');
        } else {
          this.showLoginFailedToast();
        }
      },
      () => this.showLoginFailedToast(),
    );
  }

  private showLoginFailedToast(): void {
    this.messagesService.showErrorToast('Login failed');
  }
}
