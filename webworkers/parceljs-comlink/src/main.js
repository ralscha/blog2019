import * as Comlink from 'comlink';

document.getElementById('startPlainButton').addEventListener('click', () => startPlainCalculation());
document.getElementById('startButton').addEventListener('click', () => startCalculation());
document.getElementById('startProxyButton').addEventListener('click', () => startCalculationWithProxy());

function startPlainCalculation() {
  const worker = new Worker('./worker-plain.js');
  writeOutput('&nbsp;');
  worker.addEventListener('message', messageEvent => writeOutput(messageEvent.data));
  worker.postMessage(1000);
}

async function startCalculation() {
  const fibonacci = Comlink.wrap(new Worker('./worker.js'));
  writeOutput('&nbsp;');
  const result = await fibonacci(1000);
  writeOutput(result);
}

function startCalculationWithProxy() {
  const fibonacci = Comlink.wrap(new Worker('./worker-proxy.js'));
  fibonacci(1000, Comlink.proxy(writeOutput));
}

const outputElement = document.getElementById('output');

function writeOutput(value) {
  outputElement.innerHTML = value;
}
