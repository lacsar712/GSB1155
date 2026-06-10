package com.yixin.patrol.repository;

import com.yixin.patrol.entity.PatrolTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatrolTaskRepository extends JpaRepository<PatrolTask, Long> {

    List<PatrolTask> findByOrgId(Long orgId);

    List<PatrolTask> findByExecutorId(Long executorId);

    List<PatrolTask> findByAssignerId(Long assignerId);

    List<PatrolTask> findByStatus(Integer status);

    Page<PatrolTask> findByOrgId(Long orgId, Pageable pageable);

    Page<PatrolTask> findByExecutorId(Long executorId, Pageable pageable);

    @Query("SELECT t FROM PatrolTask t WHERE t.executorId = :executorId AND t.status IN :statuses")
    List<PatrolTask> findByExecutorIdAndStatuses(@Param("executorId") Long executorId, 
                                                  @Param("statuses") List<Integer> statuses);

    @Query("SELECT t FROM PatrolTask t WHERE t.orgId = :orgId AND t.status = :status")
    List<PatrolTask> findByOrgIdAndStatus(@Param("orgId") Long orgId, @Param("status") Integer status);

    @Query("SELECT t FROM PatrolTask t WHERE t.deadline < :now AND t.status IN (0, 1)")
    List<PatrolTask> findOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM PatrolTask t WHERE t.deadline < :now AND t.status IN (0, 1)")
    Long countOverdueTasks(@Param("now") LocalDateTime now);

    Long countByOrgId(Long orgId);

    Long countByExecutorId(Long executorId);

    @Query("SELECT t.status, COUNT(t) FROM PatrolTask t WHERE t.status IN (0, 1, 3) GROUP BY t.status")
    List<Object[]> countTaskStatusBreakdown();

    @Query("SELECT t.status, COUNT(t) FROM PatrolTask t WHERE t.orgId = :orgId AND t.status IN (0, 1, 3) GROUP BY t.status")
    List<Object[]> countTaskStatusBreakdownByOrgId(@Param("orgId") Long orgId);

    @Query("SELECT t.status, COUNT(t) FROM PatrolTask t WHERE t.executorId = :executorId AND t.status IN (0, 1, 3) GROUP BY t.status")
    List<Object[]> countTaskStatusBreakdownByExecutorId(@Param("executorId") Long executorId);

    @Query("SELECT COUNT(t) FROM PatrolTask t WHERE t.status = :status")
    Long countByStatus(@Param("status") Integer status);

    @Query("SELECT COUNT(t) FROM PatrolTask t WHERE t.orgId = :orgId AND t.status = :status")
    Long countByOrgIdAndStatus(@Param("orgId") Long orgId, @Param("status") Integer status);

    @Query("SELECT COUNT(t) FROM PatrolTask t WHERE t.executorId = :executorId AND t.status = :status")
    Long countByExecutorIdAndStatus(@Param("executorId") Long executorId, @Param("status") Integer status);

    @Query("SELECT t FROM PatrolTask t ORDER BY t.createTime DESC")
    Page<PatrolTask> findAllOrderByCreateTimeDesc(Pageable pageable);

    @Query("SELECT COUNT(t) FROM PatrolTask t WHERE t.deadline BETWEEN :start AND :end AND t.status IN (0, 1)")
    Long countUpcomingDeadlines(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
