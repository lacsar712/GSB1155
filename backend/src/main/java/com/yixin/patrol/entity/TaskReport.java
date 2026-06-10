package com.yixin.patrol.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务上报记录实体
 */
@Data
@Entity
@Table(name = "task_report")
public class TaskReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "report_content", columnDefinition = "TEXT")
    private String reportContent;

    /**
     * 检查结果(JSON格式)
     */
    @Column(name = "check_result", columnDefinition = "JSON")
    private String checkResult;

    /**
     * 图片列表(JSON格式)
     */
    @Column(columnDefinition = "JSON")
    private String images;

    private String location;

    @Column(name = "report_user_id")
    private Long reportUserId;

    @Column(name = "report_time")
    private LocalDateTime reportTime;

    /**
     * 审核状态: 0-待审核 1-通过 2-驳回
     */
    @Column(name = "review_status")
    private Integer reviewStatus = 0;

    @Column(name = "review_user_id")
    private Long reviewUserId;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private PatrolTask task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_user_id", insertable = false, updatable = false)
    private User reportUser;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        reportTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    /**
     * 获取审核状态名称
     */
    public String getReviewStatusName() {
        return switch (reviewStatus) {
            case 0 -> "待审核";
            case 1 -> "已通过";
            case 2 -> "已驳回";
            default -> "未知";
        };
    }
}
