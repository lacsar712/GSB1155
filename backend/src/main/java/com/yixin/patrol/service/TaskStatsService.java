package com.yixin.patrol.service;

import com.yixin.patrol.dto.TaskStatusCounts;
import com.yixin.patrol.repository.PatrolTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskStatsService {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_IN_PROGRESS = 1;
    private static final int STATUS_COMPLETED = 3;

    @Autowired
    private PatrolTaskRepository taskRepository;

    public TaskStatusCounts getStatusCounts() {
        return TaskStatusCounts.builder()
                .totalTasks(taskRepository.count())
                .pendingTasks(taskRepository.countByStatus(STATUS_PENDING))
                .inProgressTasks(taskRepository.countByStatus(STATUS_IN_PROGRESS))
                .completedTasks(taskRepository.countByStatus(STATUS_COMPLETED))
                .build();
    }

    public TaskStatusCounts getStatusCountsByOrg(Long orgId) {
        return TaskStatusCounts.builder()
                .totalTasks(taskRepository.countByOrgId(orgId))
                .pendingTasks(taskRepository.countByOrgIdAndStatus(orgId, STATUS_PENDING))
                .inProgressTasks(taskRepository.countByOrgIdAndStatus(orgId, STATUS_IN_PROGRESS))
                .completedTasks(taskRepository.countByOrgIdAndStatus(orgId, STATUS_COMPLETED))
                .build();
    }

    public TaskStatusCounts getStatusCountsForExecutor(Long executorId) {
        return TaskStatusCounts.builder()
                .totalTasks(taskRepository.countByExecutorId(executorId))
                .pendingTasks(taskRepository.countByExecutorIdAndStatus(executorId, STATUS_PENDING))
                .inProgressTasks(taskRepository.countByExecutorIdAndStatus(executorId, STATUS_IN_PROGRESS))
                .completedTasks(taskRepository.countByExecutorIdAndStatus(executorId, STATUS_COMPLETED))
                .build();
    }

    public long countOverdueTasks() {
        return taskRepository.countOverdueTasks(LocalDateTime.now());
    }
}
