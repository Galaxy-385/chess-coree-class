
CREATE TABLE IF NOT EXISTS rooms (
                                     id VARCHAR(100) PRIMARY KEY,
    white_player_id VARCHAR(100) NOT NULL,
    black_player_id VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS games (
                                     id VARCHAR(100) PRIMARY KEY,
    room_id VARCHAR(100) NOT NULL UNIQUE
    REFERENCES rooms(id) ON DELETE CASCADE,
    board_state TEXT NOT NULL,
    turn VARCHAR(10) NOT NULL,
    half_move_clock INTEGER NOT NULL DEFAULT 0,
    full_move_number INTEGER NOT NULL DEFAULT 1
    );

CREATE TABLE IF NOT EXISTS moves (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     game_id VARCHAR(100) NOT NULL
    REFERENCES games(id) ON DELETE CASCADE,
    move_number INTEGER NOT NULL,
    from_square VARCHAR(2) NOT NULL,
    to_square VARCHAR(2) NOT NULL,
    promotion_piece VARCHAR(20),
    played_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE (game_id, move_number)
    );
