import {Component} from '@angular/core';
import {AuthService} from '../auth.service';
import QRCode from 'qrcode';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {

  qrLinkAdmin = 'otpauth://totp/admin?secret=W4AU5VIXXCPZ3S6T&issuer=totpexample';
  qrLinkUser = 'otpauth://totp/user?secret=LRVLAZ4WVFOU3JBF&issuer=totpexample';
  qrCodeAdmin: string;
  qrCodeUser: string;
  qrSafeLinkAdmin: SafeResourceUrl;
  qrSafeLinkUser: SafeResourceUrl;

  constructor(private readonly authService: AuthService,
              readonly sanitizer: DomSanitizer) {

    this.qrSafeLinkAdmin = this.sanitizer.bypassSecurityTrustResourceUrl(this.qrLinkAdmin);
    this.qrSafeLinkUser = this.sanitizer.bypassSecurityTrustResourceUrl(this.qrLinkUser);

    QRCode.toDataURL(this.qrLinkAdmin).then(url => this.qrCodeAdmin = url);
    QRCode.toDataURL(this.qrLinkUser).then(url => this.qrCodeUser = url);
  }

  login({username, password, totpkey}) {
    this.authService.login(username, password, totpkey).subscribe(() => {
    });
  }


}
