package lp.interactions;

public interface InteractionStore {
  Interaction findBy(InteractionRequest request);

  void save(Interaction interaction);
}
