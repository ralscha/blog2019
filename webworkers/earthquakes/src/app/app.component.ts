import {Component, OnInit} from '@angular/core';
import {EarthquakeService} from './earthquake.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  constructor(private readonly earthquakeService: EarthquakeService) {
  }

  ngOnInit(): void {
    this.earthquakeService.initProvider();
  }

}
