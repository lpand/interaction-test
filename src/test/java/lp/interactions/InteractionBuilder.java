package lp.interactions;

public class InteractionBuilder {
  private String requestUrl = "http://localhost/";
  private String requestMethod = "GET";
  private byte[] requestPayload;
  private byte[] responsePayload;
  private String acceptedType = "application/json";

  public static InteractionBuilder anInteraction() {
    return new InteractionBuilder();
  }

  public InteractionBuilder withJsonGetRequest(String url) {
    this.requestUrl = url;
    return this;
  }

  public Interaction withJsonResponse(String status, String payload) {
    return new Interaction(new InteractionRequest(requestMethod, requestUrl, acceptedType, requestPayload),
                           new InteractionResponse(status, payload != null ? "application/json" : null, readBody(payload)));
  }

  private byte[] readBody(String payload) {
    return payload == null ? null : payload.getBytes();
  }

  public Interaction build() {
    return new Interaction(new InteractionRequest(requestMethod, requestUrl, acceptedType, requestPayload),
            new InteractionResponse("200", "application/json", responsePayload));
  }

  public InteractionBuilder withPostRequest(String url, String body) {
    this.requestUrl = url;
    this.requestMethod = "POST";
    this.requestPayload = readBody(body);
    return this;
  }

  public static InteractionBuilder anInteraction(byte[] responsePayload) {
    InteractionBuilder builder = new InteractionBuilder();
    builder.responsePayload = responsePayload;
    return builder;
  }

  public InteractionBuilder withPostRequest(String localhost, String acceptedType, String body) {
    this.requestMethod = "POST";
    this.requestUrl = localhost;
    this.acceptedType = acceptedType;
    this.requestPayload = readBody(body);
    return this;
  }

  public Interaction withResponse(String status, String contentType, String body) {
    return new Interaction(new InteractionRequest(requestMethod, requestUrl, acceptedType, requestPayload),
                           new InteractionResponse(status, contentType, readBody(body)));
  }
}
