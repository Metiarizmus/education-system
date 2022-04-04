package com.nikolai.education.service;

import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheManager<E> {

    private final RedisService redisService;

    public List<E> cached(String key, List<E> dtos) {

        if (redisService.hasKey(key) == false) {
            redisService.lPushAll(key, ArrayUtils.toArray(dtos));
            System.out.println("no cache");
            return dtos;
        }
        System.out.println("yes cache");
        List<E> cachedLogs = (List<E>) redisService.lRange(key, 0, redisService.lSize(key));
        return cachedLogs;
    }

}
