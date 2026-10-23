const fs = require('fs');
const mnist = require('./mnist');
const brain = require('brain.js');

class AugmentedNetwork extends brain.NeuralNetwork {
    train(data, options) {
        const augmented = data.flatMap(({ input, output }) => [
            { input, output },
            { input: mnist.rotate(input, 10), output },
            { input: mnist.rotate(input, -10), output }
        ]);
        return super.train(augmented, options);
    }
}


async function run() {

    const trainLabels = await mnist.getTrainLabels();
    const trainDigits = await mnist.getTrainImages();

    const testLabels = await mnist.getTestLabels();
    const testDigits = await mnist.getTestImages();

    const labels = trainLabels;
    const digits = trainDigits;

    const imageSize = 28 * 28;
    const trainingData = [];

    for (let ix = 0; ix < labels.length; ix++) {
        const start = ix * imageSize;
        const input = digits.slice(start, start + imageSize).map(mnist.normalize);
        const output = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
        output[labels[ix]] = 1;
        trainingData.push({ input, output });

    }

    const netOptions = {
        hiddenLayers: [150]
    };

    const trainingOptions = {
        iterations: 20000,
        log: details => console.log(details)
    };

    /*
    const net = new brain.NeuralNetwork(netOptions);
    
    const result = net.train(trainingData, {log: true});   
    const model = net.toJSON();
    fs.writeFile(dataDir + 'model.json', JSON.stringify(model), 'utf8', ()=>console.log('model has been written'));
    */


    const crossValidate = new brain.CrossValidate(AugmentedNetwork, netOptions);
    const stats = crossValidate.train(trainingData, trainingOptions);
    const net = crossValidate.toNeuralNetwork();

    console.log('Cross-validation statistics:', stats.stats);
    let correct = 0;
    for (let ix = 0; ix < testLabels.length; ix++) {
        const start = ix * imageSize;
        const input = testDigits.slice(start, start + imageSize).map(mnist.normalize);
        const output = Array.from(net.run(input));
        if (output.indexOf(Math.max(...output)) === testLabels[ix]) {
            correct++;
        }
    }
    console.log(`Held-out test accuracy: ${(100 * correct / testLabels.length).toFixed(2)}%`);

    const model = net.toJSON();
    fs.writeFile('./data/model.json', JSON.stringify(model), 'utf8', () => console.log('model has been written'));
}

run();