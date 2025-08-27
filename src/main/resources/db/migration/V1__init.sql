-- Bảng users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Bảng roles (ElementCollection)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_roles FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Bảng phòng chat
CREATE TABLE chat_rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Bảng messages
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    sender_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    CONSTRAINT fk_messages_user FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_room FOREIGN KEY (room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE
);

-- Bảng quan hệ user ↔ room (ManyToMany)
CREATE TABLE user_rooms (
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, room_id),
    CONSTRAINT fk_user_rooms_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_rooms_room FOREIGN KEY (room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE
);
