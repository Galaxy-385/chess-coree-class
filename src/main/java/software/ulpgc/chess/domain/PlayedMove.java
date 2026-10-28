package software.ulpgc.chess.domain;

import java.util.Objects;

public record PlayedMove(
        int moveNumber,
        Square from,
        Square to,
        Piece promotionPiece
) {
    public PlayedMove {
        if (moveNumber < 1) {
            throw new IllegalArgumentException(
                    "Move number must be positive"
            );
        }

        Objects.requireNonNull(from, "Origin square cannot be null");
        Objects.requireNonNull(to, "Destination square cannot be null");
    }
}