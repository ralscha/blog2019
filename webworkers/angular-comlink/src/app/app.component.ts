import {AfterViewInit, Component, ElementRef, OnDestroy, ViewChild} from '@angular/core';
import {wrap} from 'comlink';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit, OnDestroy {
  duration = '';
  progress = '0 %';

  @ViewChild('myCanvas')
  myCanvasRef!: ElementRef;
  private ctx!: CanvasRenderingContext2D;

  private readonly maxIteration = 20000;
  private readonly numberOfWorkers = 4;

  private workX = 0;
  private workY = 0;
  private endCounter = 0;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private computeMandelbrotSetMethods!: any[];
  private workers!: Worker[];
  private height!: number;
  private width!: number;
  private totalPixels!: number;

  ngAfterViewInit(): void {
    const myCanvas = this.myCanvasRef.nativeElement;
    this.ctx = myCanvas.getContext('2d');
    this.height = myCanvas.height;
    this.width = myCanvas.width;
    this.totalPixels = this.height * this.width;

    this.computeMandelbrotSetMethods = [];
    this.workers = [];
    for (let w = 0; w < this.numberOfWorkers; w++) {
      this.workers[w] = new Worker(new URL('./mandelbrot.worker', import.meta.url), {type: 'module'});
      this.computeMandelbrotSetMethods[w] = wrap(this.workers[w]);
    }
  }

  ngOnDestroy(): void {
    for (const worker of this.workers) {
      worker.terminate();
    }
  }

  async startCalculation(): Promise<void> {
    this.ctx.clearRect(0, 0, this.width, this.height);
    this.duration = 'working...';
    performance.clearMarks();
    performance.clearMeasures();
    performance.mark('start-mandelbrot');

    this.workX = 0;
    this.workY = 0;
    this.endCounter = 0;


    const promises: Promise<void>[] = [];
    for (let w = 0; w < this.numberOfWorkers; w++) {
      promises.push(this.work(this.computeMandelbrotSetMethods[w]));
    }
    await Promise.all(promises);

    performance.mark('end-mandelbrot');
    performance.measure('mandelbrot', 'start-mandelbrot', 'end-mandelbrot');
    this.duration = performance.getEntriesByName('mandelbrot')[0].duration + 'ms';
    this.progress = '100 %';
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  async work(computeMandelbrotSetMethod: any): Promise<void> {
    while (this.workY < this.height) {
      const result = await computeMandelbrotSetMethod({
        startX: this.workX,
        startY: this.workY,
        width: 100,
        height: 100,
        totalWidth: this.width,
        totalHeight: this.height,
        maxIteration: this.maxIteration
      });

      for (const point of result) {
        this.ctx.fillRect(point[0], point[1], 1, 1);
      }

      const last = result[result.length - 1];
      if (last) {
        this.progress = Math.round((last[0] + (last[1] * this.width)) * 100 / this.totalPixels) + ' %';
      }

      this.workX += 100;
      if (this.workX === this.width) {
        this.workY += 100;
        this.workX = 0;
      }
    }
  }

}
