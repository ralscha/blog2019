const myCanvas = document.getElementById('myCanvas');
const ctx = myCanvas.getContext('2d');
ctx.fillStyle = 'black';
const timeElement = document.getElementById('time');
const progressElement = document.getElementById('progress');
let progress = 0;

let workX = 0;
let workY = 0;
let endCounter = 0;

const height = myCanvas.height;
const width = myCanvas.width;
const maxIteration = 20000;
const numberOfWorkers = 4;
const totalPixels = height * width;

const workers = [];

for (let w = 0; w < numberOfWorkers; w++) {
  workers[w] = new Worker('mandelbrot-multi-transfer.js');

  workers[w].addEventListener('message', message => {
    const data = message.data;
    const arrayIterator = data.values();
    let result = arrayIterator.next();
    let x;
    let y;
    while (!result.done) {
      x = result.value;
      y = arrayIterator.next().value;
      ctx.fillRect(x, y, 1, 1);
      result = arrayIterator.next();
    }

    if (x) {
      progress = x + (y * width);
      progressElement.innerText = Math.round(progress * 100 / totalPixels) + ' %';
    }

    if (workY < height) {
      workers[w].postMessage({
        startX: workX,
        startY: workY,
        width: 100,
        height: 100,
        totalWidth: width,
        totalHeight: height,
        maxIteration
      });

      workX += 100;
      if (workX === width) {
        workY += 100;
        workX = 0;
      }
    } else {
      endCounter++;
      if (endCounter === numberOfWorkers) {
        performance.mark('end-mandelbrot');
        performance.measure('mandelbrot', 'start-mandelbrot', 'end-mandelbrot');
        timeElement.innerText = performance.getEntriesByName('mandelbrot')[0].duration + 'ms';
        progressElement.innerText = '100 %';
      }
    }
  });
}

document.getElementById('startButton').addEventListener('click', () => {
  progressElement.innerText = '0 %';
  ctx.clearRect(0, 0, myCanvas.width, myCanvas.height);
  timeElement.innerText = 'working...';
  performance.clearMarks();
  performance.clearMeasures();
  performance.mark('start-mandelbrot');

  workX = 0;
  workY = 0;
  endCounter = 0;

  for (let w = 0; w < numberOfWorkers; w++) {
    workers[w].postMessage({
      startX: workX,
      startY: workY,
      width: 100,
      height: 100,
      totalWidth: width,
      totalHeight: height,
      maxIteration
    });
    workX += 100;
    if (workX === width) {
      workY += 100;
      workX = 0;
    }
  }
});
