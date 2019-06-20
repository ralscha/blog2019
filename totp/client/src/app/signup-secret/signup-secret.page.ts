import {Component, OnInit} from '@angular/core';
import {LoadingController, NavController, ToastController} from '@ionic/angular';
import {AuthService} from '../auth.service';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import QRCode from 'qrcode';

@Component({
  selector: 'app-signup-secret',
  templateUrl: './signup-secret.page.html',
  styleUrls: ['./signup-secret.page.scss'],
})
export class SignupSecretPage implements OnInit {

  qrSafeLink: SafeResourceUrl;
  qrCode: string;

  constructor(private readonly navCtrl: NavController,
              private readonly authService: AuthService,
              private readonly loadingCtrl: LoadingController,
              private readonly sanitizer: DomSanitizer,
              private readonly toastCtrl: ToastController) {
  }

  ngOnInit() {
    if (!this.authService.signupResponse) {
      this.navCtrl.navigateRoot('/login');
      return;
    }

    const link = `otpauth://totp/${this.authService.signupResponse.username}?secret=${this.authService.signupResponse.secret}&issuer=totpexample`;
    this.qrSafeLink = this.sanitizer.bypassSecurityTrustResourceUrl(link);
    QRCode.toDataURL(link).then(url => this.qrCode = url);
  }

  async verifyCode(code: string) {

    const loading = await this.loadingCtrl.create({
      spinner: 'bubbles',
      message: 'Verify Authorization Code ...'
    });
    await loading.present();

    this.authService.verifyCode(this.authService.signupResponse.username, code)
      .subscribe(async (success) => {
        loading.dismiss();
        if (success) {
          this.authService.signupResponse = null;
          this.navCtrl.navigateRoot('/signup/okay');
        } else {
          const toast = await this.toastCtrl.create({
            message: 'Authorization Code verification failed',
            duration: 4000,
            position: 'bottom'
          });
          return toast.present();
        }
      }, () => {
        loading.dismiss();
      });
  }

}

