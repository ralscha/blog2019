import ky from 'ky';

async function demo() {
  // bodyMethods();
  // error();
  // timeout();
  retry();
  // postBody();
  // abort();
  // hooks();
  // customDefaults();
}

async function bodyMethods() {
  console.log('with fetch');
  let response = await fetch('http://localhost:8080/simple-get');
  let body = await response.text();
  console.log('body: ', body);

  console.log('without body method');
  response = await ky('http://localhost:8080/simple-get');
  body = await response.text();
  console.log('body: ', body);

  console.log('without body method');
  response = await ky.get('http://localhost:8080/simple-get');
  body = await response.text();
  console.log('body: ', body);

  console.log('with json()');
  body = await ky.get('http://localhost:8080/simple-get').json();
  console.log('body: ', body);

  console.log('with text()');
  body = await ky.get('http://localhost:8080/simple-get').text();
  console.log('body: ', body);

  console.log('with formData()');
  body = await ky.get('http://localhost:8080/simple-get').formData();
  console.log('body: ', body.get('response'));

  console.log('with arrayBuffer()');
  body = await ky.get('http://localhost:8080/simple-get').arrayBuffer();
  console.log('body: ', body);

  console.log('with blob()');
  body = await ky.get('http://localhost:8080/simple-get').blob();
  console.log('body: ', body);

  console.log('with arrayBuffer() and accept header, overrides header');
  body = await ky.get('http://localhost:8080/simple-get', { headers: { Accept: 'application/octet-stream' } }).arrayBuffer();
  console.log('body: ', body);

  console.log('with arrayBuffer() and accept header');
  response = await ky.get('http://localhost:8080/simple-get', { headers: { Accept: 'application/octet-stream' } });
  body = await response.arrayBuffer();
  console.log('body: ', body);

  console.log('with blob() and accept header');
  response = await ky.get('http://localhost:8080/simple-get', { headers: { Accept: 'application/octet-stream' } });
  body = await response.blob();
  console.log('body: ', body);
}

async function timeout() {
  let response;

  console.log('ky, default, timeout 10s');
  try {
    console.log('request ', Date.now());
    response = await ky.get('http://localhost:8080/timeout');
  } catch (e) {
    console.log('response', Date.now());
    console.log(e);
  }

  console.log('ky, timeout 1s');
  try {
    console.log('request ', Date.now());
    response = await ky.get('http://localhost:8080/timeout', { timeout: 1000 });
  } catch (e) {
    console.log('response', Date.now());
    console.log(e);
  }

  console.log('ky, timeout disabled');
  console.log('request ', Date.now());
  response = await ky.get('http://localhost:8080/timeout', { timeout: false });
  console.log('response', Date.now());
}

async function error() {
  console.log('fetch');
  let response = await fetch('http://localhost:8080/notfound');
  console.log("response.ok = ", response.ok);

  console.log('ky, default');
  try {
    response = await ky.get('http://localhost:8080/notfound');
  } catch (e) {
    console.log('ky.get', e);
  }

  console.log('ky, throwHttpErrors = false');
  response = await ky.get('http://localhost:8080/notfound', { throwHttpErrors: false });
  console.log("response.ok = ", response.ok);
}

async function retry() {
  try {
    const response = await ky.get('http://localhost:8080/retry-test').text();
  } catch (e) {
    console.log(e);
  }
  try {
    const response = await ky.get('http://localhost:8080/retry-test', { retry: 0 }).text();
  } catch (e) {
    console.log(e);
  }

  let response = await ky.get('http://localhost:8080/retry').text();
  response = await ky.get('http://localhost:8080/retry').text();

  response = await ky.get('http://localhost:8080/retry', { retry: 5 }).text();

  response = await ky.get('http://localhost:8080/retry', {
    retry: {
      limit: 10,
      methods: ['get'],
      afterStatusCodes: [429]
    }
  }).text();

}

async function postBody() {
  console.log('fetch');
  let response = await fetch('http://localhost:8080/simple-post', {
    method: 'POST',
    body: JSON.stringify({ value: "hello world" }),
    headers: {
      'content-type': 'application/json'
    }
  });

  if (response.ok) {
    const body = await response.json();
    console.log(body);
  }

  console.log('ky');
  const body = await ky.post('http://localhost:8080/simple-post', { json: { value: "hello world" } }).json();
  console.log(body);


  console.log('ky, post multipart/form-data');
  const formData = new FormData();
  formData.append('value1', '10');
  formData.append('value2', 'ten');

  await ky.post('http://localhost:8080/multipart-post', {
    body: formData
  });

  console.log('ky, post application/x-www-form-urlencoded');
  const searchParams = new URLSearchParams();
  searchParams.set('value1', '10');
  searchParams.set('value2', 'ten');

  await ky.post('http://localhost:8080/formurlencoded-post', {
    body: searchParams
  });
}

async function abort() {
  const controller = new AbortController();

  setTimeout(() => {
    console.log('abort', Date.now());
    controller.abort();
  }, 2500);

  try {
    console.log('request', Date.now());
    const body = await ky.get('http://localhost:8080/timeout', { signal: controller.signal }).text();
  } catch (error) {
    console.log('exception', Date.now());
    if (error.name === 'AbortError') {
      console.log('Fetch aborted');
    } else {
      console.error('Fetch error:', error);
    }
  }
}

async function hooks() {

  const hooks = {
    beforeRequest: [
      (request, options) => {
        console.log('before request');
        options.headers.set('x-api-key', '1111');
      }
    ],
    afterResponse: [
      (request, options, response) => {
        console.log('after response');
        console.log(response);
        // return different response
        return new Response('{"value": "something else"}', { status: 200 });
      }
    ]
  };


  const body = await ky.get('http://localhost:8080/simple-get', { hooks }).json();
  console.log('body: ', body);
}

async function customDefaults() {
  console.log('default ky');
  let body = await ky.get('simple-get', { prefixUrl: 'http://localhost:8080' }).text();
  console.log(body);

  console.log('custom ky with defaults');
  const customKy = ky.create({ prefixUrl: 'http://localhost:8080' });
  body = await customKy('simple-get').text();
  console.log(body);

  const customApiKy = customKy.extend({ headers: { 'x-api-key': '1111' } });
  body = await customApiKy('simple-get').text();
  console.log(body);
}

async function downloadProgress() {
  await ky.get('http://localhost:8080/download', {
    onDownloadProgress: (progress, chunk) => {
      console.log(`${progress.percent * 100}% - ${progress.transferredBytes} of ${progress.totalBytes} bytes`);
    }
  });
}

demo();
