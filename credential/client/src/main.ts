import {AppComponent} from "./app/app.component";
import {bootstrapApplication} from "@angular/platform-browser";
import {IonicRouteStrategy, provideIonicAngular} from "@ionic/angular/standalone";
import {routes} from "./app/app.routes";
import {provideRouter, RouteReuseStrategy, withHashLocation} from "@angular/router";
import {provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";

bootstrapApplication(AppComponent, {
  providers: [
    provideIonicAngular(),
    provideHttpClient(withInterceptorsFromDi()),
    provideRouter(routes, withHashLocation()),
    {provide: RouteReuseStrategy, useClass: IonicRouteStrategy}
  ]
})
  .catch(err => console.error(err));
