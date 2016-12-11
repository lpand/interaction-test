package lp.interactions;

import java.util.List;

public interface InteractionSerializer {
  String serialize(List<Interaction> storedInteractions);
}
