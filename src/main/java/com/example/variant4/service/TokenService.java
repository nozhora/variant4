package com.example.variant4.service;

import com.example.variant4.config.JwtProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TokenService {

    RedisTemplate<String, Object> redisTemplate;
    String TOKEN_PREFIX = "refresh:";
    JwtProperties properties;


    public void saveRefreshToken(String token, String email) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, properties.getRefreshExpiration(), TimeUnit.MILLISECONDS);
    }

    public Optional<String> getEmailFromRefreshToken(String token) {
        String key = TOKEN_PREFIX + token;
        Object email = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(email).map(Object::toString);
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }

    public boolean isRefreshTokenValid(String token) {
        return redisTemplate.hasKey(TOKEN_PREFIX + token);
    }
}

