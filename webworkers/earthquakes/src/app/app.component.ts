import {Component, inject, OnInit} from '@angular/core';
import {EarthquakeService} from './earthquake.service';
import {IonApp, IonRouterOutlet} from "@ionic/angular/standalone";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [
    IonApp,
    IonRouterOutlet
  ]
})
export class AppComponent implements OnInit {
  private readonly earthquakeService = inject(EarthquakeService);


  ngOnInit(): void {
    this.earthquakeService.initProvider();
  }

}
