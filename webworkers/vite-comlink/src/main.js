import {proxy, releaseProxy, wrap} from 'comlink';
import PlainWorker from './worker-plain.js?worker';
import ComlinkWorker from './worker.js?worker';
import ComlinkProxyWorker from './worker-proxy.js?worker';

const outputElement = document.getElementById('output');

document.getElementById('startPlainButton').addEventListener('click', () => startPlainCalculation());
document.getElementById('startButton').addEventListener('click', () => startCalculation());
document.getElementById('startProxyButton').addEventListener('click', () => startCalculationWithProxy());

function startPlainCalculation() {
  const worker = new PlainWorker();
  writeOutput('\u00a0');
  worker.addEventListener('message', ({data}) => {
    writeOutput(data);
    worker.terminate();
  }, {once: true});
  worker.postMessage(1000);
}

async function startCalculation() {
  const worker = new ComlinkWorker();
  const fibonacci = wrap(worker);
  writeOutput('\u00a0');

  try {
    const result = await fibonacci(1000);
    writeOutput(result);
  } finally {
    fibonacci[releaseProxy]();
    worker.terminate();
  }
}

async function startCalculationWithProxy() {
  const worker = new ComlinkProxyWorker();
  const fibonacci = wrap(worker);

  try {
    await fibonacci(1000, proxy(writeOutput));
  } finally {
    fibonacci[releaseProxy]();
    worker.terminate();
  }
}

function writeOutput(value) {
  outputElement.textContent = String(value);
}
