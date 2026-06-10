package com.yixin.patrol.controller;

import com.yixin.patrol.dto.ApiResponse;
import com.yixin.patrol.dto.DashboardStats;
import com.yixin.patrol.security.UserPrincipal;
import com.yixin.patrol.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStats>> getStats(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        DashboardStats stats;
        
        // 根据角色返回不同范围的统计
        switch (currentUser.getRoleType()) {
            case 1: // 系统管理员 - 全局统计
                stats = dashboardService.getStats();
                break;
            case 2: // 单位管理员 - 本单位统计
                stats = dashboardService.getStatsByOrg(currentUser.getOrgId());
                break;
            case 3: // 巡检员 - 个人统计
                stats = dashboardService.getStatsForExecutor(currentUser.getId());
                break;
            default:
                stats = new DashboardStats();
        }
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/stats/all")
    public ResponseEntity<ApiResponse<DashboardStats>> getAllStats() {
        DashboardStats stats = dashboardService.getStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
