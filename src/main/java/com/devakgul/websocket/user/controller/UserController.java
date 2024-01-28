package com.devakgul.websocket.user.controller;

import com.devakgul.websocket.user.service.UserService;
import com.devakgul.websocket.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService service;


    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(-1L);
        service.addUserSseEmitter(emitter);
        emitter.onCompletion(() -> service.removeUserSseEmitter(emitter));
        emitter.onTimeout(() -> service.removeUserSseEmitter(emitter));

        return emitter;
    }

    @MessageMapping("/user.addUser")
    public User addUser(@Payload User user, SimpMessageHeaderAccessor headerAccessor){
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", user.getNickName());
        service.saveUser(user);
        service.sendNotification(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    public User disconnect(@Payload User user){
        service.disconnect(user);
        service.sendNotification(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers(){
        return ResponseEntity.ok(service.findConnectedUsers());
    }

}
