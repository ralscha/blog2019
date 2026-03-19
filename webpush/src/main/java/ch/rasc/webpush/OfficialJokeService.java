package ch.rasc.webpush;

import org.springframework.web.service.annotation.GetExchange;

public interface OfficialJokeService {

  record Joke(String type, String setup, String punchline, int id) {
  }

  @GetExchange("/random_joke")
  Joke getRandomJoke();

}