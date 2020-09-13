import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable, of} from 'rxjs';
import {AuthService} from './auth.service';
import {map, mergeMap} from 'rxjs/operators';
import {fromPromise} from 'rxjs/internal-compatibility';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private readonly authService: AuthService, private readonly router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    return this.authService.isAuthenticated().pipe(
      mergeMap(success => {
        if (success) {
          return of(true);
        }
        return this.tryAutoSignIn();
      }),
      map(success => {
        if (success) {
          return true;
        }
        return this.router.createUrlTree(['/login']);
      })
    );
  }

  private tryAutoSignIn(): Observable<boolean> {
    // @ts-ignore
    if (!window.PasswordCredential) {
      return of(false);
    }

    // @ts-ignore
    return fromPromise(navigator.credentials.get({password: true}))
      .pipe(
        mergeMap(cred => {
            if (cred) {
              // @ts-ignore
              return this.authService.login(cred.name, cred.password);
            }
            return of(false);
          }
        )
      );
  }

}
