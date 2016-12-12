package lp.interactions;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InteractionsStoreNamerTest {

  @Test
  public void defaultJsonName() throws Exception {
    assertThat(InteractionsStoreNamer.jsonStoreName("any string"), is("any string.json"));
  }

}
