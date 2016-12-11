package lp.interactions;

public class Interaction {

  private final InteractionRequest request;
  private final InteractionResponse response;

  public Interaction(InteractionRequest interactionRequest, InteractionResponse interactionResponse) {
    this.request = interactionRequest;
    this.response = interactionResponse;
  }

  public static Interaction none() {
    return new Interaction(new InteractionRequest("", "", "", null), new InteractionResponse("", "", null));
  }

  public InteractionResponse response() {
    return response;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Interaction that = (Interaction) o;

    if (!request.equals(that.request)) return false;
    return response.equals(that.response);

  }

  @Override
  public int hashCode() {
    int result = request.hashCode();
    result = 31 * result + response.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Interaction{" +
            "request=" + request +
            ", response=" + response +
            '}';
  }

  public InteractionRequest request() {
    return request;
  }
}
