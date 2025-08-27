package com.myweb.chat.repo;

import com.myweb.chat.model.ChatRoom;
import com.myweb.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findTop50ByRoomOrderByCreatedAtDesc(ChatRoom room);
}
