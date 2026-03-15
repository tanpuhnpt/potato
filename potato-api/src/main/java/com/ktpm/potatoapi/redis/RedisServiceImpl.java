package com.ktpm.potatoapi.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisServiceImpl implements RedisService {

    RedisTemplate<String, Object> redisTemplate;
    ObjectMapper redisObjectMapper;

    @Override
    public <T> T get(String key, Class<T> targetClass) {
        try {
            String json = (String) redisTemplate.opsForValue().get(key);
            if (StringUtils.hasText(json)) {
                return redisObjectMapper.readValue(json, targetClass);
            }
        } catch (Exception e) {
            log.error("Lỗi đọc Redis cache key {}: {}", key, e.getMessage());
        }
        return null;
    }

    @Override
    public <T> List<T> getAll(String key, Class<T> targetClass) {
        try {
            String json = (String) redisTemplate.opsForValue().get(key);

            if (StringUtils.hasText(json)) {
                CollectionType listType = redisObjectMapper
                        .getTypeFactory().constructCollectionType(List.class, targetClass);

                return redisObjectMapper.readValue(json, listType);
            }
        } catch (Exception e) {
            log.error("Lỗi đọc list từ Redis với key {}: {}", key, e.getMessage());
        }
        return null;
    }

    @Override
    public void save(String key, Object value) {
        try {
            String json = redisObjectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            log.error("Lỗi lưu list vào Redis với key {}: {}", key, e.getMessage());
        }
    }

    @Override
    public <T> void saveAll(String key, List<T> list) {
        try {
            String json = redisObjectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            log.error("Lỗi lưu list vào Redis với key {}: {}", key, e.getMessage());
        }
    }

    @Override
    public void clearByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (!keys.isEmpty()) redisTemplate.delete(keys);
    }
}
