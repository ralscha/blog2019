import {Component, ViewChild} from '@angular/core';
import {DrawableDirective} from '../drawable.directive';
// @ts-ignore
import * as brain from 'brain.js/browser';
import * as tf from '@tensorflow/tfjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage {

  @ViewChild(DrawableDirective) drawable!: DrawableDirective;

  detectionsMLP: number[] = [];
  detectedNumberMLP: number | null = null;

  detectionsCNN: Float32Array | null = null;
  detectedNumberCNN: number | null = null;

  private net: brain.NeuralNetwork = null;
  private tfModel!: tf.LayersModel;

  constructor() {
    this.initBrain();
    this.initTf();
  }

  async initBrain(): Promise<void> {
    const response = await fetch('assets/model.json');
    const brainModel = await response.json();
    this.net = new brain.NeuralNetwork();
    this.net.fromJSON(brainModel);
  }

  async initTf(): Promise<void> {
    this.tfModel = await tf.loadLayersModel('assets/tfjsmnist/model.json');
  }

  // tslint:disable-next-line:no-any
  detect(canvas: any): void {
    const canvasCopy = document.createElement('canvas');
    canvasCopy.width = 28;
    canvasCopy.height = 28;

    const copyContext = canvasCopy.getContext('2d');
    if (!copyContext) {
      throw new Error('can\'t get 2d context');
    }

    const ratioX = canvas.width / 28;
    const ratioY = canvas.height / 28;
    const drawBox = this.drawable.getDrawingBox();
    const scaledSourceWidth = Math.min(20, Math.max(4, ((drawBox[2] - drawBox[0] + 32) / ratioX)));
    const scaledSourceHeight = Math.min(20, ((drawBox[3] - drawBox[1] + 32) / ratioY));
    const dx = (28 - scaledSourceWidth) / 2;
    const dy = (28 - scaledSourceHeight) / 2;

    copyContext.drawImage(canvas, drawBox[0] - 16, drawBox[1] - 16, drawBox[2] - drawBox[0] + 16, drawBox[3] - drawBox[1] + 16,
      dx, dy, scaledSourceWidth, scaledSourceHeight);
    const imageData = copyContext.getImageData(0, 0, 28, 28);

    const numPixels = imageData.width * imageData.height;
    const values = new Array<number>(numPixels);
    for (let i = 0; i < numPixels; i++) {
      values[i] = imageData.data[i * 4 + 3] / 255.0;
    }

    // CNN with Tensorflow.js
    const predictTensor = this.tfModel.predict(tf.tensor4d(values, [1, 28, 28, 1])) as tf.Tensor;
    const data = predictTensor.dataSync<'float32'>();
    this.detectedNumberCNN = this.indexMax(data);
    this.detectionsCNN = data;
    // console.log(tf.argMax(predictTensor, 1).dataSync());

    // MLP with brain.js
    const detection = this.net.run(values);
    this.detectedNumberMLP = this.maxScore(detection);

    this.detectionsMLP = [];
    for (let i = 0; i <= 9; i++) {
      this.detectionsMLP.push(detection[i]);
    }

  }

  erase(): void {
    this.detectionsMLP = [];
    this.detectedNumberMLP = null;
    this.detectionsCNN = null;
    this.detectedNumberCNN = null;

    this.drawable.clear();
  }

  maxScore(obj: { [key: number]: number }): number {
    let maxKey = 0;
    let maxValue = 0;

    Object.entries(obj).forEach(entry => {
      const value = entry[1];
      if (value > maxValue) {
        maxValue = value;
        maxKey = parseInt(entry[0], 10);
      }
    });

    return maxKey;
  }

  private indexMax(data: Float32Array): number {
    let indexMax = 0;
    for (let r = 0; r < data.length; r++) {
      indexMax = data[r] > data[indexMax] ? r : indexMax;
    }

    return indexMax;
  }

}
