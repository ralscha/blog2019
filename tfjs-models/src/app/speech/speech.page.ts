import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import * as speechCommands from '@tensorflow-models/speech-commands';
import {NavigationEnd, Router} from '@angular/router';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-speech',
  templateUrl: './speech.page.html',
  styleUrls: ['./speech.page.scss'],
})
export class SpeechPage implements OnInit, OnDestroy {

  @ViewChild('canvas', {static: true}) canvas: ElementRef;
  private subscription: Subscription;
  private recognizer;
  private wordLabels: string[];
  private ctx: CanvasRenderingContext2D;

  private snakeSize = 10;
  private width = 300;
  private height = 350;
  private score = 0;
  private snake;
  private food;
  private direction: string;
  private gameloop;

  private whitelistWords = ['down', 'go', 'left', 'right', 'stop', 'up'];
  private whitelistIndex: number[] = [];

  constructor(private readonly router: Router) {
    this.recognizer = speechCommands.create('BROWSER_FFT');
    this.recognizer.ensureModelLoaded().then(() => {
      this.wordLabels = this.recognizer.wordLabels();
      this.whitelistIndex = this.wordLabels.map((value, index) => {
        if (this.whitelistWords.indexOf(value) !== -1) {
          return index;
        }
      }).filter(value => value !== undefined);
    });
  }

  ngOnInit() {
    this.onEnter();

    this.subscription = this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        if (event.url === '/tabs/speech') {
          this.onEnter();
        } else {
          this.onLeave();
        }
      }
    });
  }

  onEnter(): void {
    this.ctx = this.canvas.nativeElement.getContext('2d');
    this.start();
  }

  onLeave(): void {
    this.stop();
  }

  ngOnDestroy(): void {
    this.onLeave();
    this.subscription.unsubscribe();
  }

  start() {
    this.recognizer.listen(result => {

      const ix = result.scores.reduce((bestIndexSoFar, currentValue, currentIndex) => {
        if (this.whitelistIndex.indexOf(currentIndex) !== -1) {
          return currentValue > result.scores[bestIndexSoFar] ? currentIndex : bestIndexSoFar;
        }
        return bestIndexSoFar;
      }, 0);

      const bestWord = this.wordLabels[ix];

      switch (bestWord) {
        case 'right': {
          if (this.direction !== 'left') {
            this.direction = 'right';
          }
          break;
        }
        case 'left': {
          if (this.direction !== 'right') {
            this.direction = 'left';
          }
          break;
        }
        case 'up': {
          if (this.direction !== 'down') {
            this.direction = 'up';
          }
          break;
        }
        case 'down': {
          if (this.direction !== 'up') {
            this.direction = 'down';
          }
          break;
        }
        case 'stop': {
          this.stopGame();
          break;
        }
        case 'go': {
          this.stopGame();
          this.init();
          break;
        }
      }
    }, {
      includeSpectrogram: false,
      probabilityThreshold: 0.75
    });
  }

  stop() {
    if (this.recognizer.isListening()) {
      this.recognizer.stopListening();
      this.stopGame();
    }
  }

  stopGame() {
    if (this.gameloop) {
      clearInterval(this.gameloop);
      this.gameloop = null;
    }
  }

  drawSnake(x, y) {
    this.ctx.fillStyle = 'green';
    this.ctx.fillRect(x * this.snakeSize, y * this.snakeSize, this.snakeSize, this.snakeSize);
    this.ctx.strokeStyle = 'darkgreen';
    this.ctx.strokeRect(x * this.snakeSize, y * this.snakeSize, this.snakeSize, this.snakeSize);
  }

  drawFood(x, y) {
    this.ctx.fillStyle = 'yellow';
    this.ctx.fillRect(x * this.snakeSize, y * this.snakeSize, this.snakeSize, this.snakeSize);
    this.ctx.fillStyle = 'red';
    this.ctx.fillRect(x * this.snakeSize + 1, y * this.snakeSize + 1, this.snakeSize - 2, this.snakeSize - 2);
  }

  drawScore() {
    const scoreText = 'Score: ' + this.score;
    this.ctx.fillStyle = 'blue';
    this.ctx.fillText(scoreText, 145, this.height - 5);
  }

  initSnake() {
    const length = 4;
    this.snake = [];
    for (let i = length - 1; i >= 0; i--) {
      this.snake.push({x: i, y: 0});
    }
  }

  paint() {
    this.ctx.fillStyle = 'lightgrey';
    this.ctx.fillRect(0, 0, this.width, this.height);
    this.ctx.strokeStyle = 'black';
    this.ctx.strokeRect(0, 0, this.width, this.height);

    let snakeX = this.snake[0].x;
    let snakeY = this.snake[0].y;

    if (this.direction === 'right') {
      snakeX++;
    } else if (this.direction === 'left') {
      snakeX--;
    } else if (this.direction === 'up') {
      snakeY--;
    } else if (this.direction === 'down') {
      snakeY++;
    }

    if (snakeX === -1) {
      snakeX = this.width / this.snakeSize;
    } else if (snakeY === -1) {
      snakeY = this.height / this.snakeSize;
    } else if (snakeY === this.height / this.snakeSize) {
      snakeY = 0;
    } else if (snakeX === this.width / this.snakeSize) {
      snakeX = 0;
    }

    let tail;
    if (snakeX === this.food.x && snakeY === this.food.y) {
      tail = {x: snakeX, y: snakeY};
      this.score++;

      this.createFood();
    } else {
      tail = this.snake.pop();
      tail.x = snakeX;
      tail.y = snakeY;
    }

    this.snake.unshift(tail);

    for (const sn of this.snake) {
      this.drawSnake(sn.x, sn.y);
    }

    this.drawFood(this.food.x, this.food.y);
    this.drawScore();
  }

  createFood() {
    this.food = {
      x: Math.floor((Math.random() * 30) + 1),
      y: Math.floor((Math.random() * 30) + 1)
    };

    for (let i = 0; i > this.snake.length; i++) {
      const snakeX = this.snake[i].x;
      const snakeY = this.snake[i].y;

      if (this.food.x === snakeX && this.food.y === snakeY || this.food.y === snakeY && this.food.x === snakeX) {
        this.food.x = Math.floor((Math.random() * 30) + 1);
        this.food.y = Math.floor((Math.random() * 30) + 1);
      }
    }
  }

  init() {
    this.direction = 'down';
    this.initSnake();
    this.createFood();
    this.gameloop = setInterval(this.paint.bind(this), 80);
  }

}
