package ch.rasc.cettiatwo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import ch.rasc.cettiatwo.ChangeEventOuterClass.ChangeEvent;
import ch.rasc.cettiatwo.ChangeEventOuterClass.Todo;
import ch.rasc.cettiatwo.ChangeEventOuterClass.Todos;
import io.cettia.Server;
import io.cettia.asity.action.Action;

@Service
public class TodoService {

  private final Server defaultServer;

  private final Map<String, Todo> todos = new HashMap<>();

  private final List<Object[]> cache = new CopyOnWriteArrayList<>();

  public TodoService(Server defaultServer) {
    this.defaultServer = defaultServer;

    this.defaultServer.onsocket(socket -> {
      socket.onopen(v -> {
        socket.send("initial",
            Todos.newBuilder().addAllTodos(this.todos.values()).build().toByteArray());
      });

      socket.<byte[]>on("update", msg -> {
        try {
          ChangeEvent ce = ChangeEvent.parseFrom(msg);
          Todo todo = ce.getTodo();

          switch (ce.getChange()) {
          case DELETE:
            this.todos.remove(todo.getId());
            break;
          case INSERT:
          case UPDATE:
            this.todos.put(todo.getId(), todo);
            break;
          default:
            break;
          }
        }
        catch (InvalidProtocolBufferException e) {
          LoggerFactory.getLogger(TodoService.class).error("send update", e);
        }
      });

      // Disconnect Handling
      // https://cettia.io/guides/cettia-tutorial/#disconnection-handling

      socket.oncache((Object[] args) -> this.cache.add(args));

      socket.onopen(v -> this.cache.forEach(args -> {
        this.cache.remove(args);
        socket.send((String) args[0], args[1], (Action<?>) args[2], (Action<?>) args[3]);
      }));

      socket.ondelete(v -> this.cache.forEach(args -> System.out
          .println(socket + " missed event - name: " + args[0] + ", data: " + args[1])));

    });
  }

}
