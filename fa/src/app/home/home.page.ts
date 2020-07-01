import {Component, OnDestroy, OnInit} from '@angular/core';
import {faHandPointLeft, faHandPointRight} from '@fortawesome/free-regular-svg-icons';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage implements OnInit, OnDestroy {
  faHandPointLeft = faHandPointLeft;
  faHandPointRight = faHandPointRight;

  syncRunning = false;
  magicLevel = 0;

  color = 'red';
  stroke = 'red';
  strokeWidth = 0;
  opacity = '1';

  private interval: number;

  ngOnDestroy(): void {
    clearInterval(this.interval);
  }

  ngOnInit(): void {
    this.interval = setInterval(() => {
      this.color = this.randomColor();
      this.stroke = this.randomColor();
      this.strokeWidth = this.randomWidth();
      this.opacity = '' + Math.random();
    }, 1000);
  }

  dynamicStyle() {
    return {
      opacity: '' + Math.random(),
      color: this.randomColor(),
      stroke: this.randomColor(),
      'stroke-width': this.randomWidth() + 'px'
    };
  }

  private randomWidth(): number {
    return Math.floor(Math.random() * 30) + 1;
  }

  private randomColor(): string {
    return '#' + (0x1000000 + (Math.random()) * 0xffffff).toString(16).substr(1, 6);
  }

  updateMagicLevel($event: Event) {
    this.magicLevel = parseInt(($event.target as HTMLInputElement).value, 10);
  }
}
