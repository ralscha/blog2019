const fs = require('fs');
const mnist = require('./mnist');


function display(digit) {
    for (let y = 0; y < 28; y++) {
        let s = '';
        for (let x = 0; x < 28; x++) {
            let ii = x + 28 * y;
            if (digit[ii] > 200) {
                s += 'X';
            }
            else if (digit[ii] > 100) {
                s += 'x';
            }
            else if (digit[ii] > 0) {
                s += '.';
            }
            else {
                s += ' ';
            }
        }
        console.log(s);
    }
}

async function run() {

    const args = process.argv.slice(2)

    const digits = await mnist.getTrainImages();
    const labels = await mnist.getTrainLabels();

    const imageSize = 28 * 28;

    let ix;
    if (args.length > 0) {
        ix = parseInt(args[0], 10);
    }
    else {
        ix = Math.floor(Math.random() * (labels.length + 1));
    }

    const start = ix * imageSize;
    const digit = digits.slice(start, start + imageSize);
    console.log(labels[ix]);
    display(digit);

    const rotated1 = mnist.rotate(digit, 10);
    display(rotated1);

    const rotated2 = mnist.rotate(digit, -10);
    display(rotated2);
}

run();