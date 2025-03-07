CREATE TABLE matchmaking_sessions (
                                      id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      game_id VARCHAR,
                                      status INT,
                                      players TEXT[],
                                      total_players INT,
                                      open_slots INT,
                                      is_closed BOOLEAN DEFAULT FALSE
);

CREATE TABLE matchmaking_requests (
      id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      players TEXT[],
      session_id VARCHAR, -- REFERENCES matchmaking_sessions(id),
      status INT
);
