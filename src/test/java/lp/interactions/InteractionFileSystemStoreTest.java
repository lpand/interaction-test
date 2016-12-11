package lp.interactions;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Auto;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static lp.interactions.InteractionBuilder.anInteraction;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InteractionFileSystemStoreTest {

  private static final List<Interaction> STORED_INTERACTIONS = unmodifiableList(emptyList());

  private File repositoryFile = new File("./colors_it.json");

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private InteractionSerializer interactionSerializer;

  @Mock
  private InteractionStore delegate;

  @Auto
  private Sequence saving;

  private InteractionFileSystemStore repository;

  @Before
  public void setUp() throws Exception {
    repository = new InteractionFileSystemStore(repositoryFile, interactionSerializer, delegate, STORED_INTERACTIONS);
  }

  @After
  public void tearDown() throws Exception {
    if (Files.exists(repositoryFile.toPath()))
      Files.delete(repositoryFile.toPath());
  }

  @Test
  public void save_delegatesToDelegate() throws Exception {
    Interaction anInteraction = anInteraction().build();

    context.checking(new Expectations() {{
      oneOf(delegate).save(anInteraction);
      ignoring(interactionSerializer);
    }});

    repository.save(anInteraction);
  }

  @Test
  public void save_storeSerializedInteractionsOnFile() throws Exception {
    String serializedInteractions = "serialized interactions";

    context.checking(new Expectations() {{
      oneOf(delegate).save(with(any(Interaction.class))); inSequence(saving);
      allowing(interactionSerializer).serialize(STORED_INTERACTIONS); will(returnValue(serializedInteractions));
    }});

    repository.save(anInteraction().build());

    assertThat(new String(readAllBytes(repositoryFile.toPath())), is(serializedInteractions));
  }

  @Test
  public void findBy_delegatesToDelegate() throws Exception {
    Interaction interaction = anInteraction().build();
    InteractionRequest request = interaction.request();

    context.checking(new Expectations() {{
      allowing(delegate).findBy(request); will(returnValue(interaction));
    }});

    assertThat(repository.findBy(request), is(interaction));
  }
}
