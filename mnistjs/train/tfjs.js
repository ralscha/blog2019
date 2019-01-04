const mnist = require('./mnist');
const tf = require('@tensorflow/tfjs');
require('@tensorflow/tfjs-node');

// https://github.com/tensorflow/tfjs-examples/blob/master/mnist/index.js
function createConvModel() {
    const model = tf.sequential();

    model.add(tf.layers.conv2d({
        inputShape: [28, 28, 1],
        kernelSize: 3,
        filters: 16,
        activation: 'relu'
    }));

    model.add(tf.layers.maxPooling2d({ poolSize: 2, strides: 2 }));
    model.add(tf.layers.conv2d({ kernelSize: 3, filters: 32, activation: 'relu' }));
    model.add(tf.layers.maxPooling2d({ poolSize: 2, strides: 2 }));
    model.add(tf.layers.conv2d({ kernelSize: 3, filters: 32, activation: 'relu' }));
    model.add(tf.layers.flatten({}));
    model.add(tf.layers.dense({ units: 64, activation: 'relu' }));
    model.add(tf.layers.dense({ units: 10, activation: 'softmax' }));

    return model;
}

async function run() {

    const trainLabels = await mnist.getTrainLabels();
    const trainDigits = await mnist.getTrainImages();

    const testLabels = await mnist.getTestLabels();
    const testDigits = await mnist.getTestImages();

    const trainDigitsTensor = tf.tensor4d(trainDigits.map(mnist.normalize),
        [trainDigits.length / (28 * 28), 28, 28, 1]);

    const testDigitsTensor = tf.tensor4d(testDigits.map(mnist.normalize),
        [testDigits.length / (28 * 28), 28, 28, 1]);

    const outputs = [];
    for (let ix = 0; ix < trainLabels.length; ix++) {
        const output = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
        output[trainLabels[ix]] = 1;
        outputs.push(output);
    }
    const trainLabelsTensor = tf.tensor2d(outputs);

    const testOutputs = [];
    for (let ix = 0; ix < testLabels.length; ix++) {
        const output = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
        output[testLabels[ix]] = 1;
        testOutputs.push(output);
    }
    const testLabelsTensor = tf.tensor2d(testOutputs);

    const model = createConvModel();

    model.compile({
        optimizer: 'rmsprop',
        loss: 'categoricalCrossentropy',
        metrics: ['accuracy'],
    });

    const batchSize = 320;
    const trainEpochs = 20;

    await model.fit(trainDigitsTensor, trainLabelsTensor, {
        batchSize,
        validationData: [testDigitsTensor, testLabelsTensor],
        shuffle: true,
        epochs: trainEpochs
    });

    const testResult = model.evaluate(testDigitsTensor, testLabelsTensor);
    const testAccPercent = testResult[1].dataSync()[0] * 100;
    console.log(`Final test accuracy: ${testAccPercent.toFixed(1)}%`);

    const saveResults = await model.save('file://./data/tfjsmnist');
}

run();