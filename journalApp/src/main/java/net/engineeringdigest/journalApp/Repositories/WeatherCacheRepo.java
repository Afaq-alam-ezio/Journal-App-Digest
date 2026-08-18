package net.engineeringdigest.journalApp.Repositories;

import net.engineeringdigest.journalApp.Config.WeatherConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WeatherCacheRepo extends MongoRepository<WeatherConfig, String> {
}
