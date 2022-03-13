package com.nikolai.education.repository;

import com.nikolai.education.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepo extends JpaRepository<Organization, Long> {

    Organization findByCreatorId(Long id);
}
