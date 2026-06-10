package com.yixin.patrol.service;

import com.yixin.patrol.dto.DashboardStats;
import com.yixin.patrol.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DashboardService {

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
        return DashboardStats.builder()
                .totalTasks(taskRepository.count())
                .pendingTasks(taskRepository.countByStatus(0))
                .inProgressTasks(taskRepository.countByStatus(1))
                .completedTasks(taskRepository.countByStatus(3))
                .overdueCount((long) taskRepository.findOverdueTasks(LocalDateTime.now()).size())
                .pendingReviewCount(reportRepository.countByReviewStatus(0))
                .totalUsers(userRepository.countActiveUsers())
                .totalSchools(organizationRepository.countByOrgType(2))
                .totalTemplates(templateRepository.countActive())
                .build();
    }

    public DashboardStats getStatsByOrg(Long orgId) {
        return DashboardStats.builder()
                .totalTasks((long) taskRepository.findByOrgId(orgId).size())
                .pendingTasks(taskRepository.countByOrgIdAndStatus(orgId, 0))
                .inProgressTasks(taskRepository.countByOrgIdAndStatus(orgId, 1))
                .completedTasks(taskRepository.countByOrgIdAndStatus(orgId, 3))
                .build();
    }

    public DashboardStats getStatsForExecutor(Long executorId) {
        return DashboardStats.builder()
                .totalTasks((long) taskRepository.findByExecutorId(executorId).size())
                .pendingTasks(taskRepository.countByExecutorIdAndStatus(executorId, 0))
                .inProgressTasks(taskRepository.countByExecutorIdAndStatus(executorId, 1))
                .completedTasks(taskRepository.countByExecutorIdAndStatus(executorId, 3))
                .build();
    }
}
