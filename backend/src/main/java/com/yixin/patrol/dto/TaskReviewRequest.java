package com.yixin.patrol.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskReviewRequest {

    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    private String reviewComment;
}
