package ch.rasc.httpclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class WebSocketDemo {
  public static void main(String[] args) throws InterruptedException {


    Listener wsListener = new Listener() {
      @Override
      public CompletionStage<?> onText(WebSocket webSocket,
          CharSequence data, boolean last) {

        System.out.println("onText: " + data);

        return Listener.super.onText(webSocket, data, last);
      }

      @Override
      public void onOpen(WebSocket webSocket) {
        System.out.println("onOpen");
        Listener.super.onOpen(webSocket);
      }

      @Override
      public CompletionStage<?> onClose(WebSocket webSocket, int statusCode,
          String reason) {
        System.out.println("onClose: " + statusCode + " " + reason);
        return Listener.super.onClose(webSocket, statusCode, reason);
      }
    };

    var client = HttpClient.newHttpClient();

    WebSocket webSocket = client.newWebSocketBuilder()
    		       .buildAsync(URI.create("wss://localhost:8443/wsEndpoint"), wsListener).join();
    webSocket.sendText("hello from the client", true);
    
    TimeUnit.SECONDS.sleep(30);
    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "ok");
  }
}
