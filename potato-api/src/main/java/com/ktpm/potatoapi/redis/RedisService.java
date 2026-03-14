package com.ktpm.potatoapi.redis;

import java.util.List;

public interface RedisService {
    <T> T get(String key, Class<T> targetClass);
    <T> List<T> getAll(String key, Class<T> targetClass);
    void save(String key, Object value);
    <T> void saveAll(String key, List<T> list);
    void clearByPrefix(String prefix);
}
