package lp.interactions;

import java.util.List;

public class InMemoryInteractionStore implements InteractionStore {

  private final List<Interaction> store;

  public InMemoryInteractionStore(List<Interaction> store) {
    this.store = store;
  }

  @Override
  public Interaction findBy(InteractionRequest request) {
    return store.stream()
            .filter(i -> i.request().equals(request))
            .findFirst()
            .orElseGet(Interaction::none);
  }

  @Override
  public void save(Interaction interaction) {
    store.add(interaction);
  }
}
