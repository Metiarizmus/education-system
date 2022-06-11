package com.nikolai.education.repository;

import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgRepo extends JpaRepository<Organization, Long> {

    Organization findByUsers(User user);
    Organization findByUsers_id(Long id);
    List<Organization> findByStatus(StatusOrgEnum statusOrgEnum);
    List<Organization> findByCreatorId(Long id);

    @Query("SELECT o from Organization o where o.name like %?2% and o.status=?1")
    List<Organization> findByStatusAndNameLike(StatusOrgEnum statusOrgEnum, String name);

    @Query(nativeQuery = true, value = "select * from organizations \n" +
            "left join users_org on organizations.id=users_org.org_id\n" +
            "left join users on users.id = users_org.users_id \n" +
            "left join users_roles on users_roles.users_id = users.id\n" +
            "left join roles on roles.id = users_roles.roles_id \n" +
            "where roles.role=:roleName and users.email = :email")
    List<Organization> findByUserEmailAndRole(@Param("roleName") String roleName, @Param("email") String email);
}
