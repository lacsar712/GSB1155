package com.yixin.patrol.controller;

import com.yixin.patrol.dto.ApiResponse;
import com.yixin.patrol.entity.TaskTemplate;
import com.yixin.patrol.service.TaskTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TaskTemplateController {

    @Autowired
    private TaskTemplateService templateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskTemplate>>> getAllTemplates() {
        List<TaskTemplate> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskTemplate>> getTemplateById(@PathVariable Long id) {
        TaskTemplate template = templateService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(template));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = templateService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<TaskTemplate>>> getByCategory(@PathVariable String category) {
        List<TaskTemplate> templates = templateService.getByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<TaskTemplate>> createTemplate(@RequestBody TaskTemplate template) {
        TaskTemplate created = templateService.createTemplate(template);
        return ResponseEntity.ok(ApiResponse.success("模板创建成功", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<TaskTemplate>> updateTemplate(
            @PathVariable Long id,
            @RequestBody TaskTemplate template) {
        TaskTemplate updated = templateService.updateTemplate(id, template);
        return ResponseEntity.ok(ApiResponse.success("模板更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("模板删除成功", null));
    }
}
