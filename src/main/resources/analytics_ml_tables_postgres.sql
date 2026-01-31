-- Analytics and ML Tables for RAKCHA Enhancement Suite (PostgreSQL)
-- These tables support real-time analytics, machine learning, and advanced features

-- Analytics Tables
CREATE TABLE IF NOT EXISTS analytics_metrics
(
  id           BIGSERIAL PRIMARY KEY,
  metric_name  VARCHAR(100)   NOT NULL,
  metric_value DECIMAL(15, 4) NOT NULL,
  metric_type  VARCHAR(50)    NOT NULL, -- 'revenue', 'attendance', 'performance', etc.
  cinema_id    BIGINT,
  film_id      BIGINT,
  timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  metadata     JSONB
);

CREATE INDEX IF NOT EXISTS idx_analytics_timestamp ON analytics_metrics (timestamp);
CREATE INDEX IF NOT EXISTS idx_analytics_type ON analytics_metrics (metric_type);
CREATE INDEX IF NOT EXISTS idx_analytics_cinema ON analytics_metrics (cinema_id);
CREATE INDEX IF NOT EXISTS idx_analytics_film ON analytics_metrics (film_id);

CREATE TABLE IF NOT EXISTS real_time_events
(
  id         BIGSERIAL PRIMARY KEY,
  event_type VARCHAR(100) NOT NULL,
  event_data JSONB        NOT NULL,
  user_id    BIGINT,
  session_id VARCHAR(100),
  timestamp  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  processed  BOOLEAN   DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_events_timestamp ON real_time_events (timestamp);
CREATE INDEX IF NOT EXISTS idx_events_type ON real_time_events (event_type);
CREATE INDEX IF NOT EXISTS idx_events_user ON real_time_events (user_id);
CREATE INDEX IF NOT EXISTS idx_events_processed ON real_time_events (processed);

CREATE TABLE IF NOT EXISTS dashboard_widgets
(
  id            BIGSERIAL PRIMARY KEY,
  widget_name   VARCHAR(100) NOT NULL,
  widget_type   VARCHAR(50)  NOT NULL, -- 'chart', 'metric', 'heatmap', etc.
  configuration JSONB        NOT NULL,
  user_id       BIGINT,
  position_x    INT       DEFAULT 0,
  position_y    INT       DEFAULT 0,
  width         INT       DEFAULT 1,
  height        INT       DEFAULT 1,
  is_active     BOOLEAN   DEFAULT TRUE,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_widgets_user ON dashboard_widgets (user_id);
CREATE INDEX IF NOT EXISTS idx_widgets_active ON dashboard_widgets (is_active);

-- Machine Learning Tables
CREATE TABLE IF NOT EXISTS ml_models
(
  id                 BIGSERIAL PRIMARY KEY,
  model_name         VARCHAR(100) NOT NULL UNIQUE,
  model_type         VARCHAR(50)  NOT NULL, -- 'recommendation', 'classification', 'prediction'
  model_version      VARCHAR(20)  NOT NULL,
  model_path         VARCHAR(500) NOT NULL,
  model_metadata     JSONB,
  training_data_hash VARCHAR(64),
  accuracy_score     DECIMAL(5, 4),
  is_active          BOOLEAN   DEFAULT TRUE,
  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_models_type ON ml_models (model_type);
CREATE INDEX IF NOT EXISTS idx_models_active ON ml_models (is_active);

CREATE TABLE IF NOT EXISTS ml_training_data
(
  id             BIGSERIAL PRIMARY KEY,
  model_id       BIGINT NOT NULL,
  feature_vector JSONB  NOT NULL,
  target_value   JSONB  NOT NULL,
  data_source    VARCHAR(100), -- 'user_behavior', 'ticket_sales', 'reviews', etc.
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (model_id) REFERENCES ml_models (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_training_model ON ml_training_data (model_id);
CREATE INDEX IF NOT EXISTS idx_training_source ON ml_training_data (data_source);
CREATE INDEX IF NOT EXISTS idx_training_timestamp ON ml_training_data (created_at);

CREATE TABLE IF NOT EXISTS ml_predictions
(
  id                BIGSERIAL PRIMARY KEY,
  model_id          BIGINT NOT NULL,
  input_features    JSONB  NOT NULL,
  prediction_result JSONB  NOT NULL,
  confidence_score  DECIMAL(5, 4),
  user_id           BIGINT,
  prediction_type   VARCHAR(50), -- 'movie_recommendation', 'demand_forecast', etc.
  created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (model_id) REFERENCES ml_models (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_predictions_model ON ml_predictions (model_id);
CREATE INDEX IF NOT EXISTS idx_predictions_user ON ml_predictions (user_id);
CREATE INDEX IF NOT EXISTS idx_predictions_type ON ml_predictions (prediction_type);
CREATE INDEX IF NOT EXISTS idx_predictions_timestamp ON ml_predictions (created_at);

-- Social Features Tables
CREATE TABLE IF NOT EXISTS chat_rooms
(
  id               BIGSERIAL PRIMARY KEY,
  room_name        VARCHAR(100) NOT NULL,
  room_type        VARCHAR(50) DEFAULT 'public', -- 'public', 'private', 'movie_discussion'
  movie_id         BIGINT,
  created_by       BIGINT       NOT NULL,
  max_participants INT         DEFAULT 100,
  is_active        BOOLEAN     DEFAULT TRUE,
  created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_chatrooms_movie ON chat_rooms (movie_id);
CREATE INDEX IF NOT EXISTS idx_chatrooms_creator ON chat_rooms (created_by);
CREATE INDEX IF NOT EXISTS idx_chatrooms_active ON chat_rooms (is_active);

CREATE TABLE IF NOT EXISTS chat_messages
(
  id                  BIGSERIAL PRIMARY KEY,
  room_id             BIGINT NOT NULL,
  user_id             BIGINT NOT NULL,
  message_content     TEXT   NOT NULL,
  message_type        VARCHAR(50) DEFAULT 'text', -- 'text', 'emoji', 'media', 'system'
  reply_to_message_id BIGINT,
  is_edited           BOOLEAN     DEFAULT FALSE,
  created_at          TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES chat_rooms (id) ON DELETE CASCADE,
  FOREIGN KEY (reply_to_message_id) REFERENCES chat_messages (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_messages_room ON chat_messages (room_id);
CREATE INDEX IF NOT EXISTS idx_messages_user ON chat_messages (user_id);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON chat_messages (created_at);

CREATE TABLE IF NOT EXISTS social_connections
(
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT NOT NULL,
  friend_id       BIGINT NOT NULL,
  connection_type VARCHAR(50) DEFAULT 'friend',  -- 'friend', 'following', 'blocked'
  status          VARCHAR(50) DEFAULT 'pending', -- 'pending', 'accepted', 'rejected'
  created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (user_id, friend_id)
);

CREATE INDEX IF NOT EXISTS idx_connections_user ON social_connections (user_id);
CREATE INDEX IF NOT EXISTS idx_connections_friend ON social_connections (friend_id);
CREATE INDEX IF NOT EXISTS idx_connections_status ON social_connections (status);

-- Gamification Tables
CREATE TABLE IF NOT EXISTS achievements
(
  id                      BIGSERIAL PRIMARY KEY,
  achievement_name        VARCHAR(100) NOT NULL UNIQUE,
  achievement_description TEXT,
  achievement_type        VARCHAR(50)  NOT NULL, -- 'movie_count', 'social_activity', 'loyalty', etc.
  criteria                JSONB        NOT NULL, -- Conditions to unlock the achievement
  reward_points           INT       DEFAULT 0,
  badge_icon              VARCHAR(200),
  is_active               BOOLEAN   DEFAULT TRUE,
  created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_achievements_type ON achievements (achievement_type);
CREATE INDEX IF NOT EXISTS idx_achievements_active ON achievements (is_active);

CREATE TABLE IF NOT EXISTS user_achievements
(
  id             BIGSERIAL PRIMARY KEY,
  user_id        BIGINT NOT NULL,
  achievement_id BIGINT NOT NULL,
  unlocked_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  progress_data  JSONB, -- Current progress towards achievement
  FOREIGN KEY (achievement_id) REFERENCES achievements (id) ON DELETE CASCADE,
  UNIQUE (user_id, achievement_id)
);

CREATE INDEX IF NOT EXISTS idx_user_achievements_user ON user_achievements (user_id);
CREATE INDEX IF NOT EXISTS idx_user_achievements_unlocked ON user_achievements (unlocked_at);

CREATE TABLE IF NOT EXISTS loyalty_points
(
  id                 BIGSERIAL PRIMARY KEY,
  user_id            BIGINT      NOT NULL,
  points_earned      INT         NOT NULL,
  points_spent       INT       DEFAULT 0,
  transaction_type   VARCHAR(50) NOT NULL, -- 'earned', 'spent', 'expired'
  transaction_reason VARCHAR(200),
  reference_id       BIGINT,               -- ID of related ticket, review, etc.
  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  expires_at         TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_loyalty_user ON loyalty_points (user_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_type ON loyalty_points (transaction_type);
CREATE INDEX IF NOT EXISTS idx_loyalty_timestamp ON loyalty_points (created_at);

-- Voice and Gesture Recognition Tables
CREATE TABLE IF NOT EXISTS voice_commands
(
  id                 BIGSERIAL PRIMARY KEY,
  command_phrase     VARCHAR(200) NOT NULL,
  command_action     VARCHAR(100) NOT NULL,
  command_parameters JSONB,
  user_id            BIGINT,
  confidence_score   DECIMAL(5, 4),
  execution_status   VARCHAR(50) DEFAULT 'pending', -- 'pending', 'executed', 'failed'
  created_at         TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_voice_user ON voice_commands (user_id);
CREATE INDEX IF NOT EXISTS idx_voice_status ON voice_commands (execution_status);
CREATE INDEX IF NOT EXISTS idx_voice_timestamp ON voice_commands (created_at);

CREATE TABLE IF NOT EXISTS gesture_patterns
(
  id                     BIGSERIAL PRIMARY KEY,
  gesture_name           VARCHAR(100) NOT NULL,
  gesture_data           JSONB        NOT NULL, -- Gesture coordinates and timing
  associated_action      VARCHAR(100) NOT NULL,
  user_id                BIGINT,
  recognition_confidence DECIMAL(5, 4),
  created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_gestures_user ON gesture_patterns (user_id);
CREATE INDEX IF NOT EXISTS idx_gestures_action ON gesture_patterns (associated_action);
CREATE INDEX IF NOT EXISTS idx_gestures_timestamp ON gesture_patterns (created_at);

-- Performance Monitoring Tables
CREATE TABLE IF NOT EXISTS system_metrics
(
  id           BIGSERIAL PRIMARY KEY,
  metric_name  VARCHAR(100)   NOT NULL,
  metric_value DECIMAL(15, 4) NOT NULL,
  metric_unit  VARCHAR(20),  -- 'ms', 'mb', 'percent', 'count'
  component    VARCHAR(100), -- 'database', 'cache', 'ml_engine', 'ui'
  timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_system_metrics_name ON system_metrics (metric_name);
CREATE INDEX IF NOT EXISTS idx_system_metrics_component ON system_metrics (component);
CREATE INDEX IF NOT EXISTS idx_system_metrics_timestamp ON system_metrics (timestamp);

CREATE TABLE IF NOT EXISTS cache_statistics
(
  id              BIGSERIAL PRIMARY KEY,
  cache_name      VARCHAR(100) NOT NULL,
  hit_count       BIGINT    DEFAULT 0,
  miss_count      BIGINT    DEFAULT 0,
  eviction_count  BIGINT    DEFAULT 0,
  load_count      BIGINT    DEFAULT 0,
  total_load_time BIGINT    DEFAULT 0, -- in nanoseconds
  timestamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cache_stats_name ON cache_statistics (cache_name);
CREATE INDEX IF NOT EXISTS idx_cache_stats_timestamp ON cache_statistics (timestamp);
