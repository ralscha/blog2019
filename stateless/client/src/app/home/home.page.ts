import {Component, inject, OnInit} from '@angular/core';
import {AuthService} from '../service/auth.service';
import {IonButton, IonContent, IonHeader, IonTitle, IonToolbar, NavController} from '@ionic/angular/standalone';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError, concatMap, filter, map, take} from 'rxjs/operators';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  imports: [AsyncPipe, IonHeader, IonToolbar, IonTitle, IonContent, IonButton]
})
export class HomePage implements OnInit {
  message!: Observable<string>;
  userEnabled = false;
  private readonly authService = inject(AuthService);
  private readonly navCtrl = inject(NavController);
  private readonly httpClient = inject(HttpClient);

  ngOnInit(): void {
    this.message = this.httpClient.get('/message', {responseType: 'text'})
      .pipe(catchError(error => of('Error: ' + JSON.stringify(error))));

    this.httpClient.get('/admin', {responseType: 'text'}).subscribe(
      response => console.log(response),
      error => console.log('admin call: ' + JSON.stringify(error))
    );

    this.isAdmin().pipe(take(1), filter(isAdmin => isAdmin),
      concatMap(() => this.httpClient.get<boolean>('/isEnabled')))
      .subscribe(flag => this.userEnabled = flag, err => console.log(err));
  }

  logout(): void {
    this.authService.logout().subscribe(() => this.navCtrl.navigateRoot('/login'));
  }

  isAdmin(): Observable<boolean> {
    return this.authService.authority$.pipe(map(authority => authority === 'ADMIN'));
  }

  enable(): void {
    this.httpClient.get<void>('/enable').subscribe(() => this.userEnabled = true, error => console.log('enable: ' + error));
  }

  disable(): void {
    this.httpClient.get<void>('/disable').subscribe(() => this.userEnabled = false, error => console.log('disable: ' + error));
  }

}
