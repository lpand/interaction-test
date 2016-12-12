package lp.interactions;

import java.util.List;

public interface InteractionDeserializer {
  List<Interaction> deserialize(String json) throws InteractionDeserializationException;
}
