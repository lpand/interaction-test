package lp.interactions;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public interface InteractionFactory {
  Interaction newInteraction(HttpRequest request, byte[] body, ClientHttpResponse response);

  InteractionRequest newRequest(HttpRequest request, byte[] body);
}
