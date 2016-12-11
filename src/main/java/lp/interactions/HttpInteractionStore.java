package lp.interactions;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public interface HttpInteractionStore {
  ClientHttpResponse findBy(HttpRequest request, byte[] body);

  void save(HttpRequest request, byte[] body, ClientHttpResponse response);
}
