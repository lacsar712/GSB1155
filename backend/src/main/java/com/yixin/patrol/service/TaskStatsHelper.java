package com.yixin.patrol.service;

import com.yixin.patrol.repository.PatrolTaskRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskStatsHelper {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_IN_PROGRESS = 1;
    public static final int STATUS_COMPLETED = 3;

    private final PatrolTaskRepository taskRepository;

    public TaskStatsHelper(PatrolTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Map<Integer, Long> getGlobalStatusCounts() {
        return toStatusMap(taskRepository.countGroupByStatus());
    }

    public Map<Integer, Long> getStatusCountsByOrg(Long orgId) {
        return toStatusMap(taskRepository.countGroupByStatusByOrgId(orgId));
    }

    public Map<Integer, Long> getStatusCountsByExecutor(Long executorId) {
        return toStatusMap(taskRepository.countGroupByStatusByExecutorId(executorId));
    }

    public long countOverdue() {
        return taskRepository.countOverdueTasks(LocalDateTime.now());
    }

    public long countTotal() {
        return taskRepository.count();
    }

    public long countTotalByOrg(Long orgId) {
        return taskRepository.countByOrgId(orgId);
    }

    public long countTotalByExecutor(Long executorId) {
        return taskRepository.countByExecutorId(executorId);
    }

    public long getOrDefault(Map<Integer, Long> statusCounts, int status) {
        return statusCounts.getOrDefault(status, 0L);
    }

    private Map<Integer, Long> toStatusMap(List<Object[]> results) {
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] row : results) {
            map.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        return map;
    }
}
