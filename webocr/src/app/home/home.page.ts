import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, ViewChild} from '@angular/core';
import Tesseract from 'tesseract.js';
import {NgProgressComponent} from '@ngx-progressbar/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage implements AfterViewInit {

  @ViewChild('fileSelector', {static: false}) fileInput: ElementRef;
  @ViewChild('canvas', {static: false}) canvas: ElementRef;
  @ViewChild('canvasContainer', {static: false}) canvasContainer: ElementRef;
  @ViewChild(NgProgressComponent, {static: false}) progressBar: NgProgressComponent;

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

  onFileChange(event) {
    this.selectedFile = event.target.files[0];

    const worker = new Tesseract.TesseractWorker({
      workerPath: 'tesseract-200alpha13/worker.min.js',
      corePath: 'tesseract-200alpha13/tesseract-core.wasm.js'
    });
    this.progressStatus = '';
    this.progress = null;

    this.result = null;
    this.words = null;
    this.symbols = null;
    this.selectedLine = null;
    this.selectedWord = null;
    this.selectedSymbol = null;

    worker.detect(this.selectedFile).progress(progressEvent => {
      this.progressStatus = progressEvent.status;
      this.progress = progressEvent.progress;
    }).then(result => {
      console.log(result);
    });

    this.progressBar.set(0);

    worker
      .recognize(this.selectedFile, this.language)
      .progress(progressEvent => {
        this.progressStatus = progressEvent.status;
        this.progress = progressEvent.progress;

        this.progressBar.set(progressEvent.progress * 100);
        this.changeDetectionRef.detectChanges();
      })
      .catch(error => {
        this.progressStatus = error;
        this.progress = null;
      })
      .then(result => {
        this.result = result;
        worker.terminate();
      })
      .finally(() => {
        this.progressBar.complete();
        this.progressStatus = null;
        this.progress = null;
      });

    this.image = new Image();
    this.image.onload = async () => {
      this.drawImageScaled(this.image);
    };
    this.image.src = URL.createObjectURL(this.selectedFile);
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
