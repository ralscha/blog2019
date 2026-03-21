const serverUrl = 'http://localhost:8080';

document.addEventListener('visibilitychange', handleVisibilityChange);

async function handleVisibilityChange() {
  const state = document.visibilityState;

  document.title = `Visibility: ${state}`;

  const endpoint = document.hidden ? 'hidden' : 'visible';
  await notifyServer(endpoint);
}

async function notifyServer(endpoint) {
  try {
    await fetch(`${serverUrl}/${endpoint}`, {
      method: 'GET',
      keepalive: true
    });
  } catch (error) {
    console.error('Failed to notify server', error);
  }
}

void handleVisibilityChange();
