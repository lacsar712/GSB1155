package com.yixin.patrol.controller;

import com.yixin.patrol.dto.ApiResponse;
import com.yixin.patrol.dto.UserDTO;
import com.yixin.patrol.dto.UserCreateRequest;
import com.yixin.patrol.entity.User;
import com.yixin.patrol.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByOrg(@PathVariable Long orgId) {
        List<UserDTO> users = userService.getUsersByOrgId(orgId);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/inspectors")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getInspectors(
            @RequestParam(required = false) Long orgId) {
        List<UserDTO> users;
        if (orgId != null) {
            users = userService.getInspectors(orgId);
        } else {
            users = userService.getUsersByRoleType(3);
        }
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserDTO created = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success("用户创建成功", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        UserDTO updated = userService.updateUser(id, user);
        return ResponseEntity.ok(ApiResponse.success("用户更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
    }
}
