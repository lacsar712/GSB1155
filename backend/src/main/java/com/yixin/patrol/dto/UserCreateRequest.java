package com.yixin.patrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码摘要不能为空")
    @Pattern(regexp = "^[A-Fa-f0-9]{64}$", message = "密码摘要格式不正确")
    private String passwordHash;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String phone;

    private String email;

    @NotNull(message = "所属机构不能为空")
    private Long orgId;

    @NotNull(message = "角色不能为空")
    private Integer roleType;

    private Integer status = 1;
}
