const subscribeButton = document.getElementById('subscribeButton');
const unsubscribeButton = document.getElementById('unsubscribeButton');

const dadJokeOutput = document.getElementById('dadJoke');
const jokeOutput = document.getElementById('joke');

let publicSigningKey;

if ('serviceWorker' in navigator && 'PushManager' in window) {
  subscribeButton.disabled = true;

  startApplication().catch(error => {
    console.error('error init(): ' + error);
  });

  subscribeButton.addEventListener('click', () => {
    subscribe().catch(error => {
      if (Notification.permission === 'denied') {
        console.warn('Permission for notifications was denied');
      }
      else {
        console.error('error subscribe(): ' + error);
      }
    });
  });

  unsubscribeButton.addEventListener('click', () => {
    unsubscribe().catch(error => console.error('error unsubscribe(): ' + error));
  });
}

async function startApplication() {
  await init();
  const subscribed = await checkSubscription();
  subscribeButton.disabled = subscribed;
  unsubscribeButton.disabled = !subscribed;
}

async function checkSubscription() {
  const registration = await navigator.serviceWorker.ready;
  const subscription = await registration.pushManager.getSubscription();
  if (subscription) {

    const response = await fetch("/isSubscribed", {
      method: 'POST',
      body: JSON.stringify({endpoint: subscription.endpoint}),
      headers: {
        "content-type": "application/json"
      }
    });
    const subscribed = await response.json();

    if (subscribed) {
      subscribeButton.disabled = true;
      unsubscribeButton.disabled = false;
    }

    return subscribed;
  }

  return false;
}

async function init() {
  await navigator.serviceWorker.register("/sw.js", {
    scope: "/"
  });

  await navigator.serviceWorker.ready;
  console.info('Service Worker has been installed and is ready');
  navigator.serviceWorker.addEventListener('message', () => displayLastMessages());

  const response = await fetch('/publicSigningKey');
  publicSigningKey = await response.arrayBuffer();
  console.info('Application Server Public Key fetched from the server');

  displayLastMessages();
}

function displayLastMessages() {
  caches.open('data').then(dataCache => {
    dataCache.match('dad-joke')
      .then(response => response ? response.text() : '')
      .then(txt => dadJokeOutput.innerText = txt);

    dataCache.match('joke')
      .then(response => response ? response.text() : '')
      .then(txt => jokeOutput.innerText = txt);
  });
}

async function unsubscribe() {
  const registration = await navigator.serviceWorker.ready;
  const subscription = await registration.pushManager.getSubscription();
  if (subscription) {
    const successful = await subscription.unsubscribe();
    if (successful) {
      console.info('Unsubscription successful');

      await fetch("/unsubscribe", {
        method: 'POST',
        body: JSON.stringify({endpoint: subscription.endpoint}),
        headers: {
          "content-type": "application/json"
        }
      });

      console.info('Unsubscription info sent to the server');

      subscribeButton.disabled = false;
      unsubscribeButton.disabled = true;
    }
    else {
      console.error('Unsubscription failed');
    }
  }
}

async function subscribe() {
  const registration = await navigator.serviceWorker.ready;
  const subscription = await registration.pushManager.subscribe({
    userVisibleOnly: true,
    applicationServerKey: publicSigningKey
  });

  console.info(`Subscribed to Push Service: ${subscription.endpoint}`);

  await fetch("/subscribe", {
    method: 'POST',
    body: JSON.stringify(subscription),
    headers: {
      "content-type": "application/json"
    }
  });

  console.info('Subscription info sent to the server');

  subscribeButton.disabled = true;
  unsubscribeButton.disabled = false;
}
