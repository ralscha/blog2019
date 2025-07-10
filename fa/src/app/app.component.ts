import {Component, inject} from '@angular/core';
import {IonApp, IonRouterOutlet} from "@ionic/angular/standalone";
import {FaIconLibrary} from '@fortawesome/angular-fontawesome';
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
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [
    IonApp,
    IonRouterOutlet
  ]
})
export class AppComponent {
  private readonly library = inject(FaIconLibrary);

  constructor() {
    // library.addIconPacks(fas);
    this.library.addIcons(faSmileWinkSolid, faSmileBeanSolid,
      faSmileWink, faSmileBeam, faEnvelopeOpen, faCloud,
      faMobile, faSquare, faSpinner, faCircle,
      faSync, faPlay, faSun, faMoon, faStar,
      faHandPointLeft, faAsterisk, faCog, faSkating,
      faSkiing, faSkiingNordic, faSnowboarding, faSwimmer,
      faSolidBell, faRegularBell, faCamera, faBan);
  }
}
