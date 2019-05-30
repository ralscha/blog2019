import {Component} from '@angular/core';
import { faHandPointLeft, faHandPointRight } from '@fortawesome/free-regular-svg-icons';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage {
  faHandPointLeft = faHandPointLeft;
  faHandPointRight = faHandPointRight;

  syncRunning = false;
  magicLevel = 0;
}
