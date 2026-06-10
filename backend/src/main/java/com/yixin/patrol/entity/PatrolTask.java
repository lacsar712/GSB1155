package com.yixin.patrol.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 巡检任务实体
 */
@Data
@Entity
@Table(name = "patrol_task")
public class PatrolTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "executor_id")
    private Long executorId;

    @Column(name = "assigner_id")
    private Long assignerId;

    /**
     * 优先级: 1-低 2-中 3-高
     */
    private Integer priority = 2;

    /**
     * 状态: 0-待执行 1-执行中 2-待审核 3-已完成 4-已驳回
     */
    private Integer status = 0;

    private LocalDateTime deadline;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private TaskTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", insertable = false, updatable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", insertable = false, updatable = false)
    private User executor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigner_id", insertable = false, updatable = false)
    private User assigner;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        return switch (status) {
            case 0 -> "待执行";
            case 1 -> "执行中";
            case 2 -> "待审核";
            case 3 -> "已完成";
            case 4 -> "已驳回";
            default -> "未知";
        };
    }

    /**
     * 获取优先级名称
     */
    public String getPriorityName() {
        return switch (priority) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            default -> "中";
        };
    }
}
