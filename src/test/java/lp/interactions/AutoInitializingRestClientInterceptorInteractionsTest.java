package lp.interactions;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static lp.interactions.AutoInitializingInterceptorFactory.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = AutoInitializingRestClientInterceptorInteractionsTest.TestConfig.class)
public class AutoInitializingRestClientInterceptorInteractionsTest {

  private static final Path NAMES_INTERACTIONS_FILE_PATH = new File("interactions/names_interactions.json").toPath();

  @SpringBootApplication
  public static class TestConfig {
    @Bean
    public NamesController namesController() {
      return new NamesController();
    }
  }

  @RestController
  public static class NamesController {
    @RequestMapping("/names")
    List<String> names() {
      return asList("Uma", "Simona", "Karen");
    }
  }

  @LocalServerPort
  private String port;

  private static RestTemplate interceptedClient(String interactionFileName) {
    RestTemplate client = new RestTemplate();
    client.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    ClientHttpRequestInterceptor interceptor = intercept(interactionFileName);
    client.getInterceptors().add(0, interceptor);
    return client;
  }

  @Test
  public void interceptNewNamesRequest() throws Exception {
    RestTemplate client = interceptedClient("names_interactions.json");
    String names = client.getForObject(format("http://localhost:%s/names", port), String.class);
    assertThat(names, is("[\"Uma\",\"Simona\",\"Karen\"]"));
    assertThat(Files.exists(NAMES_INTERACTIONS_FILE_PATH), is(true));
  }

  @Test
  public void interceptSavedRequestAndReturnResponseWithoutContactingServer() throws Exception {
    RestTemplate client = interceptedClient("mocked_names_interactions.json");
    String names = client.getForObject(format("http://localhost:%s/mocked_names", port), String.class);
    assertThat(names, is("[\"Casper\",\"Luca\",\"Gianni\"]"));
  }

  @AfterClass
  public static void tearDown() throws Exception {
    if (Files.exists(NAMES_INTERACTIONS_FILE_PATH))
      Files.delete(NAMES_INTERACTIONS_FILE_PATH);
  }
}
