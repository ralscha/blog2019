import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, ViewChild} from '@angular/core';
import {createWorker, RecognizeResult} from 'tesseract.js';
import {NgProgressComponent} from '@ngx-progressbar/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage implements AfterViewInit {

  @ViewChild('fileSelector') fileInput: ElementRef;
  @ViewChild('canvas') canvas: ElementRef;
  @ViewChild('canvasContainer') canvasContainer: ElementRef;
  @ViewChild(NgProgressComponent) progressBar: NgProgressComponent;

  result: any;
  words: any[];
  symbols: any[];
  selectedLine = null;
  selectedWord = null;
  selectedSymbol = null;
  elementColumns: string[] = ['text', 'confidence'];
  progressStatus: string;
  progress: number;
  language = 'eng';
  private ctx: CanvasRenderingContext2D;
  private selectedFile: File;
  private image: any;
  private ratio: number;

  constructor(private readonly changeDetectionRef: ChangeDetectorRef) {
  }

  ngAfterViewInit(): void {
    this.ctx = this.canvas.nativeElement.getContext('2d');
  }

  clickFileSelector() {
    this.fileInput.nativeElement.click();
  }

  async onFileChange(event) {
    this.selectedFile = event.target.files[0];

    this.progressStatus = '';
    this.progress = null;

    this.result = null;
    this.words = null;
    this.symbols = null;
    this.selectedLine = null;
    this.selectedWord = null;
    this.selectedSymbol = null;

    this.image = new Image();
    this.image.onload = () => this.drawImageScaled(this.image);
    this.image.src = URL.createObjectURL(this.selectedFile);

    /*
    const worker = createWorker({
      logger: progress => {
        this.progressStatus = progress.status;
        this.progress = progress.progress;
        this.progressBar.set(progress.progress * 100);
        this.changeDetectionRef.markForCheck();
      }
    });
     */
    const worker = createWorker({
      workerPath: 'tesseract-201/worker.min.js',
      corePath: 'tesseract-201/tesseract-core.wasm.js',
      logger: progress => {
        this.progressStatus = progress.status;
        this.progress = progress.progress;
        this.progressBar.set(progress.progress * 100);
        this.changeDetectionRef.markForCheck();
      }
    });

    await worker.load();
    await worker.loadLanguage(this.language);
    await worker.initialize(this.language);

    this.progressBar.set(0);

    try {
      const result = await worker.recognize(this.selectedFile);
      if (result) {
        this.result = (result as RecognizeResult).data;
      }
      await worker.terminate();
    } catch (e) {
      console.log(e);
      this.progressStatus = e;
      this.progress = null;
    } finally {
      this.progressBar.complete();
      this.progressStatus = null;
      this.progress = null;
    }

    // reset file input
    event.target.value = null;
  }

  redrawImage() {
    if (this.image) {
      this.drawImageScaled(this.image);
    }
  }

  drawBBox(bbox: { x0: number, x1: number, y0: number, y1: number }) {
    if (bbox) {
      this.redrawImage();

      this.ctx.beginPath();
      this.ctx.moveTo(bbox.x0 * this.ratio, bbox.y0 * this.ratio);
      this.ctx.lineTo(bbox.x1 * this.ratio, bbox.y0 * this.ratio);
      this.ctx.lineTo(bbox.x1 * this.ratio, bbox.y1 * this.ratio);
      this.ctx.lineTo(bbox.x0 * this.ratio, bbox.y1 * this.ratio);
      this.ctx.closePath();
      this.ctx.strokeStyle = '#bada55';
      this.ctx.lineWidth = 2;
      this.ctx.stroke();
    }
  }

  onLineClick(line) {
    this.words = line.words;

    this.drawBBox(line.bbox);

    this.symbols = null;
    this.selectedLine = line;
    this.selectedWord = null;
    this.selectedSymbol = null;
  }

  onWordClick(word) {
    this.symbols = word.symbols;

    this.drawBBox(word.bbox);

    this.selectedWord = word;
    this.selectedSymbol = null;
  }

  onSymbolClick(symbol) {
    this.drawBBox(symbol.bbox);

    this.selectedSymbol = symbol;
  }

  private drawImageScaled(img) {
    const width = this.canvasContainer.nativeElement.clientWidth;
    const height = this.canvasContainer.nativeElement.clientHeight;

    const hRatio = width / img.width;
    const vRatio = height / img.height;
    this.ratio = Math.min(hRatio, vRatio);
    if (this.ratio > 1) {
      this.ratio = 1;
    }

    this.canvas.nativeElement.width = img.width * this.ratio;
    this.canvas.nativeElement.height = img.height * this.ratio;

    this.ctx.clearRect(0, 0, width, height);
    this.ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, img.width * this.ratio, img.height * this.ratio);
  }

}
