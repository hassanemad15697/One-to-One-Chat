package com.haskar.onetoonechat.respository;

import com.haskar.onetoonechat.model.enums.Status;
import com.haskar.onetoonechat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByNickName(String username);
    boolean existsByEmail(String email);
    Optional<User> findByNickName(String nickname);

    List<User> findAllByNickNameContainingIgnoreCase(String nickname);
}
