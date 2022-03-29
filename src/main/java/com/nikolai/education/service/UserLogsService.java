package com.nikolai.education.service;

import com.nikolai.education.model.Logs;
import com.nikolai.education.model.Organization;
import com.nikolai.education.redis.RedisService;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserLogsRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLogsService {
    private final UserLogsRepo userLogsRepo;
    private final OrgRepo orgRepo;
    private final RedisService redisService;

    public List<? extends Object> findAll(Principal principal) {
        Organization org = orgRepo.findByUsers_email(principal.getName());
        List<Logs> list = userLogsRepo.findAllByUser_Org(org);
        
        String key = "list:logs";
        redisService.lPushAll(key, ArrayUtils.toArray(list, Logs.class));
        List<Object> cachedLogs = redisService.lRange(key, 0, list.size());
        if (cachedLogs.isEmpty()) {
            return list;
        }
        return cachedLogs;
    }
}
