package com.haskar.onetoonechat.respository;

import com.haskar.onetoonechat.model.Status;
import com.haskar.onetoonechat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<List<User> > findAllByStatus(Status status);
}
