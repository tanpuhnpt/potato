package com.ktpm.potatoapi.redis;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class RedisListener {
    @Autowired
    @Lazy
    private RedisService redisService;

    @PostPersist
    @PostUpdate
    @PostRemove
    public void clearCache(Object entity) {
        String entityName = entity.getClass().getSimpleName().toLowerCase();
        String prefix = entityName + ":";
        redisService.clearByPrefix(prefix);
        log.info("clear cache for entity: {}", entityName);
    }
}
