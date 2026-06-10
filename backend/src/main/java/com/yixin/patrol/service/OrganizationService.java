package com.yixin.patrol.service;

import com.yixin.patrol.entity.Organization;
import com.yixin.patrol.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public Organization getById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("组织机构不存在"));
    }

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAllActive();
    }

    public List<Organization> getOrganizationTree() {
        List<Organization> allOrgs = organizationRepository.findAllActive();
        return buildTree(allOrgs, 0L);
    }

    private List<Organization> buildTree(List<Organization> allOrgs, Long parentId) {
        return allOrgs.stream()
                .filter(org -> org.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getOrganizationTreeWithChildren() {
        List<Organization> allOrgs = organizationRepository.findAllActive();
        return buildTreeMap(allOrgs, 0L);
    }

    private List<Map<String, Object>> buildTreeMap(List<Organization> allOrgs, Long parentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        allOrgs.stream()
                .filter(org -> org.getParentId().equals(parentId))
                .forEach(org -> {
                    Map<String, Object> node = Map.of(
                            "id", org.getId(),
                            "name", org.getName(),
                            "code", org.getCode(),
                            "orgType", org.getOrgType(),
                            "children", buildTreeMap(allOrgs, org.getId())
                    );
                    result.add(node);
                });
        
        return result;
    }

    public List<Organization> getByParentId(Long parentId) {
        return organizationRepository.findByParentIdAndStatus(parentId, 1);
    }

    public List<Organization> getAllSchools() {
        return organizationRepository.findAllSchools();
    }

    @Transactional
    public Organization createOrganization(Organization organization) {
        if (organizationRepository.existsByCode(organization.getCode())) {
            throw new RuntimeException("机构编码已存在");
        }
        return organizationRepository.save(organization);
    }

    @Transactional
    public Organization updateOrganization(Long id, Organization orgDetails) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("组织机构不存在"));

        if (orgDetails.getName() != null) {
            organization.setName(orgDetails.getName());
        }
        if (orgDetails.getSortOrder() != null) {
            organization.setSortOrder(orgDetails.getSortOrder());
        }
        if (orgDetails.getStatus() != null) {
            organization.setStatus(orgDetails.getStatus());
        }

        return organizationRepository.save(organization);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        // 检查是否有子机构
        List<Organization> children = organizationRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("该机构下存在子机构，无法删除");
        }
        organizationRepository.deleteById(id);
    }

    public Long countSchools() {
        return organizationRepository.countByOrgType(2);
    }
}
