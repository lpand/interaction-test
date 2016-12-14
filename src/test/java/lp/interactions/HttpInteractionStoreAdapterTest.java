package lp.interactions;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static lp.interactions.RequestBuilder.aJsonRequest;
import static lp.interactions.RequestBuilder.aRequest;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class HttpInteractionStoreAdapterTest {

  private static final byte[] BODY = {};
  private static final MockClientHttpResponse RESPONSE = new MockClientHttpResponse(BODY, HttpStatus.OK);
  private static final MockClientHttpRequest REQUEST = new MockClientHttpRequest();
  private static final Interaction ANY_INTERACTION = new Interaction(null, null);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private InteractionStore interactionStore;

  @Mock
  private InteractionFactory interactionFactory;

  private HttpInteractionStoreAdapter repository;
  private static final InteractionRequest INTERACTION_REQUEST = aRequest().build();

  @Before
  public void setUp() throws Exception {
    repository = new HttpInteractionStoreAdapter(interactionFactory, interactionStore);
  }

  @Test
  public void save() throws Exception {

    context.checking(new Expectations() {{
      allowing(interactionFactory).newInteraction(REQUEST, BODY, RESPONSE); will(returnValue(ANY_INTERACTION));
      oneOf(interactionStore).save(ANY_INTERACTION);
    }});

    repository.save(REQUEST, BODY, RESPONSE);
  }

  @Test
  public void saveFails() throws Exception {

    thrown.expect(HttpInteractionStoreAdapter.UnableToSaveException.class);

    context.checking(new Expectations() {{
      allowing(interactionFactory).newInteraction(REQUEST, BODY, RESPONSE); will(returnValue(ANY_INTERACTION));
      oneOf(interactionStore).save(ANY_INTERACTION); will(throwException(new RuntimeException()));
    }});

    repository.save(REQUEST, BODY, RESPONSE);
  }

  @Test
  public void findNewRequest() throws Exception {
    byte[] payload = {1, 2, 3};

    context.checking(new Expectations() {{
      allowing(interactionFactory).newRequest(REQUEST, payload); will(returnValue(INTERACTION_REQUEST));
      allowing(interactionStore).findBy(INTERACTION_REQUEST); will(returnValue(Interaction.none()));
    }});

    assertThat(repository.findBy(REQUEST, payload), is(nullValue()));
  }

  @Test
  public void findSavedRequest() throws Exception {
    byte[] payload = {9, 8};
    byte[] responsePayload = {1, 2, 3};

    context.checking(new Expectations() {{
      allowing(interactionFactory).newRequest(REQUEST, payload); will(returnValue(INTERACTION_REQUEST));
      allowing(interactionStore).findBy(INTERACTION_REQUEST); will(returnValue(InteractionBuilder.anInteraction(responsePayload).build()));
    }});

    ClientHttpResponse response = repository.findBy(REQUEST, payload);

    assertThat(response.getRawStatusCode(), is(200));
    assertThat(readBody(response), is(responsePayload));
  }

  private byte[] readBody(ClientHttpResponse response) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    StreamUtils.copy(response.getBody(), buffer);
    return buffer.toByteArray();
  }

}
