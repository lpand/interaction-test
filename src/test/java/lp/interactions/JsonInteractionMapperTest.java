package com.lastminute.concorde.client;

import lp.interactions.FileReader;
import lp.interactions.Interaction;
import lp.interactions.JsonInteractionMapper;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static lp.interactions.InteractionBuilder.anInteraction;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JsonInteractionMapperTest {

  private static final List<Interaction> INTERACTIONS = asList(
          anInteraction()
            .withJsonGetRequest("http://localhost:8080/colors")
            .withJsonResponse("200", "[\"orange\", \"green\", \"brown\"]"),
          anInteraction()
            .withPostRequest("http://localhost:8080/colors", "red")
            .withJsonResponse("201", null));

  private JsonInteractionMapper jsonInteractionMapper;
  public static final Interaction TEXT_INTERACTION = anInteraction()
      .withPostRequest("localhost", "text/plain", "request text")
      .withResponse("200", "text/plain", "response text");

  @Before
  public void setUp() throws Exception {
    jsonInteractionMapper = new JsonInteractionMapper();
  }

  @Test
  public void serialize() throws Exception {
    String expected = FileReader.asString("classpath:interactions/colors_interactions.json");
    String json = jsonInteractionMapper.serialize(INTERACTIONS);
    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void serializeWithAcceptedType() throws Exception {
    String expected = FileReader.asString("classpath:interactions/text_interactions.json");
    String json = jsonInteractionMapper.serialize(asList(TEXT_INTERACTION));
    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void deserialize() throws Exception {
    String json = FileReader.asString("classpath:interactions/colors_interactions.json");
    assertThat(jsonInteractionMapper.deserialize(json), is(INTERACTIONS));
  }

  @Test
  public void deserializeWithContentType() throws Exception {
    String json = FileReader.asString("classpath:interactions/text_interactions.json");
    assertThat(jsonInteractionMapper.deserialize(json), contains(TEXT_INTERACTION));
  }

  @Test
  public void deserializeEmptyString() throws Exception {
    assertThat(jsonInteractionMapper.deserialize(""), is(emptyList()));
  }
}
