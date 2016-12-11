package lp.interactions;

import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class AutoInitializingInterceptorFactory {
  private static final JsonInteractionMapper mapper = new JsonInteractionMapper();

  public static ClientHttpRequestInterceptor intercept(String interactionName) {
    try {
      File interactionsDir = new File("interactions");
      File interactionsFile = new File(interactionsDir, interactionName);
      createDirIfAbsent(interactionsDir);
      createFileIfAbsent(interactionsFile);
      List<Interaction> interactions = new LinkedList<>(readInteractions(interactionsFile));
      return makeInterceptor(fileSystemStore(interactionsFile, interactions));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static AutoInitializingRestClientInterceptor makeInterceptor(InteractionFileSystemStore interactionFileSystemStore) {
    return new AutoInitializingRestClientInterceptor(new HttpInteractionStoreAdapter(new DefaultInteractionFactory(), interactionFileSystemStore));
  }

  private static List<Interaction> readInteractions(File interactionsFile) throws IOException {
    return mapper.deserialize(new String(Files.readAllBytes(interactionsFile.toPath())));
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
    return new InteractionFileSystemStore(interactionsFile, mapper, new InMemoryInteractionStore(interactions), interactions);
  }
}
