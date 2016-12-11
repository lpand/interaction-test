package lp.interactions;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

public class HttpInteractionStoreAdapter implements HttpInteractionStore {
  private final InteractionFactory interactionFactory;
  private final InteractionStore interactionStore;

  public HttpInteractionStoreAdapter(InteractionFactory interactionFactory,
                                     InteractionStore interactionStore) {
    this.interactionFactory = interactionFactory;
    this.interactionStore = interactionStore;
  }

  @Override
  public ClientHttpResponse findBy(HttpRequest request, byte[] body) {
    Interaction i = findInteraction(request, body);

    return isNewInteraction(i) ? null : makeHttpResponse(i);
  }

  @Override
  public void save(HttpRequest request, byte[] body, ClientHttpResponse response) {
    Interaction interaction = interactionFactory.newInteraction(request, body, response);
    try {
      interactionStore.save(interaction);
    } catch (RuntimeException e) {
      throw new UnableToSaveException(request);
    }
  }

  private boolean isNewInteraction(Interaction i) {
    return Interaction.none().equals(i);
  }

  private MockClientHttpResponse makeHttpResponse(Interaction i) {
    return new MockClientHttpResponse(i.response().payload(), statusAsInt(i.response().status()));
  }

  private Interaction findInteraction(HttpRequest request, byte[] body) {
    return interactionStore.findBy(interactionFactory.newRequest(request, body));
  }

  private HttpStatus statusAsInt(String status) {
    return HttpStatus.valueOf(Integer.parseInt(status));
  }

  public static class UnableToSaveException extends RuntimeException {
    public UnableToSaveException(HttpRequest request) {
      super(request.toString());
    }
  }
}
