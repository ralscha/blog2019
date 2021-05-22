import {AfterViewInit, Component, ElementRef, OnDestroy, ViewChild} from '@angular/core';

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

    this.workers = [];
    for (let w = 0; w < this.numberOfWorkers; w++) {
      this.workers[w] = new Worker(new URL('./mandelbrot.worker', import.meta.url), {type: 'module'});
      this.workers[w].addEventListener('message', message => this.handleWorkerMessage(this.workers[w], message));
    }
  }

  ngOnDestroy(): void {
    for (const worker of this.workers) {
      worker.terminate();
    }
  }

  startCalculation(): void {
    this.ctx.clearRect(0, 0, this.width, this.height);
    this.duration = 'working...';
    performance.clearMarks();
    performance.clearMeasures();
    performance.mark('start-mandelbrot');

    this.workX = 0;
    this.workY = 0;
    this.endCounter = 0;

    this.drawMandelbrotSet();
  }

  drawMandelbrotSet(): void {
    for (let w = 0; w < this.numberOfWorkers; w++) {
      this.workers[w].postMessage({
        startX: this.workX,
        startY: this.workY,
        width: 100,
        height: 100,
        totalWidth: this.width,
        totalHeight: this.height,
        maxIteration: this.maxIteration
      });
      this.workX += 100;
      if (this.workX === this.width) {
        this.workY += 100;
        this.workX = 0;
      }
    }
  }


  private handleWorkerMessage(worker: Worker, message: MessageEvent): void {
    const data = message.data;
    for (const point of data) {
      this.ctx.fillRect(point[0], point[1], 1, 1);
    }

    const last = data[data.length - 1];
    if (last) {
      this.progress = Math.round((last[0] + (last[1] * this.width)) * 100 / this.totalPixels) + ' %';
    }

    if (this.workY < this.height) {
      worker.postMessage({
        startX: this.workX,
        startY: this.workY,
        width: 100,
        height: 100,
        totalWidth: this.width,
        totalHeight: this.height,
        maxIteration: this.maxIteration
      });

      this.workX += 100;
      if (this.workX === this.width) {
        this.workY += 100;
        this.workX = 0;
      }
    } else {
      this.endCounter++;
      if (this.endCounter === this.numberOfWorkers) {
        performance.mark('end-mandelbrot');
        performance.measure('mandelbrot', 'start-mandelbrot', 'end-mandelbrot');
        this.duration = performance.getEntriesByName('mandelbrot')[0].duration + 'ms';
        this.progress = '100 %';
      }
    }
  }


}
