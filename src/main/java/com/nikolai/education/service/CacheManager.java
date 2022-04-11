package com.nikolai.education.service;

import com.nikolai.education.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheManager<E> {

    private final RedisService redisService;

    public List<E> cachedList(String key, List<E> dtos) {

        if (redisService.hasKey(key) == false) {
            redisService.lPushAll(key, ArrayUtils.toArray(dtos));
            return dtos;
        }
        List<E> cachedLogs = (List<E>) redisService.lRange(key, 0, redisService.lSize(key));
        return cachedLogs;
    }

    public E cachedObject(String key, E obj) {
        if (redisService.hasKey(key) == false) {
            redisService.lPush(key, obj);
            return obj;
        }
        E cachedObject = (E) redisService.lRange(key, 0, redisService.lSize(key));
        return cachedObject;
    }

    public E getByKey(String key) {
        return (E) redisService.get(key);
    }

    public void deleteFromCache(String key) {
        redisService.del(key);
    }

}
