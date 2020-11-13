import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouteReuseStrategy, RouterModule, Routes} from '@angular/router';
import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {AppComponent} from './app.component';
import {HomePage} from './home/home.page';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {LoginPage} from './login/login.page';
import {AuthGuard} from './service/auth.guard';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomePage,
    canActivate: [AuthGuard]
  },
  {
    path: 'login',
    component: LoginPage
  },
  {
    path: '**',
    redirectTo: '/home'
  }
];

@NgModule({
  declarations: [AppComponent, HomePage, LoginPage],
  entryComponents: [],
  imports: [BrowserModule,
    CommonModule,
    HttpClientModule,
    FormsModule,
    IonicModule.forRoot(),
    RouterModule.forRoot(routes, { useHash: true, relativeLinkResolution: 'legacy' })],
  providers: [
    {provide: RouteReuseStrategy, useClass: IonicRouteStrategy}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
