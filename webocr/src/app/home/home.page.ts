import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, ViewChild} from '@angular/core';
import {createWorker, Line, Page, Symbol as TesseractSymbol, Word} from 'tesseract.js';
import {NgProgressComponent} from '@ngx-progressbar/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage implements AfterViewInit {

  @ViewChild('fileSelector') fileInput!: ElementRef;
  @ViewChild('canvas') canvas!: ElementRef;
  @ViewChild('canvasContainer') canvasContainer!: ElementRef;
  @ViewChild(NgProgressComponent) progressBar!: NgProgressComponent;

  result: Page | null = null;
  words: Word[] | null = null;
  symbols: TesseractSymbol[] | null = null;
  selectedLine: Line | null = null;
  selectedWord: Word | null = null;
  selectedSymbol: TesseractSymbol | null = null;
  elementColumns: string[] = ['text', 'confidence'];
  progressStatus: string | null = null;
  progress: number | null = null;
  language = 'eng';
  private ctx!: CanvasRenderingContext2D;
  private selectedFile: File | null = null;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private image: any | null = null;
  private ratio: number | null = null;

  constructor(private readonly changeDetectionRef: ChangeDetectorRef) {
  }

  ngAfterViewInit(): void {
    this.ctx = this.canvas.nativeElement.getContext('2d');
  }

  clickFileSelector(): void {
    this.fileInput.nativeElement.click();
  }

  async onFileChange(event: Event): Promise<void> {
    // @ts-ignore
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
      workerPath: 'tesseract-202/worker.min.js',
      corePath: 'tesseract-202/tesseract-core.wasm.js',
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
      if (this.selectedFile) {
        const recognizeResult = await worker.recognize(this.selectedFile);
        if (recognizeResult) {
          this.result = recognizeResult.data;
        } else {
          this.result = null;
        }
        await worker.terminate();
      }
    } catch (e) {
      this.progressStatus = e;
      this.progress = null;
    } finally {
      this.progressBar.complete();
      this.progressStatus = null;
      this.progress = null;
    }

    // reset file input
    // @ts-ignore
    event.target.value = null;
  }

  redrawImage(): void {
    if (this.image) {
      this.drawImageScaled(this.image);
    }
  }

  drawBBox(bbox: { x0: number, x1: number, y0: number, y1: number }): void {
    if (bbox) {
      this.redrawImage();

      if (this.ratio === null) {
        throw new Error('ratio not set');
      }

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

  onLineClick(line: Line): void {
    this.words = line.words;

    this.drawBBox(line.bbox);

    this.symbols = null;
    this.selectedLine = line;
    this.selectedWord = null;
    this.selectedSymbol = null;
  }

  onWordClick(word: Word): void {
    this.symbols = word.symbols;

    this.drawBBox(word.bbox);

    this.selectedWord = word;
    this.selectedSymbol = null;
  }

  onSymbolClick(symbol: TesseractSymbol): void {
    this.drawBBox(symbol.bbox);

    this.selectedSymbol = symbol;
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private drawImageScaled(img: any): void {
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
