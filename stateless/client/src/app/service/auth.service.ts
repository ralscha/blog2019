import {inject, Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly httpClient = inject(HttpClient);


  private readonly authoritySubject = new BehaviorSubject<string | null>(null);
  readonly authority$ = this.authoritySubject.asObservable();

  isAuthenticated(): Observable<boolean> {
    return this.httpClient.get(`/authenticate`, {responseType: 'text'})
      .pipe(
        map(response => this.handleAuthResponse(response)),
        catchError(() => of(false))
      );
  }

  isLoggedIn(): boolean {
    return this.authoritySubject.getValue() !== null;
  }

  login(username: string, password: string): Observable<boolean> {
    const body = new HttpParams().set('username', username).set('password', password);

    return this.httpClient.post('/login', body, {responseType: 'text'})
      .pipe(
        map(response => this.handleAuthResponse(response)),
        catchError(() => of(false))
      );
  }

  logout(): Observable<void> {
    return this.httpClient.get<void>('/logout')
      .pipe(
        tap(() => this.authoritySubject.next(null))
      );
  }

  private handleAuthResponse(response: string): boolean {
    if (response) {
      this.authoritySubject.next(response);
      return true;
    } else {
      this.authoritySubject.next(null);
      return false;
    }
  }

}
