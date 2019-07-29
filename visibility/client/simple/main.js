document.addEventListener('visibilitychange', () => handleVisibility());

function handleVisibility() {
  if (document.visibilityState === 'hidden') {
    document.title = 'Visibility: hidden';
    fetch('http://localhost:8080/hidden');
  } else if (document.visibilityState === 'visible') {
    document.title = 'Visibility: visible';
    fetch('http://localhost:8080/visible');
  }
}

handleVisibility();
