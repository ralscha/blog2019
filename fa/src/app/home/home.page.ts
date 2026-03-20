import {Component, OnDestroy, OnInit} from '@angular/core';
import {faHandPointLeft, faHandPointRight} from '@fortawesome/free-regular-svg-icons';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {
  IonButton,
  IonCard,
  IonCardContent,
  IonContent,
  IonHeader,
  IonTitle,
  IonToolbar
} from "@ionic/angular/standalone";

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
  imports: [FontAwesomeModule, IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonCard, IonCardContent]
})
export class HomePage implements OnInit, OnDestroy {
  readonly faHandPointLeft = faHandPointLeft;
  readonly faHandPointRight = faHandPointRight;

  syncRunning = false;
  magicLevel = 0;
  magicTransform = 'rotate-0';

  solidBellStyle = this.createRandomIconStyle();
  regularBellStyle = this.createRandomIconStyle();

  private intervalId: ReturnType<typeof window.setInterval> | undefined;

  ngOnDestroy(): void {
    if (this.intervalId !== undefined) {
      window.clearInterval(this.intervalId);
    }
  }

  ngOnInit(): void {
    this.intervalId = window.setInterval(() => {
      this.solidBellStyle = this.createRandomIconStyle();
      this.regularBellStyle = this.createRandomIconStyle();
    }, 1000);
  }

  updateMagicLevel(event: Event): void {
    this.magicLevel = Number((event.target as HTMLInputElement).value);
    this.magicTransform = `rotate-${this.magicLevel}`;
  }

  private createRandomIconStyle(): { color: string; 'stroke-width': string; opacity: string; stroke: string } {
    return {
      color: this.randomColor(),
      stroke: this.randomColor(),
      'stroke-width': `${this.randomWidth()}px`,
      opacity: Math.random().toString()
    };
  }

  private randomWidth(): number {
    return Math.floor(Math.random() * 30) + 1;
  }

  private randomColor(): string {
    return `#${Math.floor(Math.random() * 0xffffff)
      .toString(16)
      .padStart(6, '0')}`;
  }
}
