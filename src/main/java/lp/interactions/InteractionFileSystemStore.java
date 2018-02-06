package lp.interactions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class InteractionFileSystemStore implements InteractionStore {
  private final Path repository;
  private final InteractionSerializer interactionSerializer;
  private final InteractionStore delegate;
  private final List<Interaction> storedInteractions;

  public InteractionFileSystemStore(File fileRepository, InteractionSerializer interactionSerializer, InteractionStore delegate, List<Interaction> storedInteractions) {
    repository = fileRepository.toPath();
    this.interactionSerializer = interactionSerializer;
    this.delegate = delegate;
    this.storedInteractions = storedInteractions;
  }

  @Override
  public Interaction findBy(InteractionRequest request) {
    return delegate.findBy(request);
  }

  @Override
  public void save(Interaction interaction) {
    delegate.save(interaction);
    // FIXME: save new interaction too
    storeOnFile(interactionSerializer.serialize(storedInteractions));
  }

  private void storeOnFile(String content) {
    try {
      Files.write(repository, content.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
