package ch.rasc.webpush;

import org.springframework.web.service.annotation.GetExchange;

public interface ChuckNorrisJokeService {

  record Joke(String iconUrl, String id, String url, String value) {
  }

  @GetExchange("/jokes/random")
  Joke getRandomJoke();

}
