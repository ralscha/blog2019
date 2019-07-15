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

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
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
    if (!(window as any).PasswordCredential) {
      return of(false);
    }

    return fromPromise((navigator as any).credentials.get({password: true}))
      .pipe(
        mergeMap(cred => {
            if (cred) {
              const c = cred as any;
              return this.authService.login(c.name, c.password);
            }
            return of(false);
          }
        )
      );
  }

}
