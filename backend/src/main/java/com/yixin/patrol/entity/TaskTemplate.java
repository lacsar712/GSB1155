package com.yixin.patrol.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 检查模板实体
 */
@Data
@Entity
@Table(name = "task_template")
public class TaskTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String category;

    /**
     * 检查项目列表(JSON格式)
     */
    @Column(name = "check_items", columnDefinition = "JSON")
    private String checkItems;

    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status = 1;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
