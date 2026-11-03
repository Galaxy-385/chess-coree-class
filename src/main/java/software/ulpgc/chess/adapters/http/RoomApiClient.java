package software.ulpgc.chess.adapters.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class RoomApiClient {

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public RoomApiClient(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "API URL cannot be empty"
            );
        }

        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    // --------------------------------------------------
    // CREATE ROOM
    // --------------------------------------------------

    public RoomResponse createRoom(
            String roomId,
            String playerId
    ) throws IOException, InterruptedException {

        CreateRoomRequest request = new CreateRoomRequest(
                roomId,
                playerId
        );

        return send(
                "POST",
                "/rooms",
                request,
                RoomResponse.class
        );
    }

    // --------------------------------------------------
    // JOIN ROOM
    // --------------------------------------------------

    public RoomResponse joinRoom(
            String roomId,
            String playerId
    ) throws IOException, InterruptedException {

        JoinRoomRequest request = new JoinRoomRequest(playerId);

        return send(
                "POST",
                "/rooms/" + encode(roomId) + "/join",
                request,
                RoomResponse.class
        );
    }

    // --------------------------------------------------
    // GET ROOM
    // --------------------------------------------------

    public RoomResponse getRoom(
            String roomId
    ) throws IOException, InterruptedException {

        return send(
                "GET",
                "/rooms/" + encode(roomId),
                null,
                RoomResponse.class
        );
    }

    // --------------------------------------------------
    // PLAY MOVE
    // --------------------------------------------------

    public MoveResponse playMove(
            String roomId,
            String playerId,
            String from,
            String to,
            String promotionPiece
    ) throws IOException, InterruptedException {

        PlayMoveRequest request = new PlayMoveRequest(
                playerId,
                from,
                to,
                promotionPiece
        );

        return send(
                "POST",
                "/rooms/" + encode(roomId) + "/moves",
                request,
                MoveResponse.class
        );
    }

    // --------------------------------------------------
    // GET MOVE HISTORY
    // --------------------------------------------------

    public MoveHistoryResponse getMoves(
            String roomId
    ) throws IOException, InterruptedException {

        return send(
                "GET",
                "/rooms/" + encode(roomId) + "/moves",
                null,
                MoveHistoryResponse.class
        );
    }

    // --------------------------------------------------
    // HTTP COMMUNICATION
    // --------------------------------------------------

    private <T> T send(
            String method,
            String path,
            Object requestBody,
            Class<T> responseType
    ) throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header(
                        "Accept",
                        "application/json"
                );

        if (requestBody != null) {
            String json = mapper.writeValueAsString(requestBody);

            builder.header(
                    "Content-Type",
                    "application/json"
            );

            builder.method(
                    method,
                    HttpRequest.BodyPublishers.ofString(json)
            );
        } else {
            builder.method(
                    method,
                    HttpRequest.BodyPublishers.noBody()
            );
        }

        HttpResponse<String> response = client.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        int status = response.statusCode();

        if (status < 200 || status >= 300) {
            throw createApiException(response);
        }

        return mapper.readValue(
                response.body(),
                responseType
        );
    }

    private ApiException createApiException(
            HttpResponse<String> response
    ) {
        String message = "HTTP error: " + response.statusCode();

        try {
            JsonNode json = mapper.readTree(response.body());

            if (json.has("error")) {
                message = json.get("error").asText();
            }
        } catch (IOException ignored) {
            // La respuesta no contiene JSON válido.
        }

        return new ApiException(
                response.statusCode(),
                message
        );
    }

    private String encode(String value) {
        Objects.requireNonNull(value);

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        ).replace("+", "%20");
    }

    // --------------------------------------------------
    // REQUEST RECORDS
    // --------------------------------------------------

    public record CreateRoomRequest(
            String roomId,
            String playerId
    ) {}

    public record JoinRoomRequest(
            String playerId
    ) {}

    public record PlayMoveRequest(
            String playerId,
            String from,
            String to,
            String promotionPiece
    ) {}

    // --------------------------------------------------
    // RESPONSE RECORDS
    // --------------------------------------------------

    public record RoomResponse(
            String roomId,
            String whitePlayerId,
            String blackPlayerId
    ) {}

    public record MoveResponse(
            String roomId,
            String whitePlayerId,
            String blackPlayerId,
            String nextTurn,
            List<MoveResponseItem> moves
    ) {}

    public record MoveHistoryResponse(
            String roomId,
            String nextTurn,
            List<MoveResponseItem> moves
    ) {}

    public record MoveResponseItem(
            int moveNumber,
            String from,
            String to,
            String promotionPiece
    ) {}

    // --------------------------------------------------
    // API EXCEPTION
    // --------------------------------------------------

    public static class ApiException extends RuntimeException {

        private final int statusCode;

        public ApiException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public int statusCode() {
            return statusCode;
        }
    }
}