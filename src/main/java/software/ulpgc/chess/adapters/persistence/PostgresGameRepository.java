
package software.ulpgc.chess.adapters.persistence;

import software.ulpgc.chess.domain.Board;
import software.ulpgc.chess.domain.CastlingRights;
import software.ulpgc.chess.domain.Color;
import software.ulpgc.chess.domain.Game;
import software.ulpgc.chess.domain.PlayedMove;
import software.ulpgc.chess.domain.Piece;
import software.ulpgc.chess.domain.Square;
import software.ulpgc.chess.ports.GameRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PostgresGameRepository implements GameRepository {

    private final PostgresConnection connection;
    private final BoardStateMapper boardStateMapper;
    private final BoardStateJsonMapper jsonMapper;

    public PostgresGameRepository(PostgresConnection connection) {
        this.connection = Objects.requireNonNull(connection);
        this.boardStateMapper = new BoardStateMapper();
        this.jsonMapper = new BoardStateJsonMapper();
    }

    @Override
    public void save(String gameId, String roomId, Game game) {
        Objects.requireNonNull(gameId, "Game ID cannot be null");
        Objects.requireNonNull(roomId, "Room ID cannot be null");
        Objects.requireNonNull(game, "Game cannot be null");

        String sql = """
                INSERT INTO games (
                    id, room_id,
                    initial_board_state, initial_turn,
                    initial_white_king_side, initial_white_queen_side,
                    initial_black_king_side, initial_black_queen_side,
                    initial_white_king_start, initial_white_queen_rook_start,
                    initial_white_king_rook_start, initial_black_king_start,
                    initial_black_queen_rook_start, initial_black_king_rook_start,
                    initial_en_passant_target,
                    board_state, turn,
                    white_king_side, white_queen_side,
                    black_king_side, black_queen_side,
                    white_king_start, white_queen_rook_start,
                    white_king_rook_start, black_king_start,
                    black_queen_rook_start, black_king_rook_start,
                    en_passant_target, half_move_clock, full_move_number
                )
                VALUES (
                    ?, ?,
                    ?::jsonb, ?,
                    ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?,
                    ?,
                    ?::jsonb, ?,
                    ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?,
                    ?, ?, ?
                )
                ON CONFLICT (id) DO UPDATE SET
                    room_id = EXCLUDED.room_id,
                    initial_board_state = EXCLUDED.initial_board_state,
                    initial_turn = EXCLUDED.initial_turn,
                    initial_white_king_side = EXCLUDED.initial_white_king_side,
                    initial_white_queen_side = EXCLUDED.initial_white_queen_side,
                    initial_black_king_side = EXCLUDED.initial_black_king_side,
                    initial_black_queen_side = EXCLUDED.initial_black_queen_side,
                    initial_white_king_start = EXCLUDED.initial_white_king_start,
                    initial_white_queen_rook_start = EXCLUDED.initial_white_queen_rook_start,
                    initial_white_king_rook_start = EXCLUDED.initial_white_king_rook_start,
                    initial_black_king_start = EXCLUDED.initial_black_king_start,
                    initial_black_queen_rook_start = EXCLUDED.initial_black_queen_rook_start,
                    initial_black_king_rook_start = EXCLUDED.initial_black_king_rook_start,
                    initial_en_passant_target = EXCLUDED.initial_en_passant_target,
                    board_state = EXCLUDED.board_state,
                    turn = EXCLUDED.turn,
                    white_king_side = EXCLUDED.white_king_side,
                    white_queen_side = EXCLUDED.white_queen_side,
                    black_king_side = EXCLUDED.black_king_side,
                    black_queen_side = EXCLUDED.black_queen_side,
                    white_king_start = EXCLUDED.white_king_start,
                    white_queen_rook_start = EXCLUDED.white_queen_rook_start,
                    white_king_rook_start = EXCLUDED.white_king_rook_start,
                    black_king_start = EXCLUDED.black_king_start,
                    black_queen_rook_start = EXCLUDED.black_queen_rook_start,
                    black_king_rook_start = EXCLUDED.black_king_rook_start,
                    en_passant_target = EXCLUDED.en_passant_target,
                    half_move_clock = EXCLUDED.half_move_clock,
                    full_move_number = EXCLUDED.full_move_number
                """;

        String deleteMovesSql = "DELETE FROM moves WHERE game_id = ?";
        String insertMoveSql = """
                INSERT INTO moves (
                    game_id, ply_number, move_number,
                    from_square, to_square, promotion_piece
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection db = connection.open()) {
            db.setAutoCommit(false);

            try {
                try (PreparedStatement statement = db.prepareStatement(sql)) {
                    int index = 1;

                    statement.setString(index++, gameId);
                    statement.setString(index++, roomId);

                    Board initialBoard = game.initialBoard();
                    Board currentBoard = game.board();

                    statement.setString(index++, toJson(initialBoard));
                    statement.setString(index++, game.initialTurn().name());

                    index = bindCastlingRights(
                            statement, index, initialBoard.castlingRights()
                    );

                    setSquare(statement, index++, initialBoard.enPassantTarget());

                    statement.setString(index++, toJson(currentBoard));
                    statement.setString(index++, game.turn().name());

                    index = bindCastlingRights(
                            statement, index, currentBoard.castlingRights()
                    );

                    setSquare(statement, index++, currentBoard.enPassantTarget());

                    statement.setInt(index++, game.halfMoveClock());
                    statement.setInt(index, game.fullMoveNumber());

                    statement.executeUpdate();
                }

                try (PreparedStatement delete = db.prepareStatement(deleteMovesSql)) {
                    delete.setString(1, gameId);
                    delete.executeUpdate();
                }

                try (PreparedStatement insert = db.prepareStatement(insertMoveSql)) {
                    int plyNumber = 1;

                    for (PlayedMove move : game.moves()) {
                        insert.setString(1, gameId);
                        insert.setInt(2, plyNumber++);
                        insert.setInt(3, move.moveNumber());
                        insert.setString(4, squareToString(move.from()));
                        insert.setString(5, squareToString(move.to()));

                        if (move.promotionPiece() == null) {
                            insert.setNull(6, Types.VARCHAR);
                        } else {
                            insert.setString(6, move.promotionPiece().name());
                        }

                        insert.addBatch();
                    }

                    insert.executeBatch();
                }

                db.commit();

            } catch (SQLException | RuntimeException exception) {
                db.rollback();
                throw exception;
            }

        } catch (SQLException exception) {
            throw new IllegalStateException("Could not save the game", exception);
        }
    }

    @Override
    public Optional<Game> findById(String gameId) {
        Objects.requireNonNull(gameId, "Game ID cannot be null");

        String gameSql = "SELECT * FROM games WHERE id = ?";
        String movesSql = """
                SELECT move_number, from_square, to_square, promotion_piece
                FROM moves
                WHERE game_id = ?
                ORDER BY ply_number
                """;

        try (Connection db = connection.open()) {

            Board initialBoard;
            Color initialTurn;

            try (PreparedStatement statement = db.prepareStatement(gameSql)) {
                statement.setString(1, gameId);

                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        return Optional.empty();
                    }

                    Map<String, String> initialState =
                            jsonMapper.fromJson(result.getString("initial_board_state"));

                    CastlingRights initialRights =
                            readCastlingRights(result, "initial_");

                    Square initialEnPassant =
                            readSquare(result.getString("initial_en_passant_target"));

                    initialBoard = boardStateMapper.fromMap(
                            initialState, initialRights, initialEnPassant
                    );

                    initialTurn = Color.valueOf(result.getString("initial_turn"));
                }
            }

            List<PlayedMove> moves = new ArrayList<>();

            try (PreparedStatement statement = db.prepareStatement(movesSql)) {
                statement.setString(1, gameId);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        String promotion = result.getString("promotion_piece");

                        Piece promotionPiece = promotion == null
                                ? null
                                : Piece.valueOf(promotion);

                        moves.add(new PlayedMove(
                                result.getInt("move_number"),
                                Square.at(result.getString("from_square")),
                                Square.at(result.getString("to_square")),
                                promotionPiece
                        ));
                    }
                }
            }

            return Optional.of(Game.restore(initialBoard, initialTurn, moves));

        } catch (SQLException exception) {
            throw new IllegalStateException("Could not retrieve the game", exception);
        }
    }

    private String toJson(Board board) {
        return jsonMapper.toJson(boardStateMapper.toMap(board));
    }

    private int bindCastlingRights(
            PreparedStatement statement,
            int index,
            CastlingRights rights
    ) throws SQLException {

        statement.setBoolean(index++, rights.whiteKingSide());
        statement.setBoolean(index++, rights.whiteQueenSide());
        statement.setBoolean(index++, rights.blackKingSide());
        statement.setBoolean(index++, rights.blackQueenSide());

        statement.setString(index++, squareToString(rights.whiteKingStart()));
        statement.setString(index++, squareToString(rights.whiteQueenRookStart()));
        statement.setString(index++, squareToString(rights.whiteKingRookStart()));
        statement.setString(index++, squareToString(rights.blackKingStart()));
        statement.setString(index++, squareToString(rights.blackQueenRookStart()));
        statement.setString(index++, squareToString(rights.blackKingRookStart()));

        return index;
    }

    private CastlingRights readCastlingRights(
            ResultSet result,
            String prefix
    ) throws SQLException {

        return new CastlingRights(
                result.getBoolean(prefix + "white_king_side"),
                result.getBoolean(prefix + "white_queen_side"),
                result.getBoolean(prefix + "black_king_side"),
                result.getBoolean(prefix + "black_queen_side"),

                Square.at(result.getString(prefix + "white_king_start")),
                Square.at(result.getString(prefix + "white_queen_rook_start")),
                Square.at(result.getString(prefix + "white_king_rook_start")),
                Square.at(result.getString(prefix + "black_king_start")),
                Square.at(result.getString(prefix + "black_queen_rook_start")),
                Square.at(result.getString(prefix + "black_king_rook_start"))
        );
    }

    private void setSquare(
            PreparedStatement statement,
            int index,
            Square square
    ) throws SQLException {

        if (square == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, squareToString(square));
        }
    }

    private String squareToString(Square square) {
        if (square == null) {
            return null;
        }

        return square.file().name().toLowerCase()
                + square.rank().name().substring(1);
    }

    private Square readSquare(String value) {
        return value == null ? null : Square.at(value);
    }
}
