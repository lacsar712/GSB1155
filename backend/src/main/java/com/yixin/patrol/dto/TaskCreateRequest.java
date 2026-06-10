package com.yixin.patrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {

    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String description;

    private Long templateId;

    @NotNull(message = "执行单位不能为空")
    private Long orgId;

    @NotNull(message = "执行人不能为空")
    private Long executorId;

    private Integer priority = 2;

    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;
}
