import {Component} from '@angular/core';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {

  constructor(private readonly authService: AuthService) {
  }

  login(username: string, password: string) {
    this.authService.login(username, password).subscribe(() => {
    });
  }


}
