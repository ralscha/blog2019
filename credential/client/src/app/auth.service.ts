import {inject, Injectable} from '@angular/core';
import {from, Observable, of} from 'rxjs';
import {environment} from '../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, finalize, map, mapTo, switchMap, tap} from 'rxjs/operators';
import {LoadingController, NavController, ToastController} from '@ionic/angular/standalone';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly httpClient = inject(HttpClient);
  private readonly navCtrl = inject(NavController);
  private readonly toastCtrl = inject(ToastController);
  private readonly loadingCtrl = inject(LoadingController);


  private loggedIn = false;

  isAuthenticated(): Observable<boolean> {
    return this.httpClient.get<void>(`${environment.serverUrl}/authenticate`, {
      withCredentials: true
    }).pipe(
      tap(() => this.loggedIn = true),
      map(() => true),
      catchError(() => {
        this.loggedIn = false;
        return of(false);
      })
    );
  }

  login(username: string, password: string): Observable<boolean> {
    let loading: HTMLIonLoadingElement;

    return from(this.loadingCtrl.create({
      spinner: 'bubbles',
      message: `Logging in ...`
    })).pipe(
      tap(c => {
        loading = c;
        c.present();
      }),
      switchMap(() => this.submitLogin(username, password)),
      finalize(() => loading.dismiss()),
      tap(success => {
        if (success) {
          this.storePassword(username, password).then(() => this.navCtrl.navigateRoot('/home'));
        } else {
          this.showError();
        }
      })
    );
  }

  logout(): Observable<void> {
    return this.httpClient.get<void>(`${environment.serverUrl}/logout`, {
      withCredentials: true
    }).pipe(tap(() => this.loggedIn = false));
  }

  isLoggedIn(): boolean {
    return this.loggedIn;
  }

  private async storePassword(username: string, password: string): Promise<Credential | null> {
    // @ts-ignore
    if (!window.PasswordCredential) {
      return Promise.resolve(null);
    }

    // @ts-ignore
    const cred = new window.PasswordCredential({
      id: username,
      password,
      name: username
    });
    navigator.credentials.store(cred);
    return cred;
  }

  private async showError(): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: 'Login failed',
      duration: 4000,
      position: 'bottom'
    });
    return toast.present();
  }

  private submitLogin(username: string, password: string): Observable<boolean> {
    const body = new HttpParams().set('username', username).set('password', password);
    return this.httpClient.post<void>(`${environment.serverUrl}/login`, body, {
      withCredentials: true
    }).pipe(
      tap(() => this.loggedIn = true),
      mapTo(true),
      catchError(() => {
        this.loggedIn = false;
        return of(false);
      })
    );
  }

}
