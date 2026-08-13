package net.engineeringdigest.journalApp.Repositories;

import net.engineeringdigest.journalApp.Entities.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepo extends MongoRepository<Users, Object> {

    boolean existsByUserName(String userName);
    Users findByUserName(String userName);
}
