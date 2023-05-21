import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import { defineCustomElements } from '@ionic/pwa-elements/loader';
import {AppModule} from './app/app.module';

platformBrowserDynamic()
  .bootstrapModule(AppModule)
  .catch(err => console.error(err))
  .then(() => defineCustomElements(window));

