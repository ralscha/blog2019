import {Component} from '@angular/core';
import {NavController} from '@ionic/angular';
import {AuthService} from '../service/auth.service';
import {MessagesService} from '../service/messages.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {

  constructor(private readonly navCtrl: NavController,
              private readonly authService: AuthService,
              private readonly messagesService: MessagesService) {
  }

  async login({email, password}: { email: string, password: string }) {
    const loading = await this.messagesService.showLoading('Logging in');

    this.authService.login(email, password)
      .subscribe(async (authenticated) => {
        await loading.dismiss();

        if (authenticated) {
          this.navCtrl.navigateRoot('/home');
        } else {
          this.showLoginFailedToast();
        }

      }, _ => this.showLoginFailedToast());
  }

  private showLoginFailedToast() {
    this.messagesService.showErrorToast('Login failed');
  }

}
