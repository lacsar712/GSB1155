package com.yixin.patrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskReportCreateRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotBlank(message = "上报内容不能为空")
    private String reportContent;

    private String checkResult;

    private String images;

    private String location;
}
