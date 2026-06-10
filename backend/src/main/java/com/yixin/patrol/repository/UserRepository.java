package com.yixin.patrol.repository;

import com.yixin.patrol.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByOrgId(Long orgId);

    List<User> findByRoleType(Integer roleType);

    List<User> findByOrgIdAndRoleType(Long orgId, Integer roleType);

    @Query("SELECT u FROM User u WHERE u.status = 1 AND u.orgId IN :orgIds")
    List<User> findActiveUsersByOrgIds(@Param("orgIds") List<Long> orgIds);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    Long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.roleType = :roleType AND u.status = 1")
    Long countByRoleType(@Param("roleType") Integer roleType);
}
