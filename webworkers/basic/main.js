const worker = new Worker('worker.js');
worker.addEventListener('message', message => {
  console.log('result: ' + message.data);
});

worker.postMessage({ a: 1, b: 2 });
worker.postMessage({ a: 3, b: 6 });
worker.postMessage({ a: 4, b: 4 });
