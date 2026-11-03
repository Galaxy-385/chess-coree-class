
package software.ulpgc.chess.adapters.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import software.ulpgc.chess.application.CreateRoom;
import software.ulpgc.chess.application.JoinRoom;
import software.ulpgc.chess.application.GetRoom;
import software.ulpgc.chess.application.RoomNotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RoomController implements HttpHandler {

    private final CreateRoom createRoom;
    private final JoinRoom joinRoom;
    private final ObjectMapper mapper;
    private final GetRoom getRoom;

    public RoomController(
            CreateRoom createRoom,
            JoinRoom joinRoom,
            GetRoom getRoom
    ) {
        this.createRoom = createRoom;
        this.joinRoom = joinRoom;
        this.getRoom = getRoom;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.equals("/rooms") && method.equalsIgnoreCase("POST")) {
                createRoom(exchange);
                return;
            }

            if (path.matches("/rooms/[^/]+/join")
                    && method.equalsIgnoreCase("POST")) {
                joinRoom(exchange);
                return;
            }

            if (path.matches("/rooms/[^/]+")
                    && method.equalsIgnoreCase("GET")) {
                getRoom(exchange);
                return;
            }

            sendResponse(exchange, 404,
                    mapper.writeValueAsString(
                            new ErrorResponse("Endpoint not found")
                    ));


        } catch (RoomNotFoundException exception) {
            sendResponse(exchange, 404,
                    mapper.writeValueAsString(
                            new ErrorResponse(exception.getMessage())
                    ));

        } catch (IllegalArgumentException exception) {
            sendResponse(exchange, 400,
                    mapper.writeValueAsString(
                            new ErrorResponse(exception.getMessage())
                    ));
        } catch (IllegalStateException exception) {
            sendResponse(exchange, 409,
                    mapper.writeValueAsString(
                            new ErrorResponse(exception.getMessage())
                    ));
        } catch (Exception exception) {
            sendResponse(exchange, 500,
                    mapper.writeValueAsString(
                            new ErrorResponse("Internal server error")
                    ));
        }
    }

    private void createRoom(HttpExchange exchange) throws IOException {

        CreateRoomRequest request = mapper.readValue(
                exchange.getRequestBody(),
                CreateRoomRequest.class
        );

        var room = createRoom.execute(
                request.roomId(),
                request.playerId()
        );

        RoomResponse response = new RoomResponse(
                room.id(),
                room.whitePlayerId(),
                room.blackPlayerId()
        );

        sendResponse(exchange, 201, mapper.writeValueAsString(response));
    }

    private void joinRoom(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        String roomId = parts[2];

        JoinRoomRequest request = mapper.readValue(
                exchange.getRequestBody(),
                JoinRoomRequest.class
        );

        var room = joinRoom.execute(
                roomId,
                request.playerId()
        );

        RoomResponse response = new RoomResponse(
                room.id(),
                room.whitePlayerId(),
                room.blackPlayerId()
        );

        sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }

    private void sendResponse(
            HttpExchange exchange,
            int status,
            String body
    ) throws IOException {

        byte[] response = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, response.length);

        try (var output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    public record CreateRoomRequest(String roomId, String playerId) {}

    public record JoinRoomRequest(String playerId) {}

    public record RoomResponse(
            String roomId,
            String whitePlayerId,
            String blackPlayerId
    ) {}

    public record ErrorResponse(String error) {}

    private void getRoom(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        String roomId = parts[2];

        var room = getRoom.execute(roomId);

        RoomResponse response = new RoomResponse(
                room.id(),
                room.whitePlayerId(),
                room.blackPlayerId()
        );

        sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }
}
