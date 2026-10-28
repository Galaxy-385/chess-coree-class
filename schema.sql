
CREATE TABLE IF NOT EXISTS rooms (
                                     id VARCHAR(100) PRIMARY KEY,
    white_player_id VARCHAR(100) NOT NULL,
    black_player_id VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS games (
                                     id VARCHAR(100) PRIMARY KEY,
    room_id VARCHAR(100) NOT NULL UNIQUE
    REFERENCES rooms(id) ON DELETE CASCADE,

    initial_board_state JSONB NOT NULL,
    initial_turn VARCHAR(10) NOT NULL,

    initial_white_king_side BOOLEAN NOT NULL,
    initial_white_queen_side BOOLEAN NOT NULL,
    initial_black_king_side BOOLEAN NOT NULL,
    initial_black_queen_side BOOLEAN NOT NULL,

    initial_white_king_start VARCHAR(2) NOT NULL,
    initial_white_queen_rook_start VARCHAR(2) NOT NULL,
    initial_white_king_rook_start VARCHAR(2) NOT NULL,
    initial_black_king_start VARCHAR(2) NOT NULL,
    initial_black_queen_rook_start VARCHAR(2) NOT NULL,
    initial_black_king_rook_start VARCHAR(2) NOT NULL,

    initial_en_passant_target VARCHAR(2),

    board_state JSONB NOT NULL,
    turn VARCHAR(10) NOT NULL,

    white_king_side BOOLEAN NOT NULL,
    white_queen_side BOOLEAN NOT NULL,
    black_king_side BOOLEAN NOT NULL,
    black_queen_side BOOLEAN NOT NULL,

    white_king_start VARCHAR(2) NOT NULL,
    white_queen_rook_start VARCHAR(2) NOT NULL,
    white_king_rook_start VARCHAR(2) NOT NULL,
    black_king_start VARCHAR(2) NOT NULL,
    black_queen_rook_start VARCHAR(2) NOT NULL,
    black_king_rook_start VARCHAR(2) NOT NULL,

    en_passant_target VARCHAR(2),
    half_move_clock INTEGER NOT NULL DEFAULT 0,
    full_move_number INTEGER NOT NULL DEFAULT 1
    );

CREATE TABLE IF NOT EXISTS moves (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     game_id VARCHAR(100) NOT NULL
    REFERENCES games(id) ON DELETE CASCADE,

    ply_number INTEGER NOT NULL,
    move_number INTEGER NOT NULL,
    from_square VARCHAR(2) NOT NULL,
    to_square VARCHAR(2) NOT NULL,
    promotion_piece VARCHAR(20),
    played_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             UNIQUE (game_id, ply_number)
    );
