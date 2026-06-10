package com.yixin.patrol.service;

import com.yixin.patrol.dto.DashboardStats;
import com.yixin.patrol.dto.TaskStatusCounts;
import com.yixin.patrol.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private TaskStatsService taskStatsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private TaskTemplateRepository templateRepository;

    @Autowired
    private TaskReportRepository reportRepository;

    public DashboardStats getStats() {
        TaskStatusCounts statusCounts = taskStatsService.getStatusCounts();
        return DashboardStats.builder()
                .totalTasks(statusCounts.getTotalTasks())
                .pendingTasks(statusCounts.getPendingTasks())
                .inProgressTasks(statusCounts.getInProgressTasks())
                .completedTasks(statusCounts.getCompletedTasks())
                .overdueCount(taskStatsService.countOverdueTasks())
                .pendingReviewCount(reportRepository.countByReviewStatus(0))
                .totalUsers(userRepository.countActiveUsers())
                .totalSchools(organizationRepository.countByOrgType(2))
                .totalTemplates(templateRepository.countActive())
                .build();
    }

    public DashboardStats getStatsByOrg(Long orgId) {
        TaskStatusCounts statusCounts = taskStatsService.getStatusCountsByOrg(orgId);
        return DashboardStats.builder()
                .totalTasks(statusCounts.getTotalTasks())
                .pendingTasks(statusCounts.getPendingTasks())
                .inProgressTasks(statusCounts.getInProgressTasks())
                .completedTasks(statusCounts.getCompletedTasks())
                .build();
    }

    public DashboardStats getStatsForExecutor(Long executorId) {
        TaskStatusCounts statusCounts = taskStatsService.getStatusCountsForExecutor(executorId);
        return DashboardStats.builder()
                .totalTasks(statusCounts.getTotalTasks())
                .pendingTasks(statusCounts.getPendingTasks())
                .inProgressTasks(statusCounts.getInProgressTasks())
                .completedTasks(statusCounts.getCompletedTasks())
                .build();
    }
}
