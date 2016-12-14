package lp.interactions;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.net.URI;
import java.net.URISyntaxException;

import static lp.interactions.RequestBuilder.aGetJsonRequest;
import static lp.interactions.RequestBuilder.aGetRequest;
import static lp.interactions.RequestBuilder.aPostRequest;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class DefaultInteractionFactoryTest {

  private static final byte[] ANY_BODY = {};
  private DefaultInteractionFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new DefaultInteractionFactory();
  }

  @Test
  public void newRequest_ofRequestWithoutBody() throws Exception {
    MockClientHttpRequest noBodyReq = new MockClientHttpRequest(HttpMethod.GET, new URI("ANYURI"));

    assertThat(factory.newRequest(noBodyReq, null), is(aGetRequest().build()));
  }

  @Test
  public void newRequest_ofRequestWithBody() throws Exception {
    MockClientHttpRequest requestWithBody = new MockClientHttpRequest(HttpMethod.POST, new URI("ANYURI"));

    assertThat(factory.newRequest(requestWithBody, ANY_BODY), is(aPostRequest().withBody(ANY_BODY).build()));
  }

  @Test
  public void newRequest_ofRequestWithSingleAcceptedType() throws Exception {
    MockClientHttpRequest requestWithBody = new MockClientHttpRequest(HttpMethod.GET, new URI("ANYURI"));
    requestWithBody.getHeaders().set("Accept", "application/json");

    assertThat(factory.newRequest(requestWithBody, null), is(aGetJsonRequest().build()));
  }

  @Test
  public void newRequest_ofRequestWithMultipleAcceptedType() throws Exception {
    MockClientHttpRequest requestWithBody = new MockClientHttpRequest(HttpMethod.GET, new URI("ANYURI"));
    requestWithBody.getHeaders().add("Accept", "application/json");
    requestWithBody.getHeaders().add("Accept", "*/*");

    assertThat(factory.newRequest(requestWithBody, null), is(aGetRequest().withAccept("application/json,*/*").build()));
  }

  @Test
  public void create() throws Exception {

    MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.POST, new URI("ANYURI"));
    request.getHeaders().add("Accept", "text/html");

    MockClientHttpResponse response = new MockClientHttpResponse(new byte[]{9, 8}, OK);
    response.getHeaders().add("Content-Type", "text/html");

    Interaction i = factory.newInteraction(request, new byte[]{1, 2}, response);

    assertThat(i, is(new Interaction(new InteractionRequest("POST", "ANYURI", "text/html", new byte[]{1, 2}),
                                     new InteractionResponse("200", "text/html", new byte[]{9, 8}))));
  }

  @Test
  public void createWithOtherContentType() throws Exception {

    MockClientHttpResponse response = new MockClientHttpResponse(ANY_BODY, OK);
    response.getHeaders().add("Content-Type", "application/json");

    Interaction i = factory.newInteraction(anyRequest(), ANY_BODY, response);

    assertThat(i.response(), is(new InteractionResponse("200", "application/json", ANY_BODY)));
  }

  private MockClientHttpRequest anyRequest() throws URISyntaxException {
    return new MockClientHttpRequest(HttpMethod.POST, new URI("ANYURI"));
  }
}
