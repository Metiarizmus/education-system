package com.nikolai.education.service;

import com.nikolai.education.model.User;
import com.nikolai.education.redis.RedisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CacheManagerTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private CacheManagerService<Object> cacheManagerService;

    private List<Object> testList;

    @BeforeEach
    void setUp() {
        testList = new ArrayList<>();
        testList.add(new User("a", "b", "c", "d", "+3456441236"));
        testList.add(new User("a1", "b1", "c1", "d2", "+3416441236"));
    }

    @Test
    void cachedListNotHasKey() {
        when(redisService.hasKey(anyString())).thenReturn(false);
        when(redisService.lPushAll(anyString(), anyList())).thenReturn(anyLong());

        List<Object> resultList = cacheManagerService.cachedList("aaa", testList);
        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(2, resultList.size());
    }

    @Test
    void cachedListHasKey() {
        when(redisService.hasKey(anyString())).thenReturn(true);
        when(redisService.lSize(anyString())).thenReturn(anyLong());
        when(redisService.lRange(anyString(), 0, anyLong())).thenReturn(testList);

        List<Object> resultList = cacheManagerService.cachedList("aaa", testList);
        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(2, resultList.size());
    }

    @Test
    void cachedObjectNotHasKey() {
        when(redisService.hasKey(anyString())).thenReturn(false);
        when(redisService.lPushAll(anyString(), anyList())).thenReturn(anyLong());

        Object result = cacheManagerService.cachedObject("aaa", testList.get(0));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testList.get(0), result);
    }

    @Test
    void cachedObjectHasKey() {
        when(redisService.hasKey(anyString())).thenReturn(true);
        when(redisService.lSize(anyString())).thenReturn(anyLong());
        when(redisService.lRange(anyString(), 0, anyLong())).thenReturn(testList);

        Object result = cacheManagerService.cachedObject("aaa", testList.get(0));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testList.get(0), result);
    }

    @Test
    void getByKey() {
        when(redisService.get(anyString())).thenReturn(testList.get(0));
        Object result = cacheManagerService.getByKey("aaa");

    }

    @Test
    void deleteFromCache() {
    }
}