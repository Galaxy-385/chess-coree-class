package software.ulpgc.chess.adapters.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.CreateRoom;
import software.ulpgc.chess.application.GetRoom;
import software.ulpgc.chess.application.JoinRoom;
import software.ulpgc.chess.application.PlayMove;
import software.ulpgc.chess.domain.model.Room;
import software.ulpgc.chess.ports.RoomRepository;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RoomControllerTest {

    private HttpServer server;
    private HttpClient client;
    private ObjectMapper mapper;
    private InMemoryRoomRepository repository;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        repository = new InMemoryRoomRepository();
        mapper = new ObjectMapper();
        client = HttpClient.newHttpClient();

        CreateRoom createRoom = new CreateRoom(repository);
        JoinRoom joinRoom = new JoinRoom(repository);
        GetRoom getRoom = new GetRoom(repository);
        PlayMove playMove = new PlayMove(repository);

        server = HttpServer.create(
                new InetSocketAddress("localhost", 0),
                0
        );

        server.createContext(
                "/rooms",
                new RoomController(
                        createRoom,
                        joinRoom,
                        getRoom,
                        playMove
                )
        );

        server.start();

        baseUrl = "http://localhost:"
                + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    // --------------------------------------------------
    // CREATE ROOM
    // --------------------------------------------------

    @Test
    void shouldCreateRoom() throws Exception {
        HttpResponse<String> response = post(
                "/rooms",
                """
                {
                    "roomId": "room-1",
                    "playerId": "player-1"
                }
                """
        );

        assertEquals(201, response.statusCode());

        JsonNode json = mapper.readTree(response.body());

        assertEquals("room-1", json.get("roomId").asText());
        assertEquals("player-1", json.get("whitePlayerId").asText());
        assertTrue(json.get("blackPlayerId").isNull());

        assertTrue(repository.findById("room-1").isPresent());
    }

    @Test
    void shouldRejectDuplicateRoomId() throws Exception {
        createRoom("room-1", "player-1");

        HttpResponse<String> response = post(
                "/rooms",
                """
                {
                    "roomId": "room-1",
                    "playerId": "player-2"
                }
                """
        );

        assertEquals(400, response.statusCode());
        assertEquals(1, repository.rooms.size());
    }

    @Test
    void shouldRejectEmptyRoomId() throws Exception {
        HttpResponse<String> response = post(
                "/rooms",
                """
                {
                    "roomId": "",
                    "playerId": "player-1"
                }
                """
        );

        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldRejectEmptyPlayerIdWhenCreatingRoom()
            throws Exception {
        HttpResponse<String> response = post(
                "/rooms",
                """
                {
                    "roomId": "room-1",
                    "playerId": " "
                }
                """
        );

        assertEquals(400, response.statusCode());
    }

    // --------------------------------------------------
    // JOIN ROOM
    // --------------------------------------------------

    @Test
    void shouldAllowSecondPlayerToJoin() throws Exception {
        createRoom("room-1", "player-1");

        HttpResponse<String> response = post(
                "/rooms/room-1/join",
                """
                {
                    "playerId": "player-2"
                }
                """
        );

        assertEquals(200, response.statusCode());

        JsonNode json = mapper.readTree(response.body());

        assertEquals("room-1", json.get("roomId").asText());
        assertEquals("player-1", json.get("whitePlayerId").asText());
        assertEquals("player-2", json.get("blackPlayerId").asText());
    }

    @Test
    void shouldRejectJoiningNonExistingRoom() throws Exception {
        HttpResponse<String> response = post(
                "/rooms/unknown-room/join",
                """
                {
                    "playerId": "player-2"
                }
                """
        );

        // JoinRoom.execute uses RoomNotFoundException
        // when the room does not exist.
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldRejectThirdPlayer() throws Exception {
        createRoom("room-1", "player-1");
        joinRoom("room-1", "player-2");

        HttpResponse<String> response = post(
                "/rooms/room-1/join",
                """
                {
                    "playerId": "player-3"
                }
                """
        );

        assertEquals(409, response.statusCode());
    }

    @Test
    void shouldAllowWhitePlayerToJoinAgain() throws Exception {
        createRoom("room-1", "player-1");

        HttpResponse<String> response = post(
                "/rooms/room-1/join",
                """
                {
                    "playerId": "player-1"
                }
                """
        );

        assertEquals(200, response.statusCode());

        JsonNode json = mapper.readTree(response.body());

        assertEquals("room-1", json.get("roomId").asText());
        assertEquals("player-1", json.get("whitePlayerId").asText());
        assertTrue(json.get("blackPlayerId").isNull());
    }

    // --------------------------------------------------
    // GET ROOM
    // --------------------------------------------------

    @Test
    void shouldGetExistingRoom() throws Exception {
        createRoom("room-1", "player-1");
        joinRoom("room-1", "player-2");

        HttpResponse<String> response = get("/rooms/room-1");

        assertEquals(200, response.statusCode());

        JsonNode json = mapper.readTree(response.body());

        assertEquals("room-1", json.get("roomId").asText());
        assertEquals("player-1", json.get("whitePlayerId").asText());
        assertEquals("player-2", json.get("blackPlayerId").asText());
    }

    @Test
    void shouldReturn404ForNonExistingRoom() throws Exception {
        HttpResponse<String> response = get("/rooms/unknown-room");

        assertEquals(404, response.statusCode());
    }

    // --------------------------------------------------
    // PLAY MOVES
    // --------------------------------------------------

    @Test
    void shouldAllowWhitePlayerToMove() throws Exception {
        createRoom("room-1", "player-1");
        joinRoom("room-1", "player-2");

        HttpResponse<String> response = post(
                "/rooms/room-1/moves",
                """
                {
                    "playerId": "player-1",
                    "from": "e2",
                    "to": "e4",
                    "promotionPiece": null
                }
                """
        );

        assertEquals(200, response.statusCode());

        JsonNode json = mapper.readTree(response.body());

        assertEquals("room-1", json.get("roomId").asText());
        assertEquals("Black", json.get("nextTurn").asText());
        assertEquals(1, json.get("moves").size());
        assertEquals("e2", json.get("moves").get(0).get("from").asText());
        assertEquals("e4", json.get("moves").get(0).get("to").asText());
    }

    @Test
    void shouldAllowBlackPlayerToMoveAfterWhite()
            throws Exception {
        createRoom("room-1", "player-1");
        joinRoom("room-1", "player-2");

        post(
                "/rooms/room-1/moves",
                """
                {
                    "playerId": "player-1",
                    "from": "e2",
                    "to": "e4",
                    "promotionPiece": null
                }
                """
        );

        HttpResponse<String> response = post(
                "/rooms/room-1/moves",
                """
                {
                    "playerId": "player-2",
                    "from": "e7",
                    "to": "e5",
                    "promotionPiece": null
                }
                """
        );

        assertEquals(200, response.statusCode());

        JsonNode json = mapper.readTree(response.body());

        assertEquals("White", json.get("nextTurn").asText());
        assertEquals(2, json.get("moves").size());

        assertEquals("e7", json.get("moves").get(1).get("from").asText());
        assertEquals("e5", json.get("moves").get(1).get("to").asText());
    }

    @Test
    void shouldRejectMoveWhenItIsNotPlayersTurn()
            throws Exception {
        createRoom("room-1", "player-1");
        joinRoom("room-1", "player-2");

        HttpResponse<String> response = post(
                "/rooms/room-1/moves",
                """
                {
                    "playerId": "player-2",
                    "from": "e7",
                    "to": "e5",
                    "promotionPiece": null
                }
                """
        );

        assertEquals(409, response.statusCode());
    }

    @Test
    void shouldRejectPlayerWhoDoesNotBelongToRoom()
            throws Exception {
        createRoom("room-1", "player-1");
        joinRoom("room-1", "player-2");

        HttpResponse<String> response = post(
                "/rooms/room-1/moves",
                """
                {
                    "playerId": "player-3",
                    "from": "e2",
                    "to": "e4",
                    "promotionPiece": null
                }
                """
        );

        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldRejectIllegalMove() throws Exception {
        createRoom("room-1", "player-1");
        joinRoom("room-1", "player-2");

        HttpResponse<String> response = post(
                "/rooms/room-1/moves",
                """
                {
                    "playerId": "player-1",
                    "from": "e2",
                    "to": "e5",
                    "promotionPiece": null
                }
                """
        );

        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldReturn404WhenMovingInNonExistingRoom()
            throws Exception {
        HttpResponse<String> response = post(
                "/rooms/unknown-room/moves",
                """
                {
                    "playerId": "player-1",
                    "from": "e2",
                    "to": "e4",
                    "promotionPiece": null
                }
                """
        );

        assertEquals(404, response.statusCode());
    }

    // --------------------------------------------------
    // MOVE HISTORY
    // --------------------------------------------------

    @Test
    void shouldReturnMoveHistory() throws Exception {
        createRoom("room-1", "player-1");
        joinRoom("room-1", "player-2");

        post(
                "/rooms/room-1/moves",
                """
                {
                    "playerId": "player-1",
                    "from": "e2",
                    "to": "e4",
                    "promotionPiece": null
                }
                """
        );

        post(
                "/rooms/room-1/moves",
                """
                {
                    "playerId": "player-2",
                    "from": "e7",
                    "to": "e5",
                    "promotionPiece": null
                }
                """
        );

        HttpResponse<String> response = get(
                "/rooms/room-1/moves"
        );

        assertEquals(200, response.statusCode());

        JsonNode json = mapper.readTree(response.body());

        assertEquals("room-1", json.get("roomId").asText());
        assertEquals("White", json.get("nextTurn").asText());
        assertEquals(2, json.get("moves").size());

        assertEquals("e2", json.get("moves").get(0).get("from").asText());
        assertEquals("e4", json.get("moves").get(0).get("to").asText());

        assertEquals("e7", json.get("moves").get(1).get("from").asText());
        assertEquals("e5", json.get("moves").get(1).get("to").asText());
    }

    @Test
    void shouldReturnEmptyHistoryForNewRoom() throws Exception {
        createRoom("room-1", "player-1");

        HttpResponse<String> response = get(
                "/rooms/room-1/moves"
        );

        assertEquals(200, response.statusCode());

        JsonNode json = mapper.readTree(response.body());

        assertEquals("room-1", json.get("roomId").asText());
        assertEquals("White", json.get("nextTurn").asText());
        assertEquals(0, json.get("moves").size());
    }

    // --------------------------------------------------
    // UNKNOWN ENDPOINT
    // --------------------------------------------------

    @Test
    void shouldReturn404ForUnknownEndpoint() throws Exception {
        HttpResponse<String> response = get("/unknown");

        assertEquals(404, response.statusCode());
    }

    // --------------------------------------------------
    // HTTP HELPERS
    // --------------------------------------------------

    private HttpResponse<String> post(
            String path,
            String body
    ) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private HttpResponse<String> get(String path)
            throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private void createRoom(
            String roomId,
            String playerId
    ) throws Exception {

        HttpResponse<String> response = post(
                "/rooms",
                mapper.writeValueAsString(
                        Map.of(
                                "roomId", roomId,
                                "playerId", playerId
                        )
                )
        );

        assertEquals(201, response.statusCode());
    }

    private void joinRoom(
            String roomId,
            String playerId
    ) throws Exception {

        HttpResponse<String> response = post(
                "/rooms/" + roomId + "/join",
                mapper.writeValueAsString(
                        Map.of("playerId", playerId)
                )
        );

        assertEquals(200, response.statusCode());
    }

    // --------------------------------------------------
    // IN-MEMORY REPOSITORY
    // --------------------------------------------------

    private static class InMemoryRoomRepository
            implements RoomRepository {

        private final Map<String, Room> rooms = new HashMap<>();

        @Override
        public void save(Room room) {
            rooms.put(room.id(), room);
        }

        @Override
        public Optional<Room> findById(String roomId) {
            return Optional.ofNullable(rooms.get(roomId));
        }

        @Override
        public void deleteById(String roomId) {
            rooms.remove(roomId);
        }
    }
}