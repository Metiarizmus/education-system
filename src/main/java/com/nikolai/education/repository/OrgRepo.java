package com.nikolai.education.repository;

import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgRepo extends JpaRepository<Organization, Long> {

    Organization findByUsers(User user);
    Organization findByUsers_id(Long id);
    List<Organization> findByStatus(StatusOrgEnum statusOrgEnum);

}
