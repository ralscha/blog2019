import {Component} from '@angular/core';
import {LoadingController, NavController} from '@ionic/angular';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.page.html',
  styleUrls: ['./signup.page.scss'],
})
export class SignupPage {

  submitError: string = null;

  constructor(private readonly navCtrl: NavController,
              private readonly authService: AuthService,
              private readonly loadingCtrl: LoadingController) {
  }

  async signup(email: string, password: string, totp: boolean) {
    const loading = await this.loadingCtrl.create({
      spinner: 'bubbles',
      message: 'Signing up ...'
    });
    await loading.present();

    this.submitError = null;

    this.authService.signup(email, password, totp)
      .subscribe(async (response) => {
        await loading.dismiss();
        if (response.status === 'OK' && !response.secret) {
          this.navCtrl.navigateRoot('/signup/okay');
        } else if (response.status === 'OK' && response.secret) {
          this.authService.signupResponse = response;
          this.navCtrl.navigateRoot('/signup/secrect');
        } else if (response.status === 'USERNAME_TAKEN') {
          this.submitError = 'usernameTaken';
        } else if (response.status === 'WEAK_PASSWORD') {
          this.submitError = 'weakPassword';
        }

      }, () => {
        loading.dismiss();
      });

  }

}
