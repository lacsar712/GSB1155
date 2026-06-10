package com.yixin.patrol.repository;

import com.yixin.patrol.entity.TaskReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COUNT(r) FROM TaskReport r WHERE r.reviewStatus = :status")
    Long countByReviewStatus(@Param("status") Integer status);

    default Long countPendingReviews() {
        return countByReviewStatus(0);
    }
}
