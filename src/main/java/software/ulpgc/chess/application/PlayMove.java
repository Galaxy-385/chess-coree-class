package software.ulpgc.chess.application;

import software.ulpgc.chess.domain.Color;
import software.ulpgc.chess.domain.Piece;
import software.ulpgc.chess.domain.Room;
import software.ulpgc.chess.domain.Square;
import software.ulpgc.chess.ports.RoomRepository;

import java.util.Objects;

public class PlayMove {

    private final RoomRepository roomRepository;

    public PlayMove(RoomRepository roomRepository) {
        this.roomRepository = Objects.requireNonNull(roomRepository);
    }

    public Room execute(
            String roomId,
            String playerId,
            String from,
            String to,
            Piece promotionPiece
    ) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room ID cannot be empty");
        }

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID cannot be empty");
        }

        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new IllegalArgumentException("Origin and destination cannot be empty");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        Color playerColor;

        if (room.whitePlayerId().equals(playerId)) {
            playerColor = Color.White;
        } else if (playerId.equals(room.blackPlayerId())) {
            playerColor = Color.Black;
        } else {
            throw new IllegalArgumentException(
                    "Player does not belong to this room"
            );
        }

        if (room.game().turn() != playerColor) {
            throw new IllegalStateException("It is not this player's turn");
        }

        Square origin = Square.at(from);
        Square destination = Square.at(to);

        if (promotionPiece == null) {
            room.game().move(origin, destination);
        } else {
            room.game().move(origin, destination, promotionPiece);
        }

        roomRepository.save(room);

        return room;
    }
}