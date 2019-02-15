import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouteReuseStrategy, RouterModule, Routes} from '@angular/router';
import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {AppComponent} from './app.component';
import {HomePage} from './home/home.page';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {library} from '@fortawesome/fontawesome-svg-core';
import {
  faSmileWink as faSmileWinkSolid, faSmileBeam as faSmileBeanSolid,
  faAsterisk, faCog, faSkating, faSkiing, faSkiingNordic, faSnowboarding, faSwimmer,
  faCloud, faMobile, faSpinner, faHandPointLeft,
  faSync, faPlay, faSun, faMoon, faStar,
  faEnvelopeOpen, faSquare, faCircle,
} from '@fortawesome/free-solid-svg-icons';
import {faSmileWink, faSmileBeam} from '@fortawesome/free-regular-svg-icons';


const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', component: HomePage},
];

@NgModule({
  declarations: [AppComponent, HomePage],
  entryComponents: [],
  imports: [BrowserModule,
    FontAwesomeModule,
    CommonModule,
    HttpClientModule,
    FormsModule,
    IonicModule.forRoot(),
    RouterModule.forRoot(routes, {useHash: true})],
  providers: [
    {provide: RouteReuseStrategy, useClass: IonicRouteStrategy}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
    library.add(faSmileWinkSolid, faSmileBeanSolid,
      faSmileWink, faSmileBeam,
      faEnvelopeOpen,
      faCloud,
      faMobile,
      faSquare,
      faSpinner,
      faCircle,
      faSync,
      faPlay, faSun, faMoon, faStar,
      faHandPointLeft,
      faAsterisk, faCog, faSkating, faSkiing, faSkiingNordic, faSnowboarding, faSwimmer);
  }
}
