package com.haskar.onetoonechat.service;

import com.haskar.onetoonechat.exception.ResourceNotFoundException;
import com.haskar.onetoonechat.model.User;
import com.haskar.onetoonechat.model.enums.Status;
import com.haskar.onetoonechat.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.haskar.onetoonechat.model.enums.Status.OFFLINE;
import static com.haskar.onetoonechat.model.enums.Status.ONLINE;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserService {

    private final UserRepository userRepository;

    public User saveUser(User user){
        if(Objects.isNull(user) || Objects.isNull(user.getEmail()) || Objects.isNull(user.getNickName()) ){
            log.error("user object: {}", user.toString());
            throw new IllegalArgumentException("user cannot be null or email or nickname");
        }
        if (userRepository.existsByNickName(user.getNickName())) {
            throw new DuplicateKeyException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateKeyException("Email already exists");
        }
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setStatus(OFFLINE);
        return userRepository.save(user);
    }
    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickName(nickname).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

    }

    public Status getUserStatus(String userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        return user.getStatus();
    }


    public List<User> getUsersByNickname(String nickname) {
        return userRepository.findAllByNickNameContainingIgnoreCase(nickname);
    }
}
