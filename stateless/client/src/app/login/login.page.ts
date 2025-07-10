import {Component, inject} from '@angular/core';
import {
  IonButton,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonTitle,
  IonToolbar,
  NavController
} from '@ionic/angular/standalone';
import {AuthService} from '../service/auth.service';
import {MessagesService} from '../service/messages.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  imports: [FormsModule, IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem, IonInput, IonButton]
})
export class LoginPage {
  private readonly navCtrl = inject(NavController);
  private readonly authService = inject(AuthService);
  private readonly messagesService = inject(MessagesService);


  async login({email, password}: { email: string, password: string }): Promise<void> {
    const loading = await this.messagesService.showLoading('Logging in');

    this.authService.login(email, password)
      .subscribe(async (authenticated) => {
        await loading.dismiss();

        if (authenticated) {
          this.navCtrl.navigateRoot('/home');
        } else {
          this.showLoginFailedToast();
        }

      }, () => this.showLoginFailedToast());
  }

  private showLoginFailedToast(): void {
    this.messagesService.showErrorToast('Login failed');
  }

}
