import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {EarthquakeService} from '../earthquake.service';
import {Earthquake} from '../earthquake-db';
import {Subscription} from 'rxjs';
import {CdkFixedSizeVirtualScroll, CdkVirtualForOf, CdkVirtualScrollViewport} from '@angular/cdk/scrolling';
import {DatePipe, DecimalPipe} from '@angular/common';
import {
  IonCol,
  IonContent,
  IonHeader,
  IonItem,
  IonLabel,
  IonRow,
  IonTitle,
  IonToolbar
} from "@ionic/angular/standalone";

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrl: './home.page.scss',
  imports: [CdkVirtualScrollViewport, CdkFixedSizeVirtualScroll, CdkVirtualForOf, DecimalPipe, DatePipe, IonHeader, IonToolbar, IonTitle, IonContent, IonItem, IonLabel, IonRow, IonCol]
})
export class HomePage implements OnInit, OnDestroy {
  earthquakes: Earthquake[] = [];
  subscription!: Subscription;
  private readonly earthquakeService = inject(EarthquakeService);

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
