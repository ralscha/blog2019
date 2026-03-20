import {parse} from 'papaparse';
import {Earthquake, EarthquakeDb} from './earthquake-db';
import {expose} from 'comlink';

type EarthquakeRow = {
  id: string;
  time: string;
  place: string;
  mag: string;
  depth: string;
  latitude: string;
  longitude: string
};

const db = new EarthquakeDb();

const earthquakesLoader = {
  async load(url: string): Promise<void> {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Unable to download earthquake feed: ${response.status}`);
    }

    const text = await response.text();
    const data = parse<EarthquakeRow>(text, {header: true, skipEmptyLines: true});

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

    await db.transaction('rw', db.earthquakes, async () => {
      await db.earthquakes.bulkPut(earthquakes);
    });
  },

  async deleteOldRecords(): Promise<void> {
    const thirtyDaysAgo = Date.now() - (30 * 24 * 60 * 60 * 1000);
    await db.earthquakes.where('time').below(thirtyDaysAgo).delete();
  }
};

expose(earthquakesLoader);


