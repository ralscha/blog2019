import {bootstrapApplication} from "@angular/platform-browser";
import {provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {AppComponent} from "./app/app.component";

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptorsFromDi())
  ]
})
  .catch(err => console.error(err));
