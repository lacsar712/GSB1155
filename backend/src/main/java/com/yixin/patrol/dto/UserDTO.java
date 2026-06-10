package com.yixin.patrol.dto;

import com.yixin.patrol.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Long orgId;
    private String orgName;
    private Integer roleType;
    private String roleName;
    private Integer status;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setOrgId(user.getOrgId());
        dto.setRoleType(user.getRoleType());
        dto.setRoleName(user.getRoleName());
        dto.setStatus(user.getStatus());
        if (user.getOrganization() != null) {
            dto.setOrgName(user.getOrganization().getName());
        }
        return dto;
    }
}
