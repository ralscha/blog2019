import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import * as cocoSsd from '@tensorflow-models/coco-ssd';
import {ObjectDetection} from '@tensorflow-models/coco-ssd';
import {LoadingController} from '@ionic/angular';

@Component({
  selector: 'app-object-detection',
  templateUrl: './object-detection.page.html',
  styleUrls: ['./object-detection.page.scss']
})
export class ObjectDetectionPage implements OnInit {

  baseModel = 'lite_mobilenet_v2';

  @ViewChild('fileSelector', {static: false}) fileInput: ElementRef;
  @ViewChild('canvas', {static: true}) canvas: ElementRef;
  @ViewChild('canvasContainer', {static: false}) canvasContainer: ElementRef;

  private ratio: number;
  private readonly modelPromise: Promise<ObjectDetection>;
  private ctx: CanvasRenderingContext2D;
  private url: string = null;

  constructor(private readonly loadingController: LoadingController) {
    this.modelPromise = cocoSsd.load();
  }

  ngOnInit() {
    this.ctx = this.canvas.nativeElement.getContext('2d');
  }

  baseModelChange(event) {
    this.baseModel = event.detail.value;
    if (this.url) {
      this.detect(this.url);
    }
  }

  onFileChange(event) {
    this.url = URL.createObjectURL(event.target.files[0]);
    this.detect(this.url);
  }

  detect(url: string) {
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

  clickFileSelector() {
    this.fileInput.nativeElement.click();
  }

  drawImageScaled(img) {
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
