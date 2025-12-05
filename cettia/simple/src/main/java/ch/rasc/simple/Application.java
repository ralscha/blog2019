package ch.rasc.simple;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import io.cettia.DefaultServer;
import io.cettia.Server;
import io.cettia.ServerSocket.State;
import io.cettia.asity.action.Action;
import io.cettia.asity.bridge.spring.webflux5.AsityHandlerFunction;
import io.cettia.asity.bridge.spring.webflux5.AsityWebSocketHandler;
import io.cettia.transport.http.HttpTransportServer;
import io.cettia.transport.websocket.WebSocketTransportServer;

@SpringBootApplication
@EnableWebFlux
public class Application {

  @Bean
  Server defaultServer() {
    Server server = new DefaultServer();

    server.onsocket(socket -> {

      if (socket.state() == State.CLOSED) {
      }

      Action<Void> openHandler = v -> {
        System.out.println("open: " + socket.id());
      };
      socket.onopen(openHandler);
      // socket.off("open", openHandler);
      /*
       * socket.on("open", v -> { System.out.println("open: " + socket.id()); });
       */

      socket.onclose(v -> {
        System.out.println("close: " + socket.id());
      });

      socket.onerror(t -> {
        System.out.println("error: " + socket.id() + " : " + t.getMessage());
      });

      socket.ondelete(v -> {
        System.out.println("delete: " + socket.id());
      });

      socket.oncache(obj -> {
        System.out.println("cache: " + socket.id() + " : " + Arrays.toString(obj));
      });

      socket.on("ping", msg -> {
        System.out.println(msg);
        socket.send("pong", "from server");
      });

    });

    return server;
  }

  @Bean
  RouterFunction<ServerResponse> httpMapping(Server defaultServer,
      @Value("classpath:/static/index.html") final Resource indexHtml) {
    HttpTransportServer httpTransportServer = new HttpTransportServer()
        .ontransport(defaultServer);
    AsityHandlerFunction asityHandlerFunction = new AsityHandlerFunction()
        .onhttp(httpTransportServer);

    RequestPredicate isNotWebSocket = RequestPredicates.headers(
        headers -> !"websocket".equalsIgnoreCase(headers.asHttpHeaders().getUpgrade()));

    return RouterFunctions
        .route(RequestPredicates.path("/cettia").and(isNotWebSocket),
            asityHandlerFunction)
        .and(RouterFunctions.route(RequestPredicates.GET("/"),
            request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .bodyValue(indexHtml)))
        .and(RouterFunctions.resources("/**", new ClassPathResource("static/")));
  }

  @Bean
  HandlerMapping wsMapping(Server defaultServer) {
    WebSocketTransportServer wsTransportServer = new WebSocketTransportServer()
        .ontransport(defaultServer);
    AsityWebSocketHandler asityWebSocketHandler = new AsityWebSocketHandler()
        .onwebsocket(wsTransportServer);
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
