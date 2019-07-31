self.addEventListener('message', messageEvent => {
  const data = messageEvent.data;
  const result = data.a + data.b;
  postMessage(result);
});
