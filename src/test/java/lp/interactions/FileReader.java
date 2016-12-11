package lp.interactions;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static java.nio.file.Files.readAllBytes;

public class FileReader {
  public static String asString(String location) throws IOException {
    Resource resource = new FileSystemResourceLoader().getResource(location);
    return new String(readAllBytes(resource.getFile().toPath()));
  }
}
