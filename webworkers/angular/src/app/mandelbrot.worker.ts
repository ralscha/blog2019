/// <reference lib="webworker" />

self.addEventListener('message', messageEvent => {
  computeMandelbrotSet((messageEvent as MessageEvent).data);
});

function computeMandelbrotSet({startX, startY, width, height, totalWidth, totalHeight, maxIteration}:
                              {
                                startX: number, startY: number, width: number, height: number,
                                totalWidth: number, totalHeight: number, maxIteration: number
                              }): void {
  const result = [];
  for (let iy = startY; iy < startY + height; iy++) {
    for (let ix = startX; ix < startX + width; ix++) {
      const cRe = (ix - totalWidth / 2.0) * 4.0 / totalWidth;
      const cIm = (iy - totalHeight / 2.0) * 4.0 / totalWidth;
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
        result.push([ix, iy]);
      }
    }
  }

  postMessage(result);
}


