import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {load, ObjectDetection, ObjectDetectionBaseModel} from '@tensorflow-models/coco-ssd';
import {LoadingController} from '@ionic/angular';
import '@tensorflow/tfjs-backend-webgl';

@Component({
  selector: 'app-object-detection',
  templateUrl: './object-detection.page.html',
  styleUrls: ['./object-detection.page.scss']
})
export class ObjectDetectionPage implements OnInit {

  baseModel: ObjectDetectionBaseModel = 'lite_mobilenet_v2';

  @ViewChild('fileSelector') fileInput!: ElementRef;
  @ViewChild('canvas', {static: true}) canvas!: ElementRef;
  @ViewChild('canvasContainer') canvasContainer!: ElementRef;

  private ratio: number | null = null;
  private modelPromise: Promise<ObjectDetection>;
  private ctx!: CanvasRenderingContext2D;
  private url: string | null = null;

  constructor(private readonly loadingController: LoadingController) {
    this.modelPromise = load({base: this.baseModel});
  }

  ngOnInit(): void {
    this.ctx = this.canvas.nativeElement.getContext('2d');
  }

  baseModelChange(event: Event): void {
    this.baseModel = (event as CustomEvent).detail.value;
    this.modelPromise = load({base: this.baseModel});
    if (this.url) {
      this.detect(this.url);
    }
  }

  onFileChange(event: Event): void {
    // @ts-ignore
    this.url = URL.createObjectURL(event.target.files[0]);
    this.detect(this.url);
  }

  detect(url: string): void {
    const img = new Image();
    img.onload = async () => {
      this.drawImageScaled(img);
      const loading = await this.loadingController.create({
        message: 'Detecting...'
      });
      await loading.present();

      const model = await this.modelPromise;
      const predictions = await model.detect(img);
      console.log(predictions);

      if (this.ratio === null) {
        throw new Error('ratio not set');
      }

      for (const prediction of predictions) {
        const [x, y, width, height] = prediction.bbox;

        this.ctx.beginPath();
        this.ctx.moveTo(x * this.ratio, y * this.ratio);
        this.ctx.lineTo((x + width) * this.ratio, y * this.ratio);
        this.ctx.lineTo((x + width) * this.ratio, (y + height) * this.ratio);
        this.ctx.lineTo(x * this.ratio, (y + height) * this.ratio);
        this.ctx.closePath();
        this.ctx.strokeStyle = '#bada55';
        this.ctx.lineWidth = 3;
        this.ctx.stroke();

        this.ctx.font = '10px Arial';
        this.ctx.fillStyle = 'yellow';
        this.ctx.fillText(prediction.class, (x + 4) * this.ratio, (y + 15) * this.ratio);
      }

      loading.dismiss();
    };
    img.src = url;
  }

  clickFileSelector(): void {
    this.fileInput.nativeElement.click();
  }

  // tslint:disable-next-line:no-any
  drawImageScaled(img: any): void {
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
    this.ctx.drawImage(img, 0, 0, img.width, img.height,
      0, 0, img.width * this.ratio, img.height * this.ratio);
  }

}
