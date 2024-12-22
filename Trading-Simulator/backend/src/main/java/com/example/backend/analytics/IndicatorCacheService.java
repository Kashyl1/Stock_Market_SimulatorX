package com.example.backend.analytics;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class IndicatorCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    public IndicatorCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveSma(String symbol, String interval, int periods, BigDecimal value) {
        String key = "SMA:" + symbol + ":" + interval + ":" + periods;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(2));
    }

    public BigDecimal getSma(String symbol, String interval, int periods) {
        String key = "SMA:" + symbol + ":" + interval + ":" + periods;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
            return null;
        }
    }

    public void saveEma(String symbol, String interval, int periods, BigDecimal value) {
        String key = "Ema:" + symbol + ":" + interval + ":" + periods;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(2));
    }

    public BigDecimal getEma(String symbol, String interval, int periods) {
        String key = "Ema:" + symbol + ":" + interval + ":" + periods;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
            return null;
        }
    }

    public void saveRsi(String symbol, String interval, int periods, BigDecimal value) {
        String key = "Rsi:" + symbol + ":" + interval + ":" + periods;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(2));
    }

    public BigDecimal getRsi(String symbol, String interval, int periods) {
        String key = "Rsi:" + symbol + ":" + interval + ":" + periods;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
            return null;
        }
    }

    public void saveVolatility(String symbol, String interval, int periods, BigDecimal value) {
        String key = "Volatility:" + symbol + ":" + interval + ":" + periods;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(2));
    }

    public BigDecimal getVolatility(String symbol, String interval, int periods) {
        String key = "Volatility:" + symbol + ":" + interval + ":" + periods;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
            return null;
        }
    }
}
