package software.ulpgc.chess.adapters.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RoomApiClientTest {

    private HttpServer server;
    private RoomApiClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    private final AtomicReference<String> lastMethod =
            new AtomicReference<>();

    private final AtomicReference<String> lastPath =
            new AtomicReference<>();

    private final AtomicReference<String> lastBody =
            new AtomicReference<>();

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(
                new InetSocketAddress("localhost", 0), 0
        );

        server.createContext("/", exchange -> {
            lastMethod.set(exchange.getRequestMethod());
            lastPath.set(exchange.getRequestURI().getRawPath());

            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            lastBody.set(body);

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            System.out.println("Método recibido: " + exchange.getRequestMethod());
            System.out.println("Ruta recibida: " + exchange.getRequestURI().getPath());

            System.out.println("Comparación de ruta: " +
                    path.equals("/rooms/sala-001"));
            System.out.println("Comparación de método: " +
                    method.equals("GET"));

            int status = 200;
            String response;

            if (path.equals("/rooms") && method.equals("POST")) {
                status = 201;
                response = """
                        {
                            "roomId": "sala-001",
                            "whitePlayerId": "jugador-1",
                            "blackPlayerId": null
                        }
                        """;

            } else if (path.equals("/rooms/sala-001/join")) {
                response = """
                        {
                            "roomId": "sala-001",
                            "whitePlayerId": "jugador-1",
                            "blackPlayerId": "jugador-2"
                        }
                        """;

            } else if (path.equals("/rooms/sala-001")
                    && method.equals("GET")) {

                response = """
            {
                "roomId": "sala-001",
                "whitePlayerId": "jugador-1",
                "blackPlayerId": "jugador-2"
            }
            """;

            } else if (path.equals("/rooms/sala 001")
                    && method.equals("GET")) {
                response = """
            {
                "roomId": "sala 001",
                "whitePlayerId": "jugador-1",
                "blackPlayerId": null
            }
            """;

            } else if (path.equals("/rooms/sala-001/moves")
                    && method.equals("POST")) {
                response = """
                        {
                            "roomId": "sala-001",
                            "whitePlayerId": "jugador-1",
                            "blackPlayerId": "jugador-2",
                            "nextTurn": "Black",
                            "moves": [
                                {
                                    "moveNumber": 1,
                                    "from": "e2",
                                    "to": "e4",
                                    "promotionPiece": null
                                }
                            ]
                        }
                        """;

            } else if (path.equals("/rooms/sala-001/moves")
                    && method.equals("GET")) {
                response = """
                        {
                            "roomId": "sala-001",
                            "nextTurn": "Black",
                            "moves": [
                                {
                                    "moveNumber": 1,
                                    "from": "e2",
                                    "to": "e4",
                                    "promotionPiece": null
                                }
                            ]
                        }
                        """;

            } else if (path.equals("/rooms/error")) {
                status = 400;
                response = """
                        {"error": "Invalid request"}
                        """;

            } else if (path.equals("/rooms/html-error")) {
                status = 500;
                response = "<html>Internal server error</html>";

            } else {
                System.out.println("ENTRA EN EL ELSE 404");
                status = 404;
                response = """
                        {"error": "Not found"}
                        """;
            }

            byte[] responseBytes =
                    response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json; charset=UTF-8"
            );

            exchange.sendResponseHeaders(status, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
            exchange.close();
        });

        server.start();

        int port = server.getAddress().getPort();

        client = new RoomApiClient(
                "http://localhost:" + port
        );
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldCreateRoom() throws Exception {
        RoomApiClient.RoomResponse response =
                client.createRoom("sala-001", "jugador-1");

        assertEquals("sala-001", response.roomId());
        assertEquals("jugador-1", response.whitePlayerId());
        assertNull(response.blackPlayerId());

        assertEquals("POST", lastMethod.get());
        assertEquals("/rooms", lastPath.get());

        JsonNode body = mapper.readTree(lastBody.get());

        assertEquals("sala-001", body.get("roomId").asText());
        assertEquals("jugador-1", body.get("playerId").asText());
    }

    @Test
    void shouldJoinRoom() throws Exception {
        RoomApiClient.RoomResponse response =
                client.joinRoom("sala-001", "jugador-2");

        assertEquals("sala-001", response.roomId());
        assertEquals("jugador-1", response.whitePlayerId());
        assertEquals("jugador-2", response.blackPlayerId());

        assertEquals("POST", lastMethod.get());
        assertEquals("/rooms/sala-001/join", lastPath.get());

        JsonNode body = mapper.readTree(lastBody.get());

        assertEquals("jugador-2", body.get("playerId").asText());
    }

    @Test
    void shouldGetRoom() throws Exception {
        RoomApiClient.RoomResponse response =
                client.getRoom("sala-001");

        assertEquals("sala-001", response.roomId());
        assertEquals("jugador-1", response.whitePlayerId());
        assertEquals("jugador-2", response.blackPlayerId());

        assertEquals("GET", lastMethod.get());
        assertEquals("/rooms/sala-001", lastPath.get());
        assertTrue(lastBody.get().isEmpty());
    }

    @Test
    void shouldPlayMove() throws Exception {
        RoomApiClient.MoveResponse response =
                client.playMove(
                        "sala-001",
                        "jugador-1",
                        "e2",
                        "e4",
                        null
                );

        assertEquals("sala-001", response.roomId());
        assertEquals("Black", response.nextTurn());
        assertEquals(1, response.moves().size());

        RoomApiClient.MoveResponseItem move =
                response.moves().get(0);

        assertEquals(1, move.moveNumber());
        assertEquals("e2", move.from());
        assertEquals("e4", move.to());
        assertNull(move.promotionPiece());

        assertEquals("POST", lastMethod.get());
        assertEquals("/rooms/sala-001/moves", lastPath.get());

        JsonNode body = mapper.readTree(lastBody.get());

        assertEquals("jugador-1", body.get("playerId").asText());
        assertEquals("e2", body.get("from").asText());
        assertEquals("e4", body.get("to").asText());
        assertTrue(body.get("promotionPiece").isNull());
    }

    @Test
    void shouldGetMoveHistory() throws Exception {
        RoomApiClient.MoveHistoryResponse response =
                client.getMoves("sala-001");

        assertEquals("sala-001", response.roomId());
        assertEquals("Black", response.nextTurn());
        assertEquals(1, response.moves().size());

        RoomApiClient.MoveResponseItem move =
                response.moves().get(0);

        assertEquals(1, move.moveNumber());
        assertEquals("e2", move.from());
        assertEquals("e4", move.to());

        assertEquals("GET", lastMethod.get());
        assertEquals("/rooms/sala-001/moves", lastPath.get());
        assertTrue(lastBody.get().isEmpty());
    }

    @Test
    void shouldEncodeRoomIdInUrl() throws Exception {
        client.getRoom("sala 001");

        assertEquals("/rooms/sala%20001", lastPath.get());
    }

    @Test
    void shouldThrowApiExceptionWhenServerReturnsError()
            throws Exception {

        RoomApiClient.ApiException exception =
                assertThrows(
                        RoomApiClient.ApiException.class,
                        () -> client.getRoom("error")
                );

        assertEquals(400, exception.statusCode());
        assertEquals("Invalid request", exception.getMessage());
    }

    @Test
    void shouldHandleErrorResponseWithoutJson()
            throws Exception {

        RoomApiClient.ApiException exception =
                assertThrows(
                        RoomApiClient.ApiException.class,
                        () -> client.getRoom("html-error")
                );

        assertEquals(500, exception.statusCode());
        assertEquals("HTTP error: 500", exception.getMessage());
    }

    @Test
    void shouldRejectEmptyBaseUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RoomApiClient("")
        );
    }

    @Test
    void shouldRejectNullBaseUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RoomApiClient(null)
        );
    }
}