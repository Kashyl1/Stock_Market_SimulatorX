package com.example.backend.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Service
public class IndicatorCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(IndicatorCacheService.class);

    public IndicatorCacheService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveSma(String symbol, String interval, int periods, BigDecimal value) {
        String key = "SMA:" + symbol + ":" + interval + ":" + periods;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getSma(String symbol, String interval, int periods) {
        String key = "SMA:" + symbol + ":" + interval + ":" + periods;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error load SMA: {}", e.getMessage());
            return null;
        }
    }

    public void saveEma(String symbol, String interval, int periods, BigDecimal value) {
        String key = "Ema:" + symbol + ":" + interval + ":" + periods;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getEma(String symbol, String interval, int periods) {
        String key = "Ema:" + symbol + ":" + interval + ":" + periods;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error load EMA: {}", e.getMessage());
            return null;
        }
    }

    public void saveRsi(String symbol, String interval, int periods, BigDecimal value) {
        String key = "Rsi:" + symbol + ":" + interval + ":" + periods;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getRsi(String symbol, String interval, int periods) {
        String key = "Rsi:" + symbol + ":" + interval + ":" + periods;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error load RSI: {}", e.getMessage());
            return null;
        }
    }

    public void saveVolatility(String symbol, String interval, int periods, BigDecimal value) {
        String key = "Volatility:" + symbol + ":" + interval + ":" + periods;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getVolatility(String symbol, String interval, int periods) {
        String key = "Volatility:" + symbol + ":" + interval + ":" + periods;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error load Volatility: {}", e.getMessage());
            return null;
        }
    }

    public void saveMacd(String symbol, String interval, MacdResult value) {
        String key = "Macd:" + symbol + ":" + interval;
        try {
            MacdResult roundedResult = value.format(8);
            String json = objectMapper.writeValueAsString(roundedResult);
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(1));
        } catch (JsonProcessingException e) {
            logger.error("Redis error saving MACD: {}", e.getMessage());
        }
    }

    public MacdResult getMacd(String symbol, String interval) {
        String key = "Macd:" + symbol + ":" + interval;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? objectMapper.readValue(val, MacdResult.class) : null;
        } catch (Exception e) {
            logger.error("Redis error loading MACD: {}", e.getMessage());
            return null;
        }
    }

    public void saveAdx(String symbol, String interval, BigDecimal value) {
        String key = "ADX:" + symbol + ":" + interval;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getAdx(String symbol, String interval) {
        String key = "ADX:" + symbol + ":" + interval;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error loading ADX: {}", e.getMessage());
            return null;
        }
    }

    public void saveBP(String symbol, String interval, BigDecimal value) {
        String key = "BP:" + symbol + ":" + interval;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getBP(String symbol, String interval) {
        String key = "BP:" + symbol + ":" + interval;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error loading BP: {}", e.getMessage());
            return null;
        }
    }

    public void saveWilliamsR(String symbol, String interval, BigDecimal value) {
        String key = "WilliamsR:" + symbol + ":" + interval;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getWilliamsR(String symbol, String interval) {
        String key = "WilliamsR:" + symbol + ":" + interval;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error loading WilliamsR: {}", e.getMessage());
            return null;
        }
    }

    public void saveCci(String symbol, String interval, BigDecimal value) {
    String key = "Cci:" + symbol + ":" + interval;
    redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getCci(String symbol, String interval) {
        String key = "Cci:" + symbol + ":" + interval;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error loading Cci: {}", e.getMessage());
            return null;
        }
    }

    public void saveAtr(String symbol, String interval, BigDecimal value) {
        String key = "Atr:" + symbol + ":" + interval;
        redisTemplate.opsForValue().set(key, value.toPlainString(), Duration.ofMinutes(1));
    }

    public BigDecimal getAtr(String symbol, String interval) {
        String key = "Atr:" + symbol + ":" + interval;
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? new BigDecimal(val) : null;
        } catch (Exception e) {
            logger.error("Redis error loading Atr: {}", e.getMessage());
            return null;
        }
    }
}
