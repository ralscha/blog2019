package ch.rasc.cettiaone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.cettia.Server;
import io.cettia.ServerSocketPredicates;
import tools.jackson.databind.ObjectMapper;

@Service
public class DataEmitterService {

  private final Server defaultServer;

  private final ObjectMapper objectMapper;

  private final static Random random = new Random();

  public DataEmitterService(Server defaultServer, ObjectMapper objectMapper) {
    this.defaultServer = defaultServer;
    this.objectMapper = objectMapper;

    this.defaultServer.onsocket(socket -> {
      socket.on("charts", msg -> {
        socket.untag("pie", "gauge", "line", "bar");
        socket.tag(((List<String>) msg).toArray(new String[0]));
      });
    });
  }

  @Scheduled(initialDelay = 2_000, fixedRate = 5_000)
  public void sendPieData() {
    List<Integer> data = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      data.add(random.nextInt(300));
    }

    this.defaultServer.find(ServerSocketPredicates.tag("pie")).send("pie",
        this.objectMapper.writeValueAsString(data));
  }

  @Scheduled(initialDelay = 1_800, fixedRate = 4_700)
  public void sendGaugeData() {
    this.defaultServer.find(ServerSocketPredicates.tag("gauge")).send("gauge",
        random.nextInt(100));
  }

  @Scheduled(initialDelay = 2_000, fixedRate = 5_200)
  public void sendBarData() {
    List<Integer> data = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      data.add(random.nextInt(300));
    }

    this.defaultServer.find(ServerSocketPredicates.tag("bar")).send("bar",
        this.objectMapper.writeValueAsString(data));
  }

  @Scheduled(initialDelay = 2_000, fixedRate = 1_000)
  public void sendLineData() {
    var data = new HashMap<String, Object>();
    data.put("name", System.currentTimeMillis());
    data.put("value", new Object[] { System.currentTimeMillis(), random.nextInt(3000) });

    this.defaultServer.find(ServerSocketPredicates.tag("line")).send("line",
        this.objectMapper.writeValueAsString(data));
  }
}
