package com.yixin.patrol.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 组织机构实体
 */
@Data
@Entity
@Table(name = "sys_organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Column(name = "parent_id")
    private Long parentId = 0L;

    /**
     * 机构类型: 1-教育局 2-学校 3-部门
     */
    @Column(name = "org_type")
    private Integer orgType = 1;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

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
