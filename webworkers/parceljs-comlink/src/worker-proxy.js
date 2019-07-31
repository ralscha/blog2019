import * as Comlink from 'comlink';

function fibonacci(num, outputFunction) {
  let a = 1;
  let b = 0;

  outputFunction('&nbsp;');

  while (num >= 0) {
    const temp = a;
    a = a + b;
    b = temp;
    num--;
    outputFunction(b);
  }

}

Comlink.expose(fibonacci);
