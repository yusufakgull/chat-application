package com.devakgul.websocket.chat.controller;

import com.devakgul.websocket.chat.model.ChatMessage;
import com.devakgul.websocket.chat.service.ChatMessageService;
import com.devakgul.websocket.chat.model.ChatNotification;
import com.devakgul.websocket.exceptions.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService service;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage saved = service.save(chatMessage);
        ChatNotification notification = new ChatNotification(saved.getId(),
                saved.getSenderId(),
                saved.getRecipientId(),
                saved.getContent(),
                saved.getTimestamp());
        messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages", notification);
    }

    @MessageMapping("/chat/group")
    public void processGroupMessage(@Payload ChatMessage chatMessage) throws ChatNotFoundException {
        ChatMessage saved = service.saveGeneralChat(chatMessage);
        ChatNotification notification = new ChatNotification(saved.getId(),
                saved.getSenderId(),
                saved.getRecipientId(),
                saved.getContent(),
                saved.getTimestamp());
        messagingTemplate.convertAndSend("/public", notification);
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("senderId") String senderId,
                                                              @PathVariable("recipientId") String recipientId) throws ChatNotFoundException {
        return ResponseEntity.ok(service.findChatMessages(senderId, recipientId));

    }
}
