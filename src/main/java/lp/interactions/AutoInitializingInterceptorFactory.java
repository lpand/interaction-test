package lp.interactions;

import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static lp.interactions.InteractionsStoreNamer.jsonStoreName;

public class AutoInitializingInterceptorFactory {
  private static final JsonInteractionMapper MAPPER = new JsonInteractionMapper();
  private static final File DEFAULT_INTERACTIONS_DIR = new File("interactions");

  public static ClientHttpRequestInterceptor interceptorFor(String interactionName) {
    try {
      File isf = createInteractionsStoreFile(interactionName);
      List<Interaction> savedInteractions = readInteractions(isf);
      return makeInterceptor(fileSystemStore(isf, savedInteractions));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static File createInteractionsStoreFile(String interactionName) throws IOException {
    File interactionsFile = getInteractionFile(interactionName);
    createDirIfAbsent(DEFAULT_INTERACTIONS_DIR);
    createFileIfAbsent(interactionsFile);
    return interactionsFile;
  }

  private static File getInteractionFile(String interactionName) {
    return new File(DEFAULT_INTERACTIONS_DIR, jsonStoreName(interactionName));
  }

  private static AutoInitializingRestClientInterceptor makeInterceptor(InteractionFileSystemStore interactionFileSystemStore) {
    return new AutoInitializingRestClientInterceptor(new HttpInteractionStoreAdapter(new DefaultInteractionFactory(), interactionFileSystemStore));
  }

  private static List<Interaction> readInteractions(File interactionsFile) throws IOException {
    List<Interaction> interactions = MAPPER.deserialize(new String(readAllBytes(interactionsFile.toPath())));
    return new LinkedList<>(interactions);
  }

  private static void createFileIfAbsent(File file) throws IOException {
    if (!Files.exists(file.toPath()))
      Files.createFile(file.toPath());
  }

  private static void createDirIfAbsent(File dir) throws IOException {
    if (!Files.exists(dir.toPath()))
      Files.createDirectory(dir.toPath());
  }

  private static InteractionFileSystemStore fileSystemStore(File interactionsFile, List<Interaction> interactions) {
    return new InteractionFileSystemStore(interactionsFile, MAPPER, new InMemoryInteractionStore(interactions), interactions);
  }
}
