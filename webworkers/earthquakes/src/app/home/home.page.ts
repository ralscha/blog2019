import {Component, OnDestroy, OnInit} from '@angular/core';
import {EarthquakeService} from '../earthquake.service';
import {Earthquake} from '../earthquake-db';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage implements OnInit, OnDestroy {

  earthquakes: Earthquake[] = [];
  subscription!: Subscription;

  constructor(private readonly earthquakeService: EarthquakeService) {
  }

  async ngOnInit(): Promise<void> {
    this.subscription = this.earthquakeService.change$.subscribe(async () => {
      this.earthquakes = await this.earthquakeService.fetchAll();
    });

    this.earthquakes = await this.earthquakeService.fetchAll();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

}
