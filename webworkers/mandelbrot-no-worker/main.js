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

document.getElementById('startButton').addEventListener('click', () => {
  progressElement.innerText = '0 %';
  ctx.clearRect(0, 0, myCanvas.width, myCanvas.height);
  timeElement.innerText = 'working...';
  performance.clearMarks();
  performance.clearMeasures();
  performance.mark('start-mandelbrot');

  drawMandelbrotSet();

  performance.mark('end-mandelbrot');
  performance.measure('mandelbrot', 'start-mandelbrot', 'end-mandelbrot');
  timeElement.innerText = performance.getEntriesByName('mandelbrot')[0].duration + 'ms';
  progressElement.innerText = '100 %';
});

document.getElementById('clearButton').addEventListener('click', () => {
  progressElement.innerText = '0 %';
  timeElement.innerHTML = '&nbsp;';
  ctx.clearRect(0, 0, myCanvas.width, myCanvas.height);
});

function drawMandelbrotSet() {
  for (let iy = 0; iy < height; iy++) {
    for (let ix = 0; ix < width; ix++) {
      const cRe = (ix - width / 2.0) * 4.0 / width;
      const cIm = (iy - height / 2.0) * 4.0 / width;
      let x = 0;
      let y = 0;

      let iteration = 0;
      while (x * x + y * y <= 4 && iteration < maxIteration) {
        const xNew = x * x - y * y + cRe;
        y = 2 * x * y + cIm;
        x = xNew;
        iteration++;
      }
      if (iteration >= maxIteration) {
        ctx.fillRect(ix, iy, 1, 1);
      }
      progress++;
    }
    progressElement.innerText = progress * 100 / totalPixels + ' %';
  }
}
