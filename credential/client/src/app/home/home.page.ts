import {Component} from '@angular/core';
import {AuthService} from '../auth.service';
import {NavController} from '@ionic/angular';

@Component({
    selector: 'app-home',
    templateUrl: './home.page.html',
    styleUrls: ['./home.page.scss'],
    standalone: false
})
export class HomePage {
  constructor(private readonly authService: AuthService,
              private readonly navCtrl: NavController) {
  }

  async logout(): Promise<void> {
    this.authService.logout().subscribe(() => this.navCtrl.navigateRoot('/login'));
  }

}
