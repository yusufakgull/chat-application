package com.devakgul.websocket.chatroom.model;


import com.devakgul.websocket.chatroom.Type;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatRoom {

    @Id
    private String id;
    private String chatId;
    private Set<String> participants;
    private Type type;
}
