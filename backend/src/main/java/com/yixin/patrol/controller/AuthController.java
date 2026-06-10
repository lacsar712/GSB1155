package com.yixin.patrol.controller;

import com.yixin.patrol.dto.*;
import com.yixin.patrol.entity.User;
import com.yixin.patrol.repository.UserRepository;
import com.yixin.patrol.security.JwtTokenProvider;
import com.yixin.patrol.security.UserPrincipal;
import com.yixin.patrol.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            if (user.getStatus() != 1) {
                throw new RuntimeException("用户已被禁用");
            }

            if (!passwordEncoder.matches(loginRequest.getPasswordHash(), user.getPassword())) {
                throw new RuntimeException("用户名或密码错误");
            }

            UserPrincipal userPrincipal = UserPrincipal.create(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    userPrincipal.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            // 更新最后登录时间
            userService.updateLastLoginTime(userPrincipal.getId());

            UserDTO userDTO = UserDTO.fromEntity(user);

            LoginResponse response = new LoginResponse(jwt, userDTO);
            
            logger.info("用户 {} 登录成功", loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            logger.error("用户 {} 登录失败: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.ok(ApiResponse.error(401, "用户名或密码错误"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        UserDTO user = userService.getUserById(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("退出成功", null));
    }
}
