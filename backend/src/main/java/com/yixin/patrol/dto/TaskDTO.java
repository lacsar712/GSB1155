package com.yixin.patrol.dto;

import com.yixin.patrol.entity.PatrolTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private Long templateId;
    private String templateName;
    private Long orgId;
    private String orgName;
    private Long executorId;
    private String executorName;
    private Long assignerId;
    private String assignerName;
    private Integer priority;
    private String priorityName;
    private Integer status;
    private String statusName;
    private LocalDateTime deadline;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private LocalDateTime createTime;

    public static TaskDTO fromEntity(PatrolTask task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setTemplateId(task.getTemplateId());
        dto.setOrgId(task.getOrgId());
        dto.setExecutorId(task.getExecutorId());
        dto.setAssignerId(task.getAssignerId());
        dto.setPriority(task.getPriority());
        dto.setPriorityName(task.getPriorityName());
        dto.setStatus(task.getStatus());
        dto.setStatusName(task.getStatusName());
        dto.setDeadline(task.getDeadline());
        dto.setStartTime(task.getStartTime());
        dto.setCompleteTime(task.getCompleteTime());
        dto.setCreateTime(task.getCreateTime());

        if (task.getTemplate() != null) {
            dto.setTemplateName(task.getTemplate().getName());
        }
        if (task.getOrganization() != null) {
            dto.setOrgName(task.getOrganization().getName());
        }
        if (task.getExecutor() != null) {
            dto.setExecutorName(task.getExecutor().getRealName());
        }
        if (task.getAssigner() != null) {
            dto.setAssignerName(task.getAssigner().getRealName());
        }
        return dto;
    }
}
