package com.yixin.patrol.service;

import com.yixin.patrol.dto.DashboardStats;
import com.yixin.patrol.repository.OrganizationRepository;
import com.yixin.patrol.repository.TaskReportRepository;
import com.yixin.patrol.repository.TaskTemplateRepository;
import com.yixin.patrol.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DashboardService {

    private final TaskStatsHelper taskStatsHelper;
    private final TaskReportRepository reportRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final TaskTemplateRepository templateRepository;

    public DashboardService(TaskStatsHelper taskStatsHelper,
                            TaskReportRepository reportRepository,
                            UserRepository userRepository,
                            OrganizationRepository organizationRepository,
                            TaskTemplateRepository templateRepository) {
        this.taskStatsHelper = taskStatsHelper;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.templateRepository = templateRepository;
    }

    public DashboardStats getStats() {
        Map<Integer, Long> statusCounts = taskStatsHelper.getGlobalStatusCounts();
        return DashboardStats.builder()
                .totalTasks(taskStatsHelper.countTotal())
                .pendingTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_PENDING))
                .inProgressTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_IN_PROGRESS))
                .completedTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_COMPLETED))
                .overdueCount(taskStatsHelper.countOverdue())
                .pendingReviewCount(reportRepository.countByReviewStatus(0))
                .totalUsers(userRepository.countActiveUsers())
                .totalSchools(organizationRepository.countByOrgType(2))
                .totalTemplates(templateRepository.countActive())
                .build();
    }

    public DashboardStats getStatsByOrg(Long orgId) {
        Map<Integer, Long> statusCounts = taskStatsHelper.getStatusCountsByOrg(orgId);
        return DashboardStats.builder()
                .totalTasks(taskStatsHelper.countTotalByOrg(orgId))
                .pendingTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_PENDING))
                .inProgressTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_IN_PROGRESS))
                .completedTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_COMPLETED))
                .build();
    }

    public DashboardStats getStatsForExecutor(Long executorId) {
        Map<Integer, Long> statusCounts = taskStatsHelper.getStatusCountsByExecutor(executorId);
        return DashboardStats.builder()
                .totalTasks(taskStatsHelper.countTotalByExecutor(executorId))
                .pendingTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_PENDING))
                .inProgressTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_IN_PROGRESS))
                .completedTasks(taskStatsHelper.getOrDefault(statusCounts, TaskStatsHelper.STATUS_COMPLETED))
                .build();
    }
}
