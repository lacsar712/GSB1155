package com.yixin.patrol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusCounts {

    private Long totalTasks;
    private Long pendingTasks;
    private Long inProgressTasks;
    private Long completedTasks;
}
