package ch.rasc.httpclient;

import java.io.ByteArrayInputStream;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {
  private final Jsonb jsonb;
  private final Class<T> type;

  public static <T> JsonBodyHandler<T> jsonBodyHandler(final Class<T> type) {
    return jsonBodyHandler(JsonbBuilder.create(), type);
  }

  public static <T> JsonBodyHandler<T> jsonBodyHandler(final Jsonb jsonb,
      final Class<T> type) {
    return new JsonBodyHandler<>(jsonb, type);
  }

  private JsonBodyHandler(Jsonb jsonb, Class<T> type) {
    this.jsonb = jsonb;
    this.type = type;
  }

  @Override
  public HttpResponse.BodySubscriber<T> apply(
      final HttpResponse.ResponseInfo responseInfo) {
    return BodySubscribers.mapping(BodySubscribers.ofByteArray(),
        byteArray -> this.jsonb.fromJson(new ByteArrayInputStream(byteArray), this.type));
  }
}