import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, finalize, map, mapTo, switchMap, tap} from 'rxjs/operators';
import {LoadingController, NavController, ToastController} from '@ionic/angular';
import {fromPromise} from 'rxjs/internal-compatibility';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  signupResponse: SignupResponse = null;

  private loggedIn = false;

  constructor(private readonly httpClient: HttpClient,
              private readonly navCtrl: NavController,
              private readonly toastCtrl: ToastController,
              private readonly loadingCtrl: LoadingController) {
  }

  isAuthenticated(): Observable<boolean> {
    return this.httpClient.get<void>('/authenticate', {
      withCredentials: true
    }).pipe(
      tap(() => this.loggedIn = true),
      mapTo(true),
      catchError(error => {
        this.loggedIn = false;
        return of(false);
      })
    );
  }

  login(username: string, password: string, totpkey: string): Observable<boolean> {
    let loading;

    return fromPromise(this.loadingCtrl.create({
      spinner: 'bubbles',
      message: 'Logging in ...'
    })).pipe(
      tap(c => {
        loading = c;
        c.present();
      }),
      switchMap(() => this.submitLogin(username, password, totpkey)),
      finalize(() => loading.dismiss()),
      tap(success => {
        if (success) {
          this.navCtrl.navigateRoot('/home');
        } else {
          this.showError();
        }
      })
    );
  }

  logout(): Observable<void> {
    return this.httpClient.get<void>('/logout', {
      withCredentials: true
    }).pipe(tap(() => this.loggedIn = false));
  }

  isLoggedIn(): boolean {
    return this.loggedIn;
  }

  async showError() {
    const toast = await this.toastCtrl.create({
      message: 'Login failed',
      duration: 4000,
      position: 'bottom'
    });
    return toast.present();
  }

  signup(username: string, password: string, totp: boolean): Observable<SignupResponse> {
    const body = new HttpParams().set('username', username).set('password', password)
      .set('totp', `${totp}`);
    return this.httpClient.post<SignupResponse>('/signup', body);
  }

  verifyCode(username: string, code: string): Observable<boolean> {
    const body = new HttpParams().set('username', username).set('code', code);
    return this.httpClient.post('/signup-confirm-secret', body, {responseType: 'text'})
      .pipe(
        map(response => response === 'true'),
      );
  }

  private submitLogin(username: string, password: string, totpkey: string): Observable<boolean> {
    const body = new HttpParams().set('username', username)
      .set('password', password)
      .set('totpkey', totpkey);

    return this.httpClient.post<void>('/login', body, {
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

export interface SignupResponse {
  status: 'OK' | 'USERNAME_TAKEN' | 'WEAK_PASSWORD';
  username?: string;
  secret?: string;
}
