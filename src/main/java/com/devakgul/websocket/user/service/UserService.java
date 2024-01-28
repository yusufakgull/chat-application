package com.devakgul.websocket.user.service;


import com.devakgul.websocket.user.Status;
import com.devakgul.websocket.user.model.User;
import com.devakgul.websocket.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addUserSseEmitter(SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeUserSseEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public void sendNotification(User message) {
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        });
    }
    public void saveUser(User user){
        user.setStatus(Status.ONLINE);
        repository.save(user);
    }

    public void disconnect(User user){
        User storedUser = repository.findById(user.getNickName()).orElse(null);
        if (Objects.nonNull(storedUser)) {
            storedUser.setStatus(Status.OFFLINE);
            repository.save(storedUser);
        }
    }

    public List<User> findConnectedUsers(){
        return repository.findAllByStatus(Status.ONLINE);
    }
}
