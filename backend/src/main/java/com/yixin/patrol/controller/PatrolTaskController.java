package com.yixin.patrol.controller;

import com.yixin.patrol.dto.ApiResponse;
import com.yixin.patrol.dto.TaskCreateRequest;
import com.yixin.patrol.dto.TaskDTO;
import com.yixin.patrol.security.UserPrincipal;
import com.yixin.patrol.service.PatrolTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class PatrolTaskController {

    @Autowired
    private PatrolTaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskDTO>>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskDTO> tasks = taskService.getAllTasks(page, size);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByOrg(@PathVariable Long orgId) {
        List<TaskDTO> tasks = taskService.getTasksByOrgId(orgId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/executor/{executorId}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByExecutor(@PathVariable Long executorId) {
        List<TaskDTO> tasks = taskService.getTasksByExecutorId(executorId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getMyTasks(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<TaskDTO> tasks = taskService.getTasksByExecutorId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/my/pending")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getMyPendingTasks(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<TaskDTO> tasks = taskService.getPendingTasksForExecutor(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByStatus(@PathVariable Integer status) {
        List<TaskDTO> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        TaskDTO task = taskService.createTask(request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("任务创建成功", task));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
            @PathVariable Long id,
            @RequestBody TaskCreateRequest request) {
        TaskDTO task = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success("任务更新成功", task));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        TaskDTO task = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("任务状态更新成功", task));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("任务删除成功", null));
    }
}
