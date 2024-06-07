import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouteReuseStrategy, RouterModule, Routes} from '@angular/router';
import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {AppComponent} from './app.component';
import {HomePage} from './home/home.page';
import {CommonModule} from '@angular/common';
import {FaIconLibrary, FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {FormsModule} from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import {
  faAsterisk,
  faBan,
  faBell as faSolidBell,
  faCamera,
  faCircle,
  faCloud,
  faCog,
  faEnvelopeOpen,
  faHandPointLeft,
  faMobile,
  faMoon,
  faPlay,
  faSkating,
  faSkiing,
  faSkiingNordic,
  faSmileBeam as faSmileBeanSolid,
  faSmileWink as faSmileWinkSolid,
  faSnowboarding,
  faSpinner,
  faSquare,
  faStar,
  faSun,
  faSwimmer,
  faSync
} from '@fortawesome/free-solid-svg-icons';
import {faBell as faRegularBell, faSmileBeam, faSmileWink} from '@fortawesome/free-regular-svg-icons';
// import {fas} from '@fortawesome/free-solid-svg-icons';

const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', component: HomePage},
];

@NgModule({ declarations: [AppComponent, HomePage],
    bootstrap: [AppComponent], imports: [BrowserModule,
        FontAwesomeModule,
        CommonModule,
        FormsModule,
        IonicModule.forRoot(),
        RouterModule.forRoot(routes, { useHash: true })], providers: [
        { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class AppModule {
  constructor(library: FaIconLibrary) {
    // library.addIconPacks(fas);
    library.addIcons(faSmileWinkSolid, faSmileBeanSolid,
      faSmileWink, faSmileBeam, faEnvelopeOpen, faCloud,
      faMobile, faSquare, faSpinner, faCircle,
      faSync, faPlay, faSun, faMoon, faStar,
      faHandPointLeft, faAsterisk, faCog, faSkating,
      faSkiing, faSkiingNordic, faSnowboarding, faSwimmer,
      faSolidBell, faRegularBell, faCamera, faBan);
  }
}
