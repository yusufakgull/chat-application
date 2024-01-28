package com.devakgul.websocket.chatroom.repository;

import com.devakgul.websocket.chatroom.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom,String> {
    Optional<ChatRoom> findByChatId(String chatId);
}
