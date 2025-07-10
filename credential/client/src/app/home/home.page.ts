import {Component, inject} from '@angular/core';
import {AuthService} from '../auth.service';
import {IonButton, IonContent, IonHeader, IonTitle, IonToolbar, NavController} from '@ionic/angular/standalone';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  imports: [
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonButton
  ]
})
export class HomePage {
  private readonly authService = inject(AuthService);
  private readonly navCtrl = inject(NavController);


  async logout(): Promise<void> {
    this.authService.logout().subscribe(() => this.navCtrl.navigateRoot('/login'));
  }

}
