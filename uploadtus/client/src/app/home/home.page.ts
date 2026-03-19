import {ChangeDetectorRef, Component, ElementRef, inject, viewChild} from '@angular/core';
import {Upload} from 'tus-js-client';
import {
  IonButton,
  IonCol,
  IonContent,
  IonHeader,
  IonItem,
  IonLabel,
  IonRow,
  IonTitle,
  IonToolbar,
  ToastController
} from '@ionic/angular/standalone';
import {environment} from '../../environments/environment';
import {ProgressBarComponent} from '../progress-bar/progress-bar.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  imports: [ProgressBarComponent, IonHeader, IonToolbar, IonTitle, IonContent, IonItem, IonLabel, IonRow, IonCol, IonButton]
})
export class HomePage {
  recording = false;
  uploadProgress = 0;
  readonly videoElement = viewChild.required<ElementRef<HTMLVideoElement>>('videoElement');
  private readonly toastCtrl = inject(ToastController);
  private readonly changeDetectionRef = inject(ChangeDetectorRef);
  private mediaRecorder: MediaRecorder | null = null;
  private mediaStream: MediaStream | null = null;
  private recordedChunks: Blob[] = [];

  async start(): Promise<void> {
    this.recording = true;
    try {
      const stream = await navigator.mediaDevices.getUserMedia({video: true, audio: false});
      this.mediaStream = stream;
      this.videoElement().nativeElement.srcObject = stream;
      await this.videoElement().nativeElement.play();

      this.recordedChunks = [];
      const mimeType = this.getPreferredVideoMimeType();
      this.mediaRecorder = mimeType ? new MediaRecorder(stream, {mimeType}) : new MediaRecorder(stream);
      this.mediaRecorder.addEventListener('dataavailable', this.handleDataAvailable);
      this.mediaRecorder.start();
    }
    catch (error) {
      this.recording = false;
      console.error('Could not start video capture', error);
      await this.presentToast('Could not access the camera');
    }
  }

  stop(): void {
    this.recording = false;
    const mediaRecorder = this.mediaRecorder;
    if (mediaRecorder && mediaRecorder.state !== 'inactive') {
      mediaRecorder.addEventListener('stop', () => {
        const recordedBlob = new Blob(this.recordedChunks, {
          type: mediaRecorder.mimeType || 'video/webm'
        });
        this.resetRecorder();
        if (recordedBlob.size > 0) {
          this.uploadVideo(recordedBlob);
        }
      }, {once: true});
      mediaRecorder.stop();
    }
    else {
      this.resetRecorder();
    }

    this.videoElement().nativeElement.pause();
    this.videoElement().nativeElement.srcObject = null;
    this.mediaStream?.getTracks().forEach(track => track.stop());
    this.mediaStream = null;
  }

  takeSnapshot(): void {
    const canvas = document.createElement('canvas');
    canvas.width = 1280;
    canvas.height = 960;

    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.drawImage(this.videoElement().nativeElement, 0, 0, canvas.width, canvas.height);
      canvas.toBlob(this.uploadSnapshot.bind(this), 'image/jpeg', 1);
    }
  }

  private uploadVideo(blob: Blob): void {
    const f = new File([blob], `video_${Date.now()}.webm`, {
      type: 'video/webm'
    });

    this.uploadFile(f);
  }

  private uploadSnapshot(blob: Blob | null): void {
    if (blob) {
      const f = new File([blob], `snapshot_${Date.now()}.jpg`, {
        type: 'image/jpeg'
      });

      this.uploadFile(f);
    }
  }

  private async uploadFile(file: File): Promise<void> {
    this.uploadProgress = 0;

    const upload = new Upload(file, {
      endpoint: `${environment.serverURL}/upload`,
      retryDelays: [0, 3000, 6000, 12000, 24000],
      chunkSize: 20000,
      metadata: {
        filename: file.name,
        filetype: file.type
      },
      onError: async (error) => {
        await this.presentToast('Upload failed: ' + error);
      },
      onProgress: (bytesUploaded, bytesTotal) => {
        this.uploadProgress = Math.floor(bytesUploaded / bytesTotal * 100);
        this.changeDetectionRef.detectChanges();
      },
      onSuccess: async () => {
        this.uploadProgress = 100;
        this.changeDetectionRef.detectChanges();
        await this.presentToast('Upload successful');
      }
    });

    try {
      const previousUploads = await upload.findPreviousUploads();
      if (previousUploads.length > 0) {
        upload.resumeFromPreviousUpload(previousUploads[0]);
      }
      upload.start();
    }
    catch (error) {
      await this.presentToast('Could not initialize the upload');
      console.error('Could not initialize upload', error);
    }
  }

  private readonly handleDataAvailable = (event: BlobEvent): void => {
    if (event.data.size > 0) {
      this.recordedChunks.push(event.data);
    }
  };

  private getPreferredVideoMimeType(): string | undefined {
    const supportedTypes = [
      'video/webm;codecs=vp9',
      'video/webm;codecs=vp8',
      'video/webm'
    ];

    return supportedTypes.find((candidate) => MediaRecorder.isTypeSupported(candidate));
  }

  private async presentToast(message: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message,
      duration: 3000,
      position: 'top'
    });
    await toast.present();
  }

  private resetRecorder(): void {
    if (this.mediaRecorder) {
      this.mediaRecorder.removeEventListener('dataavailable', this.handleDataAvailable);
    }
    this.mediaRecorder = null;
    this.recordedChunks = [];
  }
}
