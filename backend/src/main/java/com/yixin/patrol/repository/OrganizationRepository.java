package com.yixin.patrol.repository;

import com.yixin.patrol.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByCode(String code);

    boolean existsByCode(String code);

    List<Organization> findByParentId(Long parentId);

    List<Organization> findByOrgType(Integer orgType);

    List<Organization> findByParentIdAndStatus(Long parentId, Integer status);

    @Query("SELECT o FROM Organization o WHERE o.status = 1 ORDER BY o.sortOrder, o.id")
    List<Organization> findAllActive();

    @Query("SELECT o FROM Organization o WHERE o.orgType = 2 AND o.status = 1")
    List<Organization> findAllSchools();

    @Query("SELECT COUNT(o) FROM Organization o WHERE o.orgType = :orgType AND o.status = 1")
    Long countByOrgType(@Param("orgType") Integer orgType);
}
