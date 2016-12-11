package lp.interactions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

class DefaultInteractionFactory implements InteractionFactory {
  @Override
  public InteractionRequest newRequest(HttpRequest request, byte[] body) {
    return new InteractionRequest(request.getMethod().name(), request.getURI().toString(), header(request.getHeaders(), "Accept"), body);
  }

  private String header(HttpHeaders headers, String key) {
    List<String> accepts = headers.get(key);
    return ofNullable(accepts).orElse(emptyList())
              .stream()
            .collect(joining(","));
  }

  @Override
  public Interaction newInteraction(HttpRequest request, byte[] body, ClientHttpResponse response) {
    try {
      return new Interaction(newRequest(request, body), newResponseInteraction(response));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private InteractionResponse newResponseInteraction(ClientHttpResponse response) throws IOException {
    String contentType = header(response.getHeaders(), "Content-Type");
    return new InteractionResponse(response.getStatusCode().toString(), contentType, readBody(response.getBody()));
  }

  private byte[] readBody(InputStream body) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int readBytesCount;
    byte[] data = new byte[1024];
    while ((readBytesCount = body.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, readBytesCount);
    }

    buffer.flush();
    return buffer.toByteArray();
  }
}
