import {Component} from '@angular/core';
import {AuthService} from '../auth.service';
import {noop} from 'rxjs';

@Component({
    selector: 'app-login',
    templateUrl: './login.page.html',
    styleUrls: ['./login.page.scss'],
    standalone: false
})
export class LoginPage {

  constructor(private readonly authService: AuthService) {
  }

  login(username: string, password: string): void {
    this.authService.login(username, password).subscribe(noop);
  }


}
