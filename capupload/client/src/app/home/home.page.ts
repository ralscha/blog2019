import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import {
  IonButton,
  IonCol,
  IonContent,
  IonGrid,
  IonHeader,
  IonIcon,
  IonItem,
  IonRow,
  IonText,
  IonTitle,
  IonToggle,
  IonToolbar,
  LoadingController,
  ToastController,
} from '@ionic/angular';
import { HttpClient } from '@angular/common/http';
import { Camera, MediaResult, MediaTypeSelection } from '@capacitor/camera';
import { environment } from '../../environments/environment';
import { catchError, finalize, Observable, throwError } from 'rxjs';
import { Upload } from 'tus-js-client';
import { addIcons } from 'ionicons';
import { camera, image } from 'ionicons/icons';

@Component({
  changeDetection: ChangeDetectionStrategy.Eager,
  selector: 'app-home',
  templateUrl: './home.page.html',
  imports: [
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonGrid,
    IonRow,
    IonCol,
    IonItem,
    IonToggle,
    IonButton,
    IonIcon,
    IonText,
  ],
})
export class HomePage {
  readonly tus = signal(false);
  readonly error = signal<string | null>(null);
  readonly photo = signal<string | null>(null);
  private readonly http = inject(HttpClient);
  private readonly loadingCtrl = inject(LoadingController);
  private readonly toastCtrl = inject(ToastController);
  private counter = 0;
  private loading: HTMLIonLoadingElement | null = null;

  constructor() {
    addIcons({ image, camera });
  }

  async takePhoto(): Promise<void> {
    await this.handlePhotoSelection(() => Camera.takePhoto({ quality: 90 }));
  }

  async selectPhoto(): Promise<void> {
    await this.handlePhotoSelection(async () => {
      const result = await Camera.chooseFromGallery({ mediaType: MediaTypeSelection.Photo, allowMultipleSelection: false });
      return result.results[0];
    });
  }

  private async handlePhotoSelection(select: () => Promise<MediaResult | undefined>): Promise<void> {
    this.error.set(null);
    try {
      const image = await select();
      if (!image?.webPath) {
        return;
      }
      this.photo.set(image.webPath);
      if (this.tus()) {
        await this.uploadTus(image.webPath);
      } else {
        await this.uploadAll(image.webPath);
      }
    } catch (error) {
      this.error.set(error instanceof Error ? error.message : String(error));
      await this.loading?.dismiss();
    }
  }

  private fileName(blob: Blob): string {
    const extensions: Record<string, string> = {
      'image/jpeg': 'jpg', 'image/png': 'png', 'image/webp': 'webp',
      'image/gif': 'gif', 'image/heic': 'heic', 'image/heif': 'heif',
    };
    return `file-${this.counter++}.${extensions[blob.type] ?? 'bin'}`;
  }

  private async uploadAll(webPath: string): Promise<void> {
    this.loading = await this.loadingCtrl.create({
      message: 'Uploading...',
    });
    await this.loading.present();

    const blob = await fetch(webPath).then((r) => r.blob());

    const formData = new FormData();
    formData.append('file', blob, this.fileName(blob));
    this.http
      .post<boolean>(`${environment.serverUrl}/uploadAll`, formData)
      .pipe(
        catchError((e) => this.handleError(e)),
        finalize(() => this.loading?.dismiss()),
      )
      .subscribe({
        next: (ok) => this.showToast(ok),
        error: () => this.showToast(false),
      });
  }

  private async uploadTus(webPath: string): Promise<void> {
    this.loading = await this.loadingCtrl.create({
      message: 'Uploading...',
    });
    await this.loading.present();

    const blob = await fetch(webPath).then((r) => r.blob());
    const upload = new Upload(blob, {
      endpoint: `${environment.serverUrl}/upload`,
      retryDelays: [0, 3000, 6000, 12000, 24000],
      chunkSize: 512 * 1024,
      metadata: {
        filename: this.fileName(blob),
      },
      onError: () => {
        this.showToast(false);
        this.loading?.dismiss();
      },
      onSuccess: () => {
        this.showToast(true);
        this.loading?.dismiss();
      },
    });

    upload.start();
  }

  private async showToast(ok: boolean): Promise<void> {
    if (ok) {
      const toast = await this.toastCtrl.create({
        message: 'Upload successful',
        duration: 3000,
        position: 'top',
      });
      toast.present();
    } else {
      const toast = await this.toastCtrl.create({
        message: 'Upload failed',
        duration: 3000,
        position: 'top',
      });
      toast.present();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private handleError(error: any): Observable<never> {
    const errMsg = error.message ? error.message : error.toString();
    this.error.set(errMsg);
    return throwError(() => error);
  }
}
