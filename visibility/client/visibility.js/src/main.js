import Visibility from 'visibilityjs';

const everyTimer = Visibility.every(1000, () => console.log('every 1000ms'));
setTimeout(() => Visibility.stop(everyTimer), 5000);

Visibility.every(15 * 1000, 60 * 1000, s => fetch('http://localhost:8080/poll'));

Visibility.onVisible(() => {
  console.log('onVisible event');
  document.title = 'Visibility.js (visible)';
});

Visibility.change((e, state) => {
  console.log('change event', state);
  updateTitle();
});

updateTitle();

function updateTitle() {
  if (Visibility.hidden()) {
    document.title = 'Visibility.js (hidden)';
  } else {
    document.title = 'Visibility.js (visible)';
  }
}

