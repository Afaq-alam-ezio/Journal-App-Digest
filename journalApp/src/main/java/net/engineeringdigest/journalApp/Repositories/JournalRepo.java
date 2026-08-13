package net.engineeringdigest.journalApp.Repositories;

import net.engineeringdigest.journalApp.Entities.Journal;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalRepo extends MongoRepository<Journal, ObjectId> {
}
