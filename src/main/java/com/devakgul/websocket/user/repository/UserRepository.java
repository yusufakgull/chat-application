package com.devakgul.websocket.user.repository;


import com.devakgul.websocket.user.Status;
import com.devakgul.websocket.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User,String> {
    List<User> findAllByStatus(Status status);
}
