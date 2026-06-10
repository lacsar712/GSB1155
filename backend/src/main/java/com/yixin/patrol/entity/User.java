package com.yixin.patrol.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "sys_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    private String avatar;

    @Column(name = "org_id")
    private Long orgId;

    /**
     * 角色类型: 1-系统管理员 2-单位管理员 3-巡检员
     */
    @Column(name = "role_type")
    private Integer roleType = 3;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status = 1;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", insertable = false, updatable = false)
    private Organization organization;

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
     * 获取角色名称
     */
    public String getRoleName() {
        return switch (roleType) {
            case 1 -> "系统管理员";
            case 2 -> "单位管理员";
            case 3 -> "巡检员";
            default -> "未知角色";
        };
    }
}
