package lp.interactions;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AutoInitializingRestClientInterceptorTest {

  private static final byte[] BODY = {};
  private static final MockClientHttpResponse RESPONSE = new MockClientHttpResponse(BODY, HttpStatus.OK);
  private static final MockClientHttpRequest REQUEST = new MockClientHttpRequest();

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ClientHttpRequestExecution requestExecution;

  @Mock
  private HttpInteractionStore httpInteractionStore;

  private AutoInitializingRestClientInterceptor interceptor;

  @Before
  public void setUp() throws Exception {
    interceptor = new AutoInitializingRestClientInterceptor(httpInteractionStore);
  }

  @Test
  public void onRequest_whenInteractionIsNotFound() throws Exception {

    context.checking(new Expectations(){{
      allowing(httpInteractionStore).findBy(REQUEST, BODY); will(returnValue(null));
      allowing(requestExecution).execute(REQUEST, BODY); will(returnValue(RESPONSE));
      oneOf(httpInteractionStore).save(REQUEST, BODY, RESPONSE);
    }});

    assertThat(interceptor.intercept(REQUEST, BODY, requestExecution), is(RESPONSE));
  }

  @Test
  public void onRequest_whenInteractionIsFound() throws Exception {
    context.checking(new Expectations(){{
      allowing(httpInteractionStore).findBy(REQUEST, BODY); will(returnValue(RESPONSE));
    }});

    assertThat(interceptor.intercept(REQUEST, BODY, requestExecution), is(RESPONSE));
  }

}
