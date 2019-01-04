const fs = require('fs');
const brain = require('brain.js');
const mnist = require('./mnist');

function maxScore(obj) {
    let maxKey = 0;
    let maxValue = 0;

    Object.entries(obj).forEach(entry => {
        let key = entry[0];
        let value = entry[1];
        if (value > maxValue) {
            maxValue = value;
            maxKey = parseInt(key, 10);
        }
    });
    
    return maxKey;
}
async function run() {
    const networkModel = JSON.parse(fs.readFileSync('./data/model.json', 'utf8'));

    const net = new brain.NeuralNetwork();
    net.fromJSON(networkModel);

    const testDigits = await mnist.getTestImages();
    const testLabels = await mnist.getTestLabels();

    const imageSize = 28 * 28;
    let errors = 0;

    for (let ix = 0; ix < testLabels.length; ix++) {
        const start = ix * imageSize;
        const input = testDigits.slice(start, start + imageSize).map(mnist.normalize);

        const detection = net.run(input);        
        const max = maxScore(detection);
        //console.log(max, testLabels[ix]);
        if (max !== testLabels[ix]) {
            errors++;
        }
    }

    console.log(`Total           : ${testLabels.length}`);
    console.log(`Number of errors: ${errors}`);
    console.log(`Accuracy:       : ${(testLabels.length - errors) * 100 / testLabels.length} %`);
}

run();