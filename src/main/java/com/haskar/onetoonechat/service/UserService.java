package com.haskar.onetoonechat.service;

import com.haskar.onetoonechat.model.User;
import com.haskar.onetoonechat.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.haskar.onetoonechat.model.Status.OFFLINE;
import static com.haskar.onetoonechat.model.Status.ONLINE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User saveUser(User user){
        user.setStatus(ONLINE);
        return userRepository.save(user);
    }

    public void disconnect(String nickname){
        Optional<User> foundUser = userRepository.findById(nickname);
        foundUser.ifPresent(usr -> {
            usr.setStatus(OFFLINE);
            userRepository.save(usr);
        });
    }

    public List<User> findConnectedUsers(){
        return userRepository.findAllByStatus(ONLINE).orElse(List.of());
    }
}
