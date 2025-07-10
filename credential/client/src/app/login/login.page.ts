import {Component, inject} from '@angular/core';
import {AuthService} from '../auth.service';
import {noop} from 'rxjs';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {
  IonButton,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonRouterLink,
  IonTitle,
  IonToolbar
} from "@ionic/angular/standalone";

@Component({
  selector: 'app-login',
  imports: [
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    FormsModule,
    IonList,
    IonItem,
    IonInput,
    IonButton,
    RouterLink,
    IonRouterLink
  ],
  templateUrl: './login.page.html'
})
export class LoginPage {
  private readonly authService = inject(AuthService);


  login(username: string, password: string): void {
    this.authService.login(username, password).subscribe(noop);
  }


}
