import {Component, ElementRef, ViewChild} from '@angular/core';
import * as mobilenet from '@tensorflow-models/mobilenet';
import {LoadingController} from '@ionic/angular';

@Component({
  selector: 'app-mobilenet',
  templateUrl: './mobilenet.page.html',
  styleUrls: ['./mobilenet.page.scss']
})
export class MobilenetPage {
  @ViewChild('image') image: ElementRef;

  @ViewChild('fileSelector') fileInput: ElementRef;

  modelPromise: Promise<any>;

  predictions: Promise<Array<{ className: string; probability: number }>>;

  constructor(private readonly loadingController: LoadingController) {
    this.modelPromise = mobilenet.load();
  }

  clickFileSelector() {
    this.fileInput.nativeElement.click();
  }

  onFileCange(event) {
    this.image.nativeElement.src = URL.createObjectURL(event.target.files[0]);
    this.predict();
  }

  private async predict() {
    this.predictions = null;

    const loading = await this.loadingController.create({
      message: 'Predicting...'
    });
    await loading.present();

    const model = await this.modelPromise;
    this.predictions = model.classify(this.image.nativeElement, 4).then(predictions => {
      loading.dismiss();
      console.log(predictions);
      return predictions;
    });
  }

}
