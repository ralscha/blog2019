package ch.rasc.cettiatwo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import io.cettia.DefaultServer;
import io.cettia.Server;
import io.cettia.ServerSocketPredicates;
import io.cettia.asity.bridge.spring.webflux5.AsityHandlerFunction;
import io.cettia.asity.bridge.spring.webflux5.AsityWebSocketHandler;
import io.cettia.transport.http.HttpTransportServer;
import io.cettia.transport.websocket.WebSocketTransportServer;

@SpringBootApplication
@EnableWebFlux
public class Application {

  @Bean
  public Server defaultServer() {
    Server server = new DefaultServer();

    server.onsocket(socket -> {
      socket.on("update", msg -> {
        server.find(ServerSocketPredicates.id(socket).negate()).send("update", msg);
      });
    });

    return server;
  }

  @Bean
  public RouterFunction<ServerResponse> httpMapping(Server defaultServer) {
    HttpTransportServer hts = new HttpTransportServer().ontransport(defaultServer);
    AsityHandlerFunction asityHandlerFunction = new AsityHandlerFunction().onhttp(hts);

    return RouterFunctions.route(RequestPredicates.path("/cettia")
        // Excludes WebSocket handshake requests
        .and(RequestPredicates.headers(headers -> !"websocket"
            .equalsIgnoreCase(headers.asHttpHeaders().getUpgrade()))),
        asityHandlerFunction);
  }

  @Bean
  public HandlerMapping wsMapping(Server defaultServer) {
    WebSocketTransportServer wts = new WebSocketTransportServer()
        .ontransport(defaultServer);
    AsityWebSocketHandler asityWebSocketHandler = new AsityWebSocketHandler()
        .onwebsocket(wts);
    Map<String, WebSocketHandler> map = new LinkedHashMap<>();
    map.put("/cettia", asityWebSocketHandler);

    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    mapping.setUrlMap(map);

    return mapping;
  }

  @Bean
  public WebSocketHandlerAdapter webSocketHandlerAdapter() {
    return new WebSocketHandlerAdapter();
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
