import {Component} from '@angular/core';
import {LoadingController, ToastController} from '@ionic/angular';
import {HttpClient} from '@angular/common/http';
import {CameraResultType, CameraSource, FileReadResult, Plugins} from '@capacitor/core';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {environment} from '../../environments/environment';
import {catchError, finalize} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {Upload} from 'tus-js-client';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage {

  public tus = false;
  private counter = 0;
  public error: string;
  private loading: any;
  photo: SafeResourceUrl;

  constructor(private readonly http: HttpClient,
              private readonly sanitizer: DomSanitizer,
              private readonly loadingCtrl: LoadingController,
              private readonly toastCtrl: ToastController) {
  }

  async takePhoto() {
    const ab = await this.getPhoto(CameraSource.Camera);
    if (this.tus) {
      await this.uploadTus(ab);
    } else {
      await this.uploadAll(ab);
    }
  }

  async selectPhoto() {
    const ab = await this.getPhoto(CameraSource.Photos);
    if (this.tus) {
      await this.uploadTus(ab);
    } else {
      await this.uploadAll(ab);
    }
  }

  private async getPhoto(source: CameraSource) {
    const image = await Plugins.Camera.getPhoto({
      quality: 90,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      source
    });

    this.photo = this.sanitizer.bypassSecurityTrustResourceUrl(image && (image.webPath));
    return image.webPath;
  }

  private readFile(webPath: string): Promise<FileReadResult> {
    try {
      return Plugins.Filesystem.readFile({
        path: webPath
      });
    } catch (e) {
      console.log(e);
    }
  }

  private async uploadAll(webPath: string) {
    this.loading = await this.loadingCtrl.create({
      message: 'Uploading...'
    });
    await this.loading.present();

    const blob = await fetch(webPath).then(r => r.blob());

    const formData = new FormData();
    formData.append('file', blob, `file-${this.counter++}.jpg`);
    this.http.post<boolean>(`${environment.serverUrl}/uploadAll`, formData)
      .pipe(
        catchError(e => this.handleError(e)),
        finalize(() => this.loading.dismiss())
      )
      .subscribe(ok => this.showToast(ok));
  }

  private async uploadTus(webPath: string) {

    this.loading = await this.loadingCtrl.create({
      message: 'Uploading...'
    });
    await this.loading.present();

    const blob = await fetch(webPath).then(r => r.blob());
    const upload = new Upload(blob, {
      endpoint: `${environment.serverUrl}/upload`,
      retryDelays: [0, 3000, 6000, 12000, 24000],
      chunkSize: 512 * 1024,
      metadata: {
        filename: `file-${this.counter++}.jpg`
      },
      onError: () => {
        this.showToast(false);
        this.loading.dismiss();
      },
      onSuccess: () => {
        this.showToast(true);
        this.loading.dismiss();
      }
    });

    upload.start();
  }

  private async showToast(ok: boolean) {
    if (ok) {
      const toast = await this.toastCtrl.create({
        message: 'Upload successful',
        duration: 3000,
        position: 'top'
      });
      toast.present();
    } else {
      const toast = await this.toastCtrl.create({
        message: 'Upload failed',
        duration: 3000,
        position: 'top'
      });
      toast.present();
    }
  }

  private handleError(error: any) {
    const errMsg = error.message ? error.message : error.toString();
    this.error = errMsg;
    return throwError(errMsg);
  }

}
