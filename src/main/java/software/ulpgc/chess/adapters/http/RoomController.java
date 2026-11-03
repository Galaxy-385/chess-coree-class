package software.ulpgc.chess.adapters.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import software.ulpgc.chess.application.CreateRoom;
import software.ulpgc.chess.application.GetRoom;
import software.ulpgc.chess.application.JoinRoom;
import software.ulpgc.chess.application.PlayMove;
import software.ulpgc.chess.application.RoomNotFoundException;
import software.ulpgc.chess.domain.Piece;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RoomController implements HttpHandler {

    private final CreateRoom createRoom;
    private final JoinRoom joinRoom;
    private final GetRoom getRoom;
    private final PlayMove playMove;
    private final ObjectMapper mapper;

    public RoomController(
            CreateRoom createRoom,
            JoinRoom joinRoom,
            GetRoom getRoom,
            PlayMove playMove
    ) {
        this.createRoom = createRoom;
        this.joinRoom = joinRoom;
        this.getRoom = getRoom;
        this.playMove = playMove;
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

            if (path.matches("/rooms/[^/]+/moves")
                    && method.equalsIgnoreCase("POST")) {
                playMove(exchange);
                return;
            }

            if (path.matches("/rooms/[^/]+/moves")
                    && method.equalsIgnoreCase("GET")) {
                getMoves(exchange);
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

        String[] parts = exchange.getRequestURI().getPath().split("/");
        String roomId = parts[2];

        JoinRoomRequest request = mapper.readValue(
                exchange.getRequestBody(),
                JoinRoomRequest.class
        );

        var room = joinRoom.execute(roomId, request.playerId());

        RoomResponse response = new RoomResponse(
                room.id(),
                room.whitePlayerId(),
                room.blackPlayerId()
        );

        sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }

    private void getRoom(HttpExchange exchange) throws IOException {

        String[] parts = exchange.getRequestURI().getPath().split("/");
        String roomId = parts[2];

        var room = getRoom.execute(roomId);

        RoomResponse response = new RoomResponse(
                room.id(),
                room.whitePlayerId(),
                room.blackPlayerId()
        );

        sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }

    private void getMoves(HttpExchange exchange) throws IOException {

        String[] parts = exchange.getRequestURI().getPath().split("/");
        String roomId = parts[2];

        var room = getRoom.execute(roomId);

        List<MoveResponseItem> moves = room.game().moves().stream()
                .map(move -> new MoveResponseItem(
                        move.moveNumber(),
                        move.from().file().name().toLowerCase()
                                + move.from().rank().name().substring(1),
                        move.to().file().name().toLowerCase()
                                + move.to().rank().name().substring(1),
                        move.promotionPiece() == null
                                ? null
                                : move.promotionPiece().name()
                ))
                .toList();

        MoveHistoryResponse response = new MoveHistoryResponse(
                room.id(),
                room.game().turn().toString(),
                moves
        );

        sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }

    private void playMove(HttpExchange exchange) throws IOException {

        String[] parts = exchange.getRequestURI().getPath().split("/");
        String roomId = parts[2];

        PlayMoveRequest request = mapper.readValue(
                exchange.getRequestBody(),
                PlayMoveRequest.class
        );

        Piece promotionPiece = request.promotionPiece() == null
                || request.promotionPiece().isBlank()
                ? null
                : Piece.valueOf(request.promotionPiece());

        var room = playMove.execute(
                roomId,
                request.playerId(),
                request.from(),
                request.to(),
                promotionPiece
        );

        List<MoveResponseItem> moves = room.game().moves().stream()
                .map(move -> new MoveResponseItem(
                        move.moveNumber(),
                        move.from().file().name().toLowerCase()
                                + move.from().rank().name().substring(1),
                        move.to().file().name().toLowerCase()
                                + move.to().rank().name().substring(1),
                        move.promotionPiece() == null
                                ? null
                                : move.promotionPiece().name()
                ))
                .toList();

        MoveResponse response = new MoveResponse(
                room.id(),
                room.whitePlayerId(),
                room.blackPlayerId(),
                room.game().turn().toString(),
                moves
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

    public record PlayMoveRequest(
            String playerId,
            String from,
            String to,
            String promotionPiece
    ) {}

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

    public record MoveResponseItem(
            int moveNumber,
            String from,
            String to,
            String promotionPiece
    ) {}

    public record MoveHistoryResponse(
            String roomId,
            String nextTurn,
            List<MoveResponseItem> moves
    ) {}

    public record ErrorResponse(String error) {}
}