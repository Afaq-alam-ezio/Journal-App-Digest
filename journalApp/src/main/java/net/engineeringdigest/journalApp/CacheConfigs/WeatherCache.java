package net.engineeringdigest.journalApp.CacheConfigs;

import net.engineeringdigest.journalApp.Config.WeatherConfig;
import net.engineeringdigest.journalApp.Repositories.WeatherCacheRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeatherCache {

    public Map<String, String> weatherCacheData;

    @Autowired
    WeatherCacheRepo weatherCacheRepo;

    @PostConstruct
    public void init(){

        weatherCacheData = new HashMap<>();

        List<WeatherConfig> dbData = weatherCacheRepo.findAll();

        for (WeatherConfig data : dbData) {

            weatherCacheData.put(data.getKey(), data.getValue());
        }
    }
}
