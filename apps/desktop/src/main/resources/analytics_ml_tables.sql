-- Analytics and ML Tables for RAKCHA Enhancement Suite
-- These tables support real-time analytics, machine learning, and advanced features

-- Analytics Tables
CREATE TABLE IF NOT EXISTS analytics_metrics
(
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  metric_name  VARCHAR(100)   NOT NULL,
  metric_value DECIMAL(15, 4) NOT NULL,
  metric_type  VARCHAR(50)    NOT NULL, -- 'revenue', 'attendance', 'performance', etc.
  cinema_id    BIGINT,
  film_id      BIGINT,
  timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  metadata     JSON,
  INDEX        idx_analytics_timestamp(timestamp),
  INDEX        idx_analytics_type(metric_type),
  INDEX        idx_analytics_cinema(cinema_id),
  INDEX        idx_analytics_film(film_id)
);

CREATE TABLE IF NOT EXISTS real_time_events
(
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_type VARCHAR(100) NOT NULL,
  event_data JSON         NOT NULL,
  user_id    BIGINT,
  session_id VARCHAR(100),
  timestamp  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  processed  BOOLEAN   DEFAULT FALSE,
  INDEX      idx_events_timestamp(timestamp),
  INDEX      idx_events_type(event_type),
  INDEX      idx_events_user(user_id),
  INDEX      idx_events_processed(processed)
);

CREATE TABLE IF NOT EXISTS dashboard_widgets
(
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  widget_name   VARCHAR(100) NOT NULL,
  widget_type   VARCHAR(50)  NOT NULL, -- 'chart', 'metric', 'heatmap', etc.
  configuration JSON         NOT NULL,
  user_id       BIGINT,
  position_x    INT       DEFAULT 0,
  position_y    INT       DEFAULT 0,
  width         INT       DEFAULT 1,
  height        INT       DEFAULT 1,
  is_active     BOOLEAN   DEFAULT TRUE,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX         idx_widgets_user(user_id),
  INDEX         idx_widgets_active(is_active)
);

-- Machine Learning Tables
CREATE TABLE IF NOT EXISTS ml_models
(
  id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
  model_name         VARCHAR(100) NOT NULL UNIQUE,
  model_type         VARCHAR(50)  NOT NULL, -- 'recommendation', 'classification', 'prediction'
  model_version      VARCHAR(20)  NOT NULL,
  model_path         VARCHAR(500) NOT NULL,
  model_metadata     JSON,
  training_data_hash VARCHAR(64),
  accuracy_score     DECIMAL(5, 4),
  is_active          BOOLEAN   DEFAULT TRUE,
  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX              idx_models_type(model_type),
  INDEX              idx_models_active(is_active)
);

CREATE TABLE IF NOT EXISTS ml_training_data
(
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  model_id       BIGINT NOT NULL,
  feature_vector JSON   NOT NULL,
  target_value   JSON   NOT NULL,
  data_source    VARCHAR(100), -- 'user_behavior', 'ticket_sales', 'reviews', etc.
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (model_id) REFERENCES ml_models (id) ON DELETE CASCADE,
  INDEX          idx_training_model(model_id),
  INDEX          idx_training_source(data_source),
  INDEX          idx_training_timestamp(created_at)
);

CREATE TABLE IF NOT EXISTS ml_predictions
(
  id                BIGINT PRIMARY KEY AUTO_INCREMENT,
  model_id          BIGINT NOT NULL,
  input_features    JSON   NOT NULL,
  prediction_result JSON   NOT NULL,
  confidence_score  DECIMAL(5, 4),
  user_id           BIGINT,
  prediction_type   VARCHAR(50), -- 'movie_recommendation', 'demand_forecast', etc.
  created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (model_id) REFERENCES ml_models (id) ON DELETE CASCADE,
  INDEX             idx_predictions_model(model_id),
  INDEX             idx_predictions_user(user_id),
  INDEX             idx_predictions_type(prediction_type),
  INDEX             idx_predictions_timestamp(created_at)
);

-- Social Features Tables
CREATE TABLE IF NOT EXISTS chat_rooms
(
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_name        VARCHAR(100) NOT NULL,
  room_type        VARCHAR(50) DEFAULT 'public', -- 'public', 'private', 'movie_discussion'
  movie_id         BIGINT,
  created_by       BIGINT       NOT NULL,
  max_participants INT         DEFAULT 100,
  is_active        BOOLEAN     DEFAULT TRUE,
  created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX            idx_chatrooms_movie(movie_id),
  INDEX            idx_chatrooms_creator(created_by),
  INDEX            idx_chatrooms_active(is_active)
);

CREATE TABLE IF NOT EXISTS chat_messages
(
  id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id             BIGINT NOT NULL,
  user_id             BIGINT NOT NULL,
  message_content     TEXT   NOT NULL,
  message_type        VARCHAR(50) DEFAULT 'text', -- 'text', 'emoji', 'media', 'system'
  reply_to_message_id BIGINT,
  is_edited           BOOLEAN     DEFAULT FALSE,
  created_at          TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES chat_rooms (id) ON DELETE CASCADE,
  FOREIGN KEY (reply_to_message_id) REFERENCES chat_messages (id) ON DELETE SET NULL,
  INDEX               idx_messages_room(room_id),
  INDEX               idx_messages_user(user_id),
  INDEX               idx_messages_timestamp(created_at)
);

CREATE TABLE IF NOT EXISTS social_connections
(
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id         BIGINT NOT NULL,
  friend_id       BIGINT NOT NULL,
  connection_type VARCHAR(50) DEFAULT 'friend',  -- 'friend', 'following', 'blocked'
  status          VARCHAR(50) DEFAULT 'pending', -- 'pending', 'accepted', 'rejected'
  created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY unique_connection (user_id, friend_id),
  INDEX           idx_connections_user(user_id),
  INDEX           idx_connections_friend(friend_id),
  INDEX           idx_connections_status(status)
);

-- Gamification Tables
CREATE TABLE IF NOT EXISTS achievements
(
  id                      BIGINT PRIMARY KEY AUTO_INCREMENT,
  achievement_name        VARCHAR(100) NOT NULL UNIQUE,
  achievement_description TEXT,
  achievement_type        VARCHAR(50)  NOT NULL, -- 'movie_count', 'social_activity', 'loyalty', etc.
  criteria                JSON         NOT NULL, -- Conditions to unlock the achievement
  reward_points           INT       DEFAULT 0,
  badge_icon              VARCHAR(200),
  is_active               BOOLEAN   DEFAULT TRUE,
  created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX                   idx_achievements_type(achievement_type),
  INDEX                   idx_achievements_active(is_active)
);

CREATE TABLE IF NOT EXISTS user_achievements
(
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id        BIGINT NOT NULL,
  achievement_id BIGINT NOT NULL,
  unlocked_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  progress_data  JSON, -- Current progress towards achievement
  FOREIGN KEY (achievement_id) REFERENCES achievements (id) ON DELETE CASCADE,
  UNIQUE KEY unique_user_achievement (user_id, achievement_id),
  INDEX          idx_user_achievements_user(user_id),
  INDEX          idx_user_achievements_unlocked(unlocked_at)
);

CREATE TABLE IF NOT EXISTS loyalty_points
(
  id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id            BIGINT      NOT NULL,
  points_earned      INT         NOT NULL,
  points_spent       INT       DEFAULT 0,
  transaction_type   VARCHAR(50) NOT NULL, -- 'earned', 'spent', 'expired'
  transaction_reason VARCHAR(200),
  reference_id       BIGINT,               -- ID of related ticket, review, etc.
  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  expires_at         TIMESTAMP,
  INDEX              idx_loyalty_user(user_id),
  INDEX              idx_loyalty_type(transaction_type),
  INDEX              idx_loyalty_timestamp(created_at)
);

-- Voice and Gesture Recognition Tables
CREATE TABLE IF NOT EXISTS voice_commands
(
  id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
  command_phrase     VARCHAR(200) NOT NULL,
  command_action     VARCHAR(100) NOT NULL,
  command_parameters JSON,
  user_id            BIGINT,
  confidence_score   DECIMAL(5, 4),
  execution_status   VARCHAR(50) DEFAULT 'pending', -- 'pending', 'executed', 'failed'
  created_at         TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  INDEX              idx_voice_user(user_id),
  INDEX              idx_voice_status(execution_status),
  INDEX              idx_voice_timestamp(created_at)
);

CREATE TABLE IF NOT EXISTS gesture_patterns
(
  id                     BIGINT PRIMARY KEY AUTO_INCREMENT,
  gesture_name           VARCHAR(100) NOT NULL,
  gesture_data           JSON         NOT NULL, -- Gesture coordinates and timing
  associated_action      VARCHAR(100) NOT NULL,
  user_id                BIGINT,
  recognition_confidence DECIMAL(5, 4),
  created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX                  idx_gestures_user(user_id),
  INDEX                  idx_gestures_action(associated_action),
  INDEX                  idx_gestures_timestamp(created_at)
);

-- Performance Monitoring Tables
CREATE TABLE IF NOT EXISTS system_metrics
(
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  metric_name  VARCHAR(100)   NOT NULL,
  metric_value DECIMAL(15, 4) NOT NULL,
  metric_unit  VARCHAR(20),  -- 'ms', 'mb', 'percent', 'count'
  component    VARCHAR(100), -- 'database', 'cache', 'ml_engine', 'ui'
  timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX        idx_system_metrics_name(metric_name),
  INDEX        idx_system_metrics_component(component),
  INDEX        idx_system_metrics_timestamp(timestamp)
);

CREATE TABLE IF NOT EXISTS cache_statistics
(
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  cache_name      VARCHAR(100) NOT NULL,
  hit_count       BIGINT    DEFAULT 0,
  miss_count      BIGINT    DEFAULT 0,
  eviction_count  BIGINT    DEFAULT 0,
  load_count      BIGINT    DEFAULT 0,
  total_load_time BIGINT    DEFAULT 0, -- in nanoseconds
  timestamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX           idx_cache_stats_name(cache_name),
  INDEX           idx_cache_stats_timestamp(timestamp)
);
