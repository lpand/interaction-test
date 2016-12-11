package lp.interactions;

import java.util.Arrays;

public class InteractionRequest {
  private final String method;
  private final String url;
  private final String acceptedType;
  private final byte[] payload;

  public InteractionRequest(String method, String url, String acceptedType, byte[] payload) {
    this.method = method;
    this.url = url;
    this.acceptedType = acceptedType;
    this.payload = payload;
  }

  public String method() {
    return method;
  }

  public String url() {
    return url;
  }

  public byte[] payload() {
    return payload;
  }

  public String acceptedType() {
    return acceptedType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    InteractionRequest that = (InteractionRequest) o;

    if (!method.equals(that.method)) return false;
    if (!url.equals(that.url)) return false;
    if (acceptedType != null ? !acceptedType.equals(that.acceptedType) : that.acceptedType != null) return false;
    return Arrays.equals(payload, that.payload);

  }

  @Override
  public int hashCode() {
    int result = method.hashCode();
    result = 31 * result + url.hashCode();
    result = 31 * result + (acceptedType != null ? acceptedType.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(payload);
    return result;
  }

  @Override
  public String toString() {
    return "InteractionRequest{" +
            "method='" + method + '\'' +
            ", url='" + url + '\'' +
            ", acceptedType='" + acceptedType + '\'' +
            ", payload=" + Arrays.toString(payload) +
            '}';
  }
}
