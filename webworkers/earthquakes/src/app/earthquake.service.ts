import {Injectable} from '@angular/core';
import {Earthquake, EarthquakeDb} from './earthquake-db';
import {Subject} from 'rxjs';
import * as Comlink from 'comlink';

@Injectable({
  providedIn: 'root'
})
export class EarthquakeService {

  private static readonly FOURTYFIVE_MINUTES = 30 * 60 * 1000;
  private static readonly ONE_HOUR = 60 * 60 * 1000;
  private static readonly ONE_DAY = 24 * 60 * 60 * 1000;
  private static readonly SEVEN_DAYS = 7 * 24 * 60 * 60 * 1000;

  private db: EarthquakeDb;

  private changeSubject = new Subject();
  change$ = this.changeSubject.asObservable();

  constructor() {
    this.db = new EarthquakeDb();
  }

  async initProvider(): Promise<void> {
    const lastUpdate = localStorage.getItem('lastUpdate');
    let url = null;

    if (lastUpdate) {
      const lastUpdateTs = parseInt(lastUpdate, 10);
      const now = Date.now();
      if (lastUpdateTs + EarthquakeService.SEVEN_DAYS < now) {
        // database older than 7 days. load the 30 days file
        url = 'https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/1.0_month.csv';
      } else if (lastUpdateTs + EarthquakeService.ONE_DAY < now) {
        // database older than 1 day. load the 7 days file
        url = 'https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.csv';
      } else if (lastUpdateTs + EarthquakeService.ONE_HOUR < now) {
        // database older than 1 hour. load the 1 day file
        url = 'https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.csv';
      } else if (lastUpdateTs + EarthquakeService.FOURTYFIVE_MINUTES < now) {
        // database older than 45 minutes. load the 1 hour file
        url = 'https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.csv';
      }
    } else {
      // no last update. load the 30 days file
      url = 'https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/1.0_month.csv';
    }

    if (url !== null) {
      const EarthquakesLoader = Comlink.wrap(new Worker('./earthquakes-loader.worker', {type: 'module'}));
      // @ts-ignore
      const earthquakesLoader = await new EarthquakesLoader();

      await earthquakesLoader.load(url);
      console.log('records loaded');
      localStorage.setItem('lastUpdate', Date.now().toString());

      await earthquakesLoader.deleteOldRecords();
      console.log('old records deleted');

      this.changeSubject.next('changed');
    }
  }

  async fetchAll(): Promise<Earthquake[]> {
    return this.db.earthquakes.toArray();
  }


}
