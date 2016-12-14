package lp.interactions;

public class RequestBuilder {
  private String url = "ANYURI";
  private String method = "GET";
  private byte[] payload;
  private String acceptedType = "";

  private RequestBuilder() {}

  public static RequestBuilder aJsonRequest(byte[] payload) {
    RequestBuilder builder = new RequestBuilder();
    builder.payload = payload;
    builder.acceptedType = "application/json";
    return builder;
  }

  public InteractionRequest build() {
    return new InteractionRequest(method, url, acceptedType, payload);
  }

  public static RequestBuilder aRequest() {
    return new RequestBuilder();
  }

  public static RequestBuilder aGetRequest() {
    return new RequestBuilder();
  }

  public static RequestBuilder aPostRequest() {
    RequestBuilder builder = new RequestBuilder();
    builder.method = "POST";
    return builder;
  }

  public RequestBuilder withBody(byte[] body) {
    this.payload = body;
    return this;
  }

  public static RequestBuilder aGetJsonRequest() {
    RequestBuilder builder = new RequestBuilder();
    builder.acceptedType = "application/json";
    return builder;
  }

  public RequestBuilder withAccept(String value) {
    this.acceptedType = value;
    return this;
  }
}
