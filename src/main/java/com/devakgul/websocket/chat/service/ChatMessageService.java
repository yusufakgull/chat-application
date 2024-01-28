package com.devakgul.websocket.chat.service;

import com.devakgul.websocket.chat.Constants;
import com.devakgul.websocket.chat.model.ChatMessage;
import com.devakgul.websocket.chat.repository.ChatMessageRepository;
import com.devakgul.websocket.chatroom.Type;
import com.devakgul.websocket.chatroom.model.ChatRoom;
import com.devakgul.websocket.chatroom.service.ChatRoomService;
import com.devakgul.websocket.exceptions.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        String chatId = chatRoomService.getOrCreatePrivateChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId());
        chatMessage.setChatId(chatId);
        repository.save(chatMessage);
        return chatMessage;
    }

    public ChatMessage saveWithChatId(ChatMessage chatMessage) {
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) throws ChatNotFoundException {
        if (recipientId.equals(Constants.GENERAL_CHAT)) {
            ChatRoom chatRoom = chatRoomService.getChatRoom(Constants.GENERAL_CHAT);
            return repository.findByChatId(chatRoom.getChatId());
        }
        Optional<ChatRoom> chatRoomOptional = chatRoomService.findChatRoomBySenderIdAndRecipientId(senderId, recipientId);
        return chatRoomOptional.map(r -> repository.findByChatId(r.getChatId())).orElse(Collections.emptyList());
    }

    public ChatMessage saveGeneralChat(ChatMessage chatMessage) throws ChatNotFoundException {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatMessage.getChatId());
        if (chatRoom.getType().equals(Type.GROUP) && !chatRoom.getParticipants().contains(chatMessage.getSenderId())) {
            chatRoom.getParticipants().add(chatMessage.getSenderId());
            chatRoomService.update(chatRoom);
        }
        repository.save(chatMessage);
        return chatMessage;
    }
}
