package ch.rasc.httpclient;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class DemoService {

	private final DemoWsHandler wsHandler;

	private int counter;

	public DemoService(DemoWsHandler wsHandler) {
		this.wsHandler = wsHandler;
		this.counter = 0;
	}

	@Scheduled(fixedDelay = 5_000L)
	public void send() {
		this.counter++;
		for (WebSocketSession session : this.wsHandler.getSessions().values()) {
			try {
				session.sendMessage(new TextMessage("from server: " + this.counter));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
