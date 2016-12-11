package lp.interactions;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static lp.interactions.InteractionBuilder.anInteraction;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InMemoryInteractionStoreTest {

  private static final InteractionRequest NEW_REQUEST = new InteractionRequest("", "", "application/json", null);
  private List<Interaction> storedInteractions;
  private InMemoryInteractionStore store;

  @Before
  public void setUp() throws Exception {
    storedInteractions = new LinkedList<>();
    store = new InMemoryInteractionStore(storedInteractions);
  }

  @Test
  public void save() throws Exception {
    Interaction newInteraction = anInteraction().build();

    store.save(newInteraction);

    assertThat(storedInteractions, contains(newInteraction));
  }

  @Test
  public void findByRequest_newRequest_emptyStore() throws Exception {
    assertThat(store.findBy(NEW_REQUEST), is(Interaction.none()));
  }

  @Test
  public void findByRequest_newRequest_interactionsStored() throws Exception {
    storedInteractions.add(anInteraction().withPostRequest("URL.COM", "").build());
    storedInteractions.add(anInteraction().withPostRequest("FB.IT", "BODY").build());
    assertThat(store.findBy(NEW_REQUEST), is(Interaction.none()));
  }

  @Test
  public void findByRequest_savedInteraction() throws Exception {
    Interaction colorInteraction = anInteraction().withPostRequest("COLOR.IT", "green").build();

    storedInteractions.add(anInteraction().withJsonGetRequest("URL.COM").build());
    storedInteractions.add(anInteraction().withPostRequest("FB.IT", "BODY").build());
    storedInteractions.add(colorInteraction);
    storedInteractions.add(anInteraction().withJsonGetRequest("FITBIT.COM").build());

    assertThat(store.findBy(new InteractionRequest("POST", "COLOR.IT", "application/json", "green".getBytes())), is(colorInteraction));
  }

}
