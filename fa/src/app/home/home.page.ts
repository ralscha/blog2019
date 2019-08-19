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
  private interval: number;

  ngOnDestroy(): void {
    clearInterval(this.interval);
  }

  ngOnInit(): void {
    this.interval = setInterval(() => this.color = this.randomColor(), 1000);
  }

  dynamicStyle() {
    return {
      color: this.randomColor(),
      stroke: this.randomColor()
    };
  }

  private randomColor(): string {
    return '#' + (0x1000000 + (Math.random()) * 0xffffff).toString(16).substr(1, 6);
  }
}
