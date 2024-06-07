import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {RouteReuseStrategy} from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import {LoginPage} from './login/login.page';
import {FormsModule} from '@angular/forms';

@NgModule({ declarations: [
        AppComponent,
        LoginPage
    ],
    bootstrap: [AppComponent], imports: [BrowserModule,
        FormsModule,
        IonicModule.forRoot(),
        AppRoutingModule], providers: [
        { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class AppModule {
}
