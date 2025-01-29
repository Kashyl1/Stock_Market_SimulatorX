package com.example.backend.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class IndicatorCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(IndicatorCacheService.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(1);

    public IndicatorCacheService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private <T> void saveIndicator(String indicatorName, String symbol, String interval, Integer periods, T value) {
        String key = buildKey(indicatorName, symbol, interval, periods);
        try {
            String serializedValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, serializedValue, CACHE_TTL);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize {}: {}", indicatorName, e.getMessage());
        }
    }

    private <T> T getIndicator(String indicatorName, String symbol, String interval, Integer periods, Class<T> type) {
        String key = buildKey(indicatorName, symbol, interval, periods);
        try {
            String cachedValue = redisTemplate.opsForValue().get(key);
            return cachedValue != null ? objectMapper.readValue(cachedValue, type) : null;
        } catch (Exception e) {
            logger.error("Failed to deserialize {}: {}", indicatorName, e.getMessage());
            return null;
        }
    }

    private String buildKey(String indicatorName, String symbol, String interval, Integer periods) {
        String baseKey = String.format("%s:%s:%s", indicatorName.toUpperCase(), symbol.toUpperCase(), interval);
        return periods != null ? baseKey + ":" + periods : baseKey;
    }
    public void saveSma(String symbol, String interval, int periods, BigDecimal value) {
        saveIndicator("SMA", symbol, interval, periods, value);
    }

    public BigDecimal getSma(String symbol, String interval, int periods) {
        return getIndicator("SMA", symbol, interval, periods, BigDecimal.class);
    }

    public void saveEma(String symbol, String interval, int periods, BigDecimal value) {
        saveIndicator("EMA", symbol, interval, periods, value);
    }

    public BigDecimal getEma(String symbol, String interval, int periods) {
        return getIndicator("EMA", symbol, interval, periods, BigDecimal.class);
    }

    public void saveRsi(String symbol, String interval, int periods, BigDecimal value) {
        saveIndicator("RSI", symbol, interval, periods, value);
    }

    public BigDecimal getRsi(String symbol, String interval, int periods) {
        return getIndicator("RSI", symbol, interval, periods, BigDecimal.class);
    }

    public void saveVolatility(String symbol, String interval, int periods, BigDecimal value) {
        saveIndicator("VOLATILITY", symbol, interval, periods, value);
    }

    public BigDecimal getVolatility(String symbol, String interval, int periods) {
        return getIndicator("VOLATILITY", symbol, interval, periods, BigDecimal.class);
    }

    public void saveMacd(String symbol, String interval, MacdResult value) {
        saveIndicator("MACD", symbol, interval, null, value);
    }

    public MacdResult getMacd(String symbol, String interval) {
        return getIndicator("MACD", symbol, interval, null, MacdResult.class);
    }

    public void saveAdx(String symbol, String interval, BigDecimal value) {
        saveIndicator("ADX", symbol, interval, null, value);
    }

    public BigDecimal getAdx(String symbol, String interval) {
        return getIndicator("ADX", symbol, interval, null, BigDecimal.class);
    }

    public void saveBP(String symbol, String interval, BigDecimal value) {
        saveIndicator("BP", symbol, interval, null, value);
    }

    public BigDecimal getBP(String symbol, String interval) {
        return getIndicator("BP", symbol, interval, null, BigDecimal.class);
    }

    public void saveWilliamsR(String symbol, String interval, BigDecimal value) {
        saveIndicator("WILLIAMS_R", symbol, interval, null, value);
    }

    public BigDecimal getWilliamsR(String symbol, String interval) {
        return getIndicator("WILLIAMS_R", symbol, interval, null, BigDecimal.class);
    }

    public void saveCci(String symbol, String interval, BigDecimal value) {
        saveIndicator("CCI", symbol, interval, null, value);
    }

    public BigDecimal getCci(String symbol, String interval) {
        return getIndicator("CCI", symbol, interval, null, BigDecimal.class);
    }

    public void saveAtr(String symbol, String interval, BigDecimal value) {
        saveIndicator("ATR", symbol, interval, null, value);
    }

    public BigDecimal getAtr(String symbol, String interval) {
        return getIndicator("ATR", symbol, interval, null, BigDecimal.class);
    }
}