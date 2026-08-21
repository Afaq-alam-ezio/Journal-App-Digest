package net.engineeringdigest.journalApp.RedisTest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@Disabled
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void RedisConnectTest(){

        redisTemplate.opsForValue().set("email", "Og@gg.com");
        String ez = redisTemplate.opsForValue().get("ez");
        String pr = redisTemplate.opsForValue().get("pr");
        String email = redisTemplate.opsForValue().get("email");
    }
}
