package net.engineeringdigest.journalApp.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisCachingService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper mapper;

    public <T> T getDataFromRedisCache(String cityName, Class<T> entityClass){

        try{

            Object weatherData = redisTemplate.opsForValue().get(cityName);

            return mapper.readValue(weatherData.toString(), entityClass);

        } catch (Exception e) {

            log.error("Error : " + e);
        }

        return null;
    }

    public void setDataInRedisCache(String cityName, Object weatherData, Long TTL){

        try{

            String json = mapper.writeValueAsString(weatherData);
            redisTemplate.opsForValue().set(cityName, json, TTL, TimeUnit.MINUTES);
        }
        catch (Exception e){

            log.error("Error : " + e);
        }
    }
}
