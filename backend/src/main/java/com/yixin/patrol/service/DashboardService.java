package com.yixin.patrol.service;

import com.yixin.patrol.dto.DashboardStats;
import com.yixin.patrol.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_IN_PROGRESS = 1;
    private static final int STATUS_COMPLETED = 3;

    @Autowired
    private PatrolTaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private TaskTemplateRepository templateRepository;

    @Autowired
    private TaskReportRepository reportRepository;

    public DashboardStats getStats() {
        DashboardStats.DashboardStatsBuilder builder = DashboardStats.builder()
                .totalTasks(taskRepository.count())
                .overdueCount(taskRepository.countOverdueTasks(LocalDateTime.now()))
                .pendingReviewCount(reportRepository.countPendingReviews())
                .totalUsers(userRepository.countActiveUsers())
                .totalSchools(organizationRepository.countByOrgType(2))
                .totalTemplates(templateRepository.countActive());
        applyTaskStatusCounts(builder, taskRepository.countTaskStatusBreakdown());
        return builder.build();
    }

    public DashboardStats getStatsByOrg(Long orgId) {
        DashboardStats.DashboardStatsBuilder builder = DashboardStats.builder()
                .totalTasks(taskRepository.countByOrgId(orgId));
        applyTaskStatusCounts(builder, taskRepository.countTaskStatusBreakdownByOrgId(orgId));
        return builder.build();
    }

    public DashboardStats getStatsForExecutor(Long executorId) {
        DashboardStats.DashboardStatsBuilder builder = DashboardStats.builder()
                .totalTasks(taskRepository.countByExecutorId(executorId));
        applyTaskStatusCounts(builder, taskRepository.countTaskStatusBreakdownByExecutorId(executorId));
        return builder.build();
    }

    private void applyTaskStatusCounts(DashboardStats.DashboardStatsBuilder builder,
                                       List<Object[]> statusRows) {
        long pending = 0L;
        long inProgress = 0L;
        long completed = 0L;
        for (Object[] row : statusRows) {
            Integer status = (Integer) row[0];
            Long count = (Long) row[1];
            if (status == STATUS_PENDING) {
                pending = count;
            } else if (status == STATUS_IN_PROGRESS) {
                inProgress = count;
            } else if (status == STATUS_COMPLETED) {
                completed = count;
            }
        }
        builder.pendingTasks(pending)
               .inProgressTasks(inProgress)
               .completedTasks(completed);
    }
}
