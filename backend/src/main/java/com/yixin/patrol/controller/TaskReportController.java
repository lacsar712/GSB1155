package com.yixin.patrol.controller;

import com.yixin.patrol.dto.ApiResponse;
import com.yixin.patrol.dto.TaskReportCreateRequest;
import com.yixin.patrol.dto.TaskReportDTO;
import com.yixin.patrol.dto.TaskReviewRequest;
import com.yixin.patrol.security.UserPrincipal;
import com.yixin.patrol.service.TaskReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-reports")
public class TaskReportController {

    @Autowired
    private TaskReportService taskReportService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiResponse<List<TaskReportDTO>>> getReportsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.success(taskReportService.getReportsByTaskId(taskId)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<TaskReportDTO>>> getMyReports(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(taskReportService.getMyReports(currentUser.getId())));
    }

    @GetMapping("/pending-review")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<TaskReportDTO>>> getPendingReviews() {
        return ResponseEntity.ok(ApiResponse.success(taskReportService.getPendingReviews()));
    }

    @PostMapping
    @PreAuthorize("hasRole('INSPECTOR')")
    public ResponseEntity<ApiResponse<TaskReportDTO>> submitReport(
            @Valid @RequestBody TaskReportCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        TaskReportDTO report = taskReportService.submitReport(request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("上报提交成功", report));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<TaskReportDTO>> reviewReport(
            @PathVariable Long id,
            @Valid @RequestBody TaskReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        TaskReportDTO report = taskReportService.reviewReport(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("审核完成", report));
    }
}
