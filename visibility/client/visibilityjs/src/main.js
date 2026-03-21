const serverUrl = 'http://localhost:8080';

const stopConsoleTimer = createVisibilityInterval({
  visibleInterval: 1000,
  callback: state => console.log(`tick while ${state}`)
});

window.setTimeout(stopConsoleTimer, 5000);

createVisibilityInterval({
  visibleInterval: 15 * 1000,
  hiddenInterval: 60 * 1000,
  callback: async state => {
    console.log(`poll while ${state}`);
    await fetch(`${serverUrl}/poll`);
  }
});

runWhenVisible(() => {
  console.log('The page is visible');
  updateTitle(document.visibilityState);
});

subscribeToVisibilityChange(state => {
  console.log('visibilitychange event', state);
  updateTitle(state);
});

updateTitle(document.visibilityState);

function runWhenVisible(callback) {
  if (!document.hidden) {
    callback(document.visibilityState);
    return () => {};
  }

  const handleChange = () => {
    if (document.hidden) {
      return;
    }

    document.removeEventListener('visibilitychange', handleChange);
    callback(document.visibilityState);
  };

  document.addEventListener('visibilitychange', handleChange);

  return () => document.removeEventListener('visibilitychange', handleChange);
}

function createVisibilityInterval({
  visibleInterval,
  hiddenInterval = visibleInterval,
  callback
}) {
  let timeoutId = 0;
  let stopped = false;

  const scheduleNext = () => {
    if (stopped) {
      return;
    }

    const delay = document.hidden ? hiddenInterval : visibleInterval;
    timeoutId = window.setTimeout(() => {
      void runCallback();
    }, delay);
  };

  const runCallback = async () => {
    if (stopped) {
      return;
    }

    await callback(document.visibilityState);
    scheduleNext();
  };

  const handleChange = () => {
    window.clearTimeout(timeoutId);
    scheduleNext();
  };

  scheduleNext();
  document.addEventListener('visibilitychange', handleChange);

  return () => {
    stopped = true;
    window.clearTimeout(timeoutId);
    document.removeEventListener('visibilitychange', handleChange);
  };
}

function subscribeToVisibilityChange(callback) {
  const handleChange = () => callback(document.visibilityState);

  document.addEventListener('visibilitychange', handleChange);

  return () => document.removeEventListener('visibilitychange', handleChange);
}

function updateTitle(state) {
  document.title = `Visibility helpers (${state})`;
}

