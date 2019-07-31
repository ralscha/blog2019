const myCanvas = document.getElementById('myCanvas');
const ctx = myCanvas.getContext('2d');
ctx.fillStyle = 'black';
const timeElement = document.getElementById('time');
const progressElement = document.getElementById('progress');
let progress = 0;

const height = myCanvas.height;
const width = myCanvas.width;
const maxIteration = 20000;
const totalPixels = height * width;

const worker = new Worker('mandelbrot-batch.js');
worker.addEventListener('message', message => {
  const data = message.data;
  if (data !== null) {
    for (let i = 0; i < data.length; i++) {
      ctx.fillRect(data[i][0], data[i][1], 1, 1);
    }
    const last = data[data.length - 1];
    progress = last[0] + (last[1] * width);
    progressElement.innerText = Math.round(progress * 100 / totalPixels) + ' %';
  } else {
    performance.mark('end-mandelbrot');
    performance.measure('mandelbrot', 'start-mandelbrot', 'end-mandelbrot');
    timeElement.innerText = performance.getEntriesByName('mandelbrot')[0].duration + 'ms';
    progressElement.innerText = '100 %';
  }
});

document.getElementById('startButton').addEventListener('click', () => {
  progressElement.innerText = '0 %';
  ctx.clearRect(0, 0, myCanvas.width, myCanvas.height);
  timeElement.innerText = 'working...';
  performance.clearMarks();
  performance.clearMeasures();
  performance.mark('start-mandelbrot');

  worker.postMessage({ height, width, maxIteration });
});

