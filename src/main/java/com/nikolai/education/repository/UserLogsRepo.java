package com.nikolai.education.repository;

import com.nikolai.education.model.Logs;
import com.nikolai.education.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLogsRepo extends JpaRepository<Logs, Long> {
    List<Logs> findAllByUser_Org(Organization organization);
}
