import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import * as posenet from '@tensorflow-models/posenet';
import {LoadingController} from '@ionic/angular';

@Component({
  selector: 'app-posenet',
  templateUrl: './posenet.page.html',
  styleUrls: ['./posenet.page.scss'],
})
export class PosenetPage implements OnInit {

  @ViewChild('fileSelector') fileInput: ElementRef;
  @ViewChild('canvas', {static: true}) canvas: ElementRef;
  @ViewChild('canvasContainer') canvasContainer: ElementRef;
  ratio: number;
  modelPromise: Promise<any>;
  private ctx: CanvasRenderingContext2D;

  constructor(private readonly loadingController: LoadingController) {
    this.modelPromise = posenet.load();
  }

  ngOnInit() {
    this.ctx = this.canvas.nativeElement.getContext('2d');
  }

  onFileCange(event) {
    const url = URL.createObjectURL(event.target.files[0]);
    const img = new Image();
    img.onload = async () => {
      this.drawImageScaled(img);
      const loading = await this.loadingController.create({
        message: 'Estimating...'
      });
      await loading.present();
      await this.estimate(img);
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

  private async estimate(img) {
    const flipHorizontal = false;

    const model = await this.modelPromise;
    const poses = await model.estimatePoses(img, {
      flipHorizontal,
      decodingMethod: 'single-person'
    });
    const pose = poses && poses[0];

    if (pose && pose.keypoints) {
      for (const keypoint of pose.keypoints.filter(kp => kp.score >= 0.2)) {
        const x = keypoint.position.x * this.ratio;
        const y = keypoint.position.y * this.ratio;

        this.ctx.beginPath();
        this.ctx.arc(x, y, 5, 0, 2 * Math.PI, false);
        this.ctx.lineWidth = 3;
        this.ctx.strokeStyle = '#bada55';
        this.ctx.stroke();
      }

      const adjacentKeyPoints = posenet.getAdjacentKeyPoints(pose.keypoints, 0.2);
      adjacentKeyPoints.forEach(keypoints => this.drawSegment(keypoints[0].position, keypoints[1].position));
    }
  }

  private drawSegment({y: ay, x: ax}, {y: by, x: bx}) {
    this.ctx.beginPath();
    this.ctx.moveTo(ax * this.ratio, ay * this.ratio);
    this.ctx.lineTo(bx * this.ratio, by * this.ratio);
    this.ctx.lineWidth = 2;
    this.ctx.strokeStyle = '#bada55';
    this.ctx.stroke();
  }
}
