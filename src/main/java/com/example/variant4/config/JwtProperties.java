package com.example.variant4.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtProperties {
    @Value("${app.jwt.key}")
    private String key;
    @Value("${app.jwt.access-expiration}")
    private long accessExpiration;
    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;
}
