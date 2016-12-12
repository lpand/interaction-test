package lp.interactions;

public class InteractionsStoreNamer {
  public static String jsonStoreName(String baseName) {
    return String.format("%s.json", baseName);
  }
}
