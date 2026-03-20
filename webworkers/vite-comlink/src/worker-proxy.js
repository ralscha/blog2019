import {expose} from 'comlink';

async function fibonacci(num, outputFunction) {
  let a = 1;
  let b = 0;

  await outputFunction('\u00a0');

  while (num >= 0) {
    const temp = a;
    a = a + b;
    b = temp;
    num--;
    await outputFunction(b);
  }

}

expose(fibonacci);
