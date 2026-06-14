import { Component, inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { EarthquakeService } from './earthquake.service';
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [IonApp, IonRouterOutlet],
})
export class AppComponent implements OnInit {
  private readonly earthquakeService = inject(EarthquakeService);

  ngOnInit(): void {
    this.earthquakeService.initProvider();
  }
}
