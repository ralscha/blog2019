import * as Comlink from 'comlink';

function fibonacci(num) {
  let a = 1;
  let b = 0;

  while (num >= 0) {
    const temp = a;
    a = a + b;
    b = temp;
    num--;
  }

  return b;
}

Comlink.expose(fibonacci);
