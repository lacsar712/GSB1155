package com.yixin.patrol.controller;

import com.yixin.patrol.dto.ApiResponse;
import com.yixin.patrol.entity.Organization;
import com.yixin.patrol.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Organization>>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(ApiResponse.success(organizations));
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOrganizationTree() {
        List<Map<String, Object>> tree = organizationService.getOrganizationTreeWithChildren();
        return ResponseEntity.ok(ApiResponse.success(tree));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Organization>> getOrganizationById(@PathVariable Long id) {
        Organization organization = organizationService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(organization));
    }

    @GetMapping("/children/{parentId}")
    public ResponseEntity<ApiResponse<List<Organization>>> getChildOrganizations(@PathVariable Long parentId) {
        List<Organization> children = organizationService.getByParentId(parentId);
        return ResponseEntity.ok(ApiResponse.success(children));
    }

    @GetMapping("/schools")
    public ResponseEntity<ApiResponse<List<Organization>>> getAllSchools() {
        List<Organization> schools = organizationService.getAllSchools();
        return ResponseEntity.ok(ApiResponse.success(schools));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Organization>> createOrganization(@RequestBody Organization organization) {
        Organization created = organizationService.createOrganization(organization);
        return ResponseEntity.ok(ApiResponse.success("组织机构创建成功", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Organization>> updateOrganization(
            @PathVariable Long id,
            @RequestBody Organization organization) {
        Organization updated = organizationService.updateOrganization(id, organization);
        return ResponseEntity.ok(ApiResponse.success("组织机构更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.ok(ApiResponse.success("组织机构删除成功", null));
    }
}
