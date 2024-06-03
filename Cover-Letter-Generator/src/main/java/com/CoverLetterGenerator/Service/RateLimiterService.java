package com.CoverLetterGenerator.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
public class RateLimiterService {

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Value("${rate.limit.requests}")
    private int maxRequests;

    @Value("${rate.limit.duration}")
    private String duration;

    public boolean isAllowed(String key) {
        Integer currentCount = redisTemplate.opsForValue().get(key);

        if (currentCount == null) {
            redisTemplate.opsForValue().set(key, 1, Duration.parse(duration).getSeconds(), TimeUnit.SECONDS);
            return true;
        } else if (currentCount < maxRequests) {
            redisTemplate.opsForValue().increment(key);
            return true;
        } else {
            return false;
        }
    }
}

