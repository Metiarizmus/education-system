package com.nikolai.education.service;

import com.nikolai.education.model.Logs;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserLogsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLogsService {
    private final UserLogsRepo userLogsRepo;
    private final OrgRepo orgRepo;
    private final CacheManager<Logs> cacheManager;

    public List<?> findAll(User user) {
        String key = "list:logs";

        Organization org = orgRepo.findByUsers(user);
        List<Logs> list = userLogsRepo.findAllByUser_Org(org);

        return cacheManager.cachedList(key, list);
    }
}