package com.yixin.patrol.dto;

import com.yixin.patrol.entity.TaskReport;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskReportDTO {

    private Long id;
    private Long taskId;
    private String taskTitle;
    private String reportContent;
    private String checkResult;
    private String images;
    private String location;
    private Long reportUserId;
    private String reportUserName;
    private LocalDateTime reportTime;
    private Integer reviewStatus;
    private String reviewStatusName;
    private Long reviewUserId;
    private LocalDateTime reviewTime;
    private String reviewComment;

    public static TaskReportDTO fromEntity(TaskReport report) {
        TaskReportDTO dto = new TaskReportDTO();
        dto.setId(report.getId());
        dto.setTaskId(report.getTaskId());
        dto.setReportContent(report.getReportContent());
        dto.setCheckResult(report.getCheckResult());
        dto.setImages(report.getImages());
        dto.setLocation(report.getLocation());
        dto.setReportUserId(report.getReportUserId());
        dto.setReportTime(report.getReportTime());
        dto.setReviewStatus(report.getReviewStatus());
        dto.setReviewStatusName(report.getReviewStatusName());
        dto.setReviewUserId(report.getReviewUserId());
        dto.setReviewTime(report.getReviewTime());
        dto.setReviewComment(report.getReviewComment());

        if (report.getTask() != null) {
            dto.setTaskTitle(report.getTask().getTitle());
        }
        if (report.getReportUser() != null) {
            dto.setReportUserName(report.getReportUser().getRealName());
        }

        return dto;
    }
}
