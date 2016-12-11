package lp.interactions;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class AutoInitializingRestClientInterceptor implements ClientHttpRequestInterceptor {

  private final HttpInteractionStore httpInteractionStore;

  public AutoInitializingRestClientInterceptor(HttpInteractionStore httpInteractionStore) {
    this.httpInteractionStore = httpInteractionStore;
  }

  @Override
  public ClientHttpResponse
  intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
    ClientHttpResponse response = httpInteractionStore.findBy(httpRequest, body);

    if (response == null)
      response = fetchAndStoreResponse(httpRequest, body, clientHttpRequestExecution);

    return response;
  }

  private ClientHttpResponse
  fetchAndStoreResponse(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
    ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, body);
    httpInteractionStore.save(httpRequest, body, response);
    return response;
  }

}
