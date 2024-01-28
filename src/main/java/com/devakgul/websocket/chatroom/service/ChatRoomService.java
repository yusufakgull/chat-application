package com.devakgul.websocket.chatroom.service;

import com.devakgul.websocket.chat.Constants;
import com.devakgul.websocket.chatroom.model.ChatRoom;
import com.devakgul.websocket.chatroom.repository.ChatRoomRepository;
import com.devakgul.websocket.exceptions.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.devakgul.websocket.chatroom.Type.GROUP;
import static com.devakgul.websocket.chatroom.Type.PRIVATE;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository repository;

    public String getOrCreatePrivateChatRoomId(String participant1, String participant2) {
        String chatId = generateChatId(participant1, participant2);
        ChatRoom chatRoom = repository.findByChatId(chatId).orElseGet(() -> createAndSaveChatRoom(chatId, participant1, participant2));
        return chatRoom.getChatId();
    }


    public Optional<ChatRoom> findChatRoomBySenderIdAndRecipientId(String participant1, String participant2) {
        String chatRoomId = generateChatId(participant1, participant2);
        return repository.findByChatId(chatRoomId);
    }


    public ChatRoom getChatRoom(String chatRoomId) throws ChatNotFoundException {
        return repository.findByChatId(chatRoomId).orElseThrow(() -> new ChatNotFoundException("Chat " + chatRoomId + "not found."));
    }

    public ChatRoom update(ChatRoom chatRoom) {
        return repository.save(chatRoom);
    }

    private ChatRoom createAndSaveChatRoom(String chatId, String participant1, String participant2) {
        ChatRoom chatRoom = ChatRoom.builder().chatId(chatId).participants(Set.of(participant1, participant2)).type(PRIVATE).build();
        return repository.save(chatRoom);
    }

    private String generateChatId(String participant1, String participant2) {
        return Stream.of(participant1, participant2).sorted().reduce("", (concatenated, str) -> concatenated + str);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    private void createGeneralChatRoomIfNotExists() {
        repository.findByChatId(Constants.GENERAL_CHAT).ifPresentOrElse(r -> {
        }, () -> {
            ChatRoom chatRoom = ChatRoom.builder().chatId(Constants.GENERAL_CHAT).participants(new HashSet<>()).type(GROUP).build();
            repository.save(chatRoom);
        });
    }
}
