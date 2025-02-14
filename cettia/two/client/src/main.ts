import {
  PreloadAllModules,
  provideRouter,
  RouteReuseStrategy,
  Routes,
  withHashLocation,
  withPreloading
} from '@angular/router';
import {IonicRouteStrategy} from '@ionic/angular';
import {bootstrapApplication} from '@angular/platform-browser';
import {HomePage} from './app/home/home.page';
import {EditPage} from './app/edit/edit.page';
import {AppComponent} from './app/app.component';
import {provideIonicAngular} from "@ionic/angular/standalone";

const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', component: HomePage},
  {path: 'edit', component: EditPage},
  {path: 'edit/:id', component: EditPage},
  {path: '**', redirectTo: '/home'}
];

bootstrapApplication(AppComponent, {
  providers: [
    {provide: RouteReuseStrategy, useClass: IonicRouteStrategy},
    provideIonicAngular(),
    provideRouter(routes, withHashLocation(), withPreloading(PreloadAllModules))
  ]
})
  .catch(err => console.error(err));
