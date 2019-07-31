import Papa from 'papaparse';
import {Earthquake, EarthquakeDb} from './earthquake-db';
import * as Comlink from 'comlink';

class EarthquakesLoader {
  private db: EarthquakeDb;

  constructor() {
    this.db = new EarthquakeDb();
  }

  async load(url: string) {
    const response = await fetch(url);
    const text = await response.text();
    const data = Papa.parse(text, {header: true});

    const earthquakes: Earthquake[] = [];

    for (const row of data.data) {
      if (row.id) {
        earthquakes.push({
          time: new Date(row.time).getTime(),
          place: row.place,
          mag: Number(row.mag),
          depth: Number(row.depth),
          latLng: [Number(row.latitude), Number(row.longitude)],
          id: row.id
        });
      }
    }

    return this.db.transaction('rw', this.db.earthquakes, async () => {
      await this.db.earthquakes.bulkPut(earthquakes);
    });
  }

  async deleteOldRecords() {
    const thirtyDaysAgo = Date.now() - (30 * 24 * 60 * 60 * 1000);
    await this.db.earthquakes.where('time').below(thirtyDaysAgo).delete();
  }

}

Comlink.expose(EarthquakesLoader);


