package com.yixin.patrol.repository;

import com.yixin.patrol.entity.TaskReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskReportRepository extends JpaRepository<TaskReport, Long> {

    List<TaskReport> findByTaskId(Long taskId);

    List<TaskReport> findByTaskIdOrderByReportTimeDesc(Long taskId);

    Optional<TaskReport> findFirstByTaskIdOrderByReportTimeDesc(Long taskId);

    List<TaskReport> findByReportUserId(Long reportUserId);

    List<TaskReport> findByReportUserIdOrderByReportTimeDesc(Long reportUserId);

    List<TaskReport> findByReviewStatus(Integer reviewStatus);

    @Query("SELECT r FROM TaskReport r WHERE r.reviewStatus = 0 ORDER BY r.reportTime DESC")
    List<TaskReport> findPendingReviews();

    /**
     * 按复核状态计数。使用 Spring Data 派生查询，对应 SQL 为 SELECT COUNT(*)，避免拉取全量数据。
     */
    Long countByReviewStatus(Integer reviewStatus);
}
