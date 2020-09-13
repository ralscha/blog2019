import {Component} from '@angular/core';
import {LoadingController, ToastController} from '@ionic/angular';
import {HttpClient} from '@angular/common/http';
import {CameraResultType, CameraSource, Plugins} from '@capacitor/core';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {environment} from '../../environments/environment';
import {catchError, finalize} from 'rxjs/operators';
import {Observable, throwError} from 'rxjs';
import {Upload} from 'tus-js-client';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage {

  public tus = false;
  public error: string | null = null;
  photo: SafeResourceUrl | null = null;
  private counter = 0;
  private loading: HTMLIonLoadingElement | null = null;

  constructor(private readonly http: HttpClient,
              private readonly sanitizer: DomSanitizer,
              private readonly loadingCtrl: LoadingController,
              private readonly toastCtrl: ToastController) {
  }

  async takePhoto(): Promise<void> {
    const ab = await this.getPhoto(CameraSource.Camera);
    if (ab) {
      if (this.tus) {
        await this.uploadTus(ab);
      } else {
        await this.uploadAll(ab);
      }
    }
  }

  async selectPhoto(): Promise<void> {
    const ab = await this.getPhoto(CameraSource.Photos);
    if (ab) {
      if (this.tus) {
        await this.uploadTus(ab);
      } else {
        await this.uploadAll(ab);
      }
    }
  }

  private async getPhoto(source: CameraSource): Promise<string | undefined> {
    const image = await Plugins.Camera.getPhoto({
      quality: 90,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      source
    });

    if (image.webPath) {
      this.photo = this.sanitizer.bypassSecurityTrustResourceUrl(image.webPath);
    }
    return image.webPath;
  }

  private async uploadAll(webPath: string): Promise<void> {
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
        finalize(() => this.loading?.dismiss())
      )
      .subscribe(ok => this.showToast(ok));
  }

  private async uploadTus(webPath: string): Promise<void> {

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
        this.loading?.dismiss();
      },
      onSuccess: () => {
        this.showToast(true);
        this.loading?.dismiss();
      }
    });

    upload.start();
  }

  private async showToast(ok: boolean): Promise<void> {
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

  // tslint:disable-next-line:no-any
  private handleError(error: any): Observable<never> {
    const errMsg = error.message ? error.message : error.toString();
    this.error = errMsg;
    return throwError(errMsg);
  }

}
