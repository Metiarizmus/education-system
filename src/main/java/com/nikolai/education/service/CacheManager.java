package com.nikolai.education.service;

import com.nikolai.education.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheManager<E> {

    private final RedisService redisService;

    public List<E> cachedList(String key, List<E> list) {

        if (redisService.hasKey(key) == false) {
            redisService.lPushAll(key, ArrayUtils.toArray(list));
            return list;
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

    public Optional<E> getByKey(String key) {
        return (Optional<E>) redisService.get(key);
    }


    public void deleteFromCache(String key) {
        redisService.del(key);
    }

}
