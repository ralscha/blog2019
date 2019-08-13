const subscribeButton = document.getElementById('subscribeButton');
const unsubscribeButton = document.getElementById('unsubscribeButton');

const factOutput = document.getElementById('fact');
const jokeOutput = document.getElementById('joke');

if ("serviceWorker" in navigator) {
  try {
	checkSubscription();
    init();
  } catch (e) {
    console.error('error init(): ' + e);
  }

  subscribeButton.addEventListener('click', () => {
	  subscribe().catch(e => {
		  if (Notification.permission === 'denied') {
	         console.warn('Permission for notifications was denied');
	      } else {
	    	 console.error('error subscribe(): ' + e);
	      }   
	  });
  });

  unsubscribeButton.addEventListener('click', () => {
	unsubscribe().catch(e => console.error('error unsubscribe(): ' + e)); 
  });
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
  fetch('/publicSigningKey')
     .then(response => response.arrayBuffer())
     .then(key => this.publicSigningKey = key)
     .finally(() => console.info('Application Server Public Key fetched from the server'));

  await navigator.serviceWorker.register("/sw.js", {
    scope: "/"
  });

  await navigator.serviceWorker.ready;
  console.info('Service Worker has been installed and is ready');
  navigator.serviceWorker.addEventListener('message', event => displayLastMessages());

  displayLastMessages();
}

function displayLastMessages() {
  caches.open('data').then(dataCache => {
    dataCache.match('fact')
      .then(response => response ? response.text() : '')
      .then(txt => factOutput.innerText = txt);
    
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
    applicationServerKey: this.publicSigningKey
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
