package ch.rasc.httpclient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class DemoWsHandler extends TextWebSocketHandler {

  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    System.out.println("connection established: " + session.getId());
    this.sessions.put(session.getId(), session);
    super.afterConnectionEstablished(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
      throws Exception {
    System.out.println("connection closed: " + session.getId());
    this.sessions.remove(session.getId());
    super.afterConnectionClosed(session, status);
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) {
    System.out.println("From client: " + message.getPayload());
  }

  public Map<String, WebSocketSession> getSessions() {
    return this.sessions;
  }
}