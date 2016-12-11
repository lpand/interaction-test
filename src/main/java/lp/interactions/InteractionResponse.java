package lp.interactions;

import java.util.Arrays;

public class InteractionResponse {
  private final String status;
  private String contentType;
  private final byte[] payload;

  public InteractionResponse(String status, String contentType, byte[] payload) {
    this.status = status;
    this.contentType = contentType;
    this.payload = payload;
  }

  public byte[] payload() {
    return payload;
  }

  public String status() {
    return status;
  }

  public String contentType() {
    return contentType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    InteractionResponse that = (InteractionResponse) o;

    if (!status.equals(that.status)) return false;
    if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
    return Arrays.equals(payload, that.payload);

  }

  @Override
  public int hashCode() {
    int result = status.hashCode();
    result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(payload);
    return result;
  }

  @Override
  public String toString() {
    return "InteractionResponse{" +
            "status='" + status + '\'' +
            ", contentType='" + contentType + '\'' +
            ", payload=" + Arrays.toString(payload) +
            '}';
  }
}
