package lp.interactions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class JsonInteractionMapper implements InteractionSerializer, InteractionDeserializer {

  private final ObjectMapper mapper;

  public JsonInteractionMapper() {
    mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  @Override
  public String serialize(List<Interaction> interactions) {
    try {
      return mapper.writeValueAsString(InteractionsWrapper.from(interactions));
    } catch (JsonProcessingException e) {
      // TODO: better handling along with tests
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Interaction> deserialize(String content) {
    if (content.isEmpty())
      return Collections.emptyList();

    try {
      InteractionsWrapper wrapper = mapper.readValue(content, InteractionsWrapper.class);
      return InteractionsWrapper.toInteractions(wrapper);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static class InteractionsWrapper {
    public List<InteractionsWrapper.InteractionDto> interactions;

    public static InteractionsWrapper from(List<Interaction> interactions) {
      InteractionsWrapper wrapper = new InteractionsWrapper();
      wrapper.interactions = interactions.stream().map(InteractionsWrapper::adapt).collect(toList());
      return wrapper;
    }

    private static InteractionsWrapper.InteractionDto adapt(Interaction interaction) {
      InteractionsWrapper.InteractionDto interactionDto = new InteractionsWrapper.InteractionDto();
      interactionDto.request = adapt(interaction.request());
      interactionDto.response = adapt(interaction.response());
      return interactionDto;
    }

    private static InteractionsWrapper.InteractionResponseDto adapt(InteractionResponse response) {
      InteractionsWrapper.InteractionResponseDto interactionResponseDto = new InteractionsWrapper.InteractionResponseDto();
      interactionResponseDto.status = response.status();
      interactionResponseDto.body = bodyOrNull(response.payload());
      interactionResponseDto.type = response.contentType();
      return interactionResponseDto;
    }

    private static InteractionsWrapper.InteractionRequestDto adapt(InteractionRequest request) {
      InteractionsWrapper.InteractionRequestDto interactionRequestDto = new InteractionsWrapper.InteractionRequestDto();
      interactionRequestDto.method = request.method();
      interactionRequestDto.url = request.url();
      interactionRequestDto.body = bodyOrNull(request.payload());
      interactionRequestDto.type = request.acceptedType();
      return interactionRequestDto;
    }

    public static List<Interaction> toInteractions(InteractionsWrapper wrapper) {
      return wrapper.interactions.stream()
              .map(InteractionsWrapper::adapt)
              .collect(toList());
    }

    private static String bodyOrNull(byte[] payload) {
      return payload == null ? null : new String(payload);
    }

    private static byte[] bodyOrNull(String body) {
      return body == null ? null : body.getBytes();
    }

    private static Interaction adapt(InteractionsWrapper.InteractionDto dto) {
      return new Interaction(adapt(dto.request), adapt(dto.response));
    }

    private static InteractionRequest adapt(InteractionsWrapper.InteractionRequestDto dto) {
      return new InteractionRequest(dto.method, dto.url, dto.type, bodyOrNull(dto.body));
    }

    private static InteractionResponse adapt(InteractionsWrapper.InteractionResponseDto dto) {
      return new InteractionResponse(dto.status, dto.type, bodyOrNull(dto.body));
    }

    private static class InteractionDto {
      public InteractionsWrapper.InteractionRequestDto request;
      public InteractionsWrapper.InteractionResponseDto response;
    }

    private static class InteractionRequestDto {
      public String method;
      public String url;
      public String body;
      public String type;
    }

    private static class InteractionResponseDto {
      public String status;
      public String body;
      public String type;
    }

  }
}
