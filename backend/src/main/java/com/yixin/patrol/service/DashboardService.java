package com.yixin.patrol.service;

import com.yixin.patrol.dto.DashboardStats;
import com.yixin.patrol.repository.OrganizationRepository;
import com.yixin.patrol.repository.PatrolTaskRepository;
import com.yixin.patrol.repository.TaskReportRepository;
import com.yixin.patrol.repository.TaskTemplateRepository;
import com.yixin.patrol.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * 仪表盘统计服务，统一负责面向 Dashboard 的聚合查询。
 * 所有统计均通过数据库 COUNT 完成，不再拉取实体列表后用 size() 计数。
 */
@Service
public class DashboardService {

    /** 任务状态：待执行 */
    private static final int STATUS_PENDING = 0;
    /** 任务状态：执行中 */
    private static final int STATUS_IN_PROGRESS = 1;
    /** 任务状态：已完成 */
    private static final int STATUS_COMPLETED = 3;
    /** 任务报告复核状态：待复核 */
    private static final int REVIEW_STATUS_PENDING = 0;

    private final PatrolTaskRepository taskRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final TaskTemplateRepository templateRepository;
    private final TaskReportRepository reportRepository;

    @Autowired
    public DashboardService(PatrolTaskRepository taskRepository,
                            UserRepository userRepository,
                            OrganizationRepository organizationRepository,
                            TaskTemplateRepository templateRepository,
                            TaskReportRepository reportRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.templateRepository = templateRepository;
        this.reportRepository = reportRepository;
    }

    /**
     * 获取全局仪表盘统计。
     */
    public DashboardStats getStats() {
        DashboardStats.DashboardStatsBuilder builder = DashboardStats.builder()
                .totalTasks(taskRepository.count())
                .overdueCount(taskRepository.countOverdueTasks(LocalDateTime.now()))
                .pendingReviewCount(reportRepository.countByReviewStatus(REVIEW_STATUS_PENDING))
                .totalUsers(userRepository.countActiveUsers())
                .totalSchools(organizationRepository.countByOrgType(2))
                .totalTemplates(templateRepository.countActive());
        return fillStatusCounts(builder, taskRepository::countByStatus).build();
    }

    /**
     * 获取指定组织维度的仪表盘统计。
     */
    public DashboardStats getStatsByOrg(Long orgId) {
        DashboardStats.DashboardStatsBuilder builder = DashboardStats.builder()
                .totalTasks(taskRepository.countByOrgId(orgId));
        return fillStatusCounts(builder, status -> taskRepository.countByOrgIdAndStatus(orgId, status)).build();
    }

    /**
     * 获取指定执行人维度的仪表盘统计。
     */
    public DashboardStats getStatsForExecutor(Long executorId) {
        DashboardStats.DashboardStatsBuilder builder = DashboardStats.builder()
                .totalTasks(taskRepository.countByExecutorId(executorId));
        return fillStatusCounts(builder, status -> taskRepository.countByExecutorIdAndStatus(executorId, status)).build();
    }

    /**
     * 通用任务状态计数填充：通过传入的计数函数填入待执行 / 执行中 / 已完成三类计数，
     * 避免在不同维度下重复书写同样的状态分支。
     */
    private DashboardStats.DashboardStatsBuilder fillStatusCounts(
            DashboardStats.DashboardStatsBuilder builder,
            Function<Integer, Long> counter) {
        return builder
                .pendingTasks(counter.apply(STATUS_PENDING))
                .inProgressTasks(counter.apply(STATUS_IN_PROGRESS))
                .completedTasks(counter.apply(STATUS_COMPLETED));
    }
}
