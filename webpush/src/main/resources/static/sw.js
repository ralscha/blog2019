self.addEventListener('install', event => event.waitUntil(self.skipWaiting()));

self.addEventListener('activate', event => event.waitUntil(clients.claim()));

self.addEventListener('push', event => event.waitUntil(handlePushEvent(event)));

self.addEventListener('notificationclick', event => event.waitUntil(handleNotificationClick(event)));

self.addEventListener('notificationclose', event => console.info('notificationclose event fired'));

async function handlePushEvent(event) {
  console.info('push event emitted');

  const needToShow = await needToShowNotification();
  const dataCache = await caches.open('data');

  if (!event.data) {
    console.info('dad joke received without payload');

    if (needToShow) {
      await self.registration.showNotification('Dad Joke', {
        body: 'A new joke has arrived',
        tag: 'dad-joke',
        icon: 'dad-joke.png',
        data: { url: urlToOpen }
      });
    }

    const response = await fetch('lastDadJoke');
    const dadJoke = await response.text();

    await dataCache.put('dad-joke', new Response(dadJoke));
  }
  else {
    console.info('chuck joke received');

    const msg = event.data.json();

    if (needToShow) {
      await self.registration.showNotification(msg.title, {
        body: msg.body,
        icon: 'chuck.png',
        data: { url: urlToOpen }
      });
    }

    await dataCache.put('joke', new Response(msg.body));
  }

  const allClients = await clients.matchAll({ includeUncontrolled: true });
  for (const client of allClients) {
    client.postMessage('data-updated');
  }
}

const urlToOpen = new URL('/index.html', self.location.origin).href;
const alternateUrlToOpen = new URL('/', self.location.origin).href;

async function handleNotificationClick(event) {
  const targetUrl = event.notification.data?.url ?? urlToOpen;

  let openClient = null;
  const allClients = await clients.matchAll({ includeUncontrolled: true, type: 'window' });
  for (const client of allClients) {
    if (client.url === targetUrl || client.url === alternateUrlToOpen) {
      openClient = client;
      break;
    }
  }

  if (openClient) {
    await openClient.focus();
  } else {
    const newClient = await clients.openWindow(targetUrl);
    if (newClient) {
      await newClient.focus();
    }
  }

  event.notification.close();
}

async function needToShowNotification() {
  const allClients = await clients.matchAll({ includeUncontrolled: true, type: 'window' });
  for (const client of allClients) {
    if (client.visibilityState === 'visible') {
      return false;
    }
  }
  return true;
}
