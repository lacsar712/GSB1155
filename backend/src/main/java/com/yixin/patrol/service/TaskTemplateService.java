package com.yixin.patrol.service;

import com.yixin.patrol.entity.TaskTemplate;
import com.yixin.patrol.repository.TaskTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskTemplateService {

    @Autowired
    private TaskTemplateRepository templateRepository;

    public TaskTemplate getById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("模板不存在"));
    }

    public List<TaskTemplate> getAllTemplates() {
        return templateRepository.findAllActive();
    }

    public List<TaskTemplate> getByCategory(String category) {
        return templateRepository.findByCategoryAndStatus(category, 1);
    }

    public List<String> getAllCategories() {
        return templateRepository.findAllCategories();
    }

    @Transactional
    public TaskTemplate createTemplate(TaskTemplate template) {
        return templateRepository.save(template);
    }

    @Transactional
    public TaskTemplate updateTemplate(Long id, TaskTemplate templateDetails) {
        TaskTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("模板不存在"));

        if (templateDetails.getName() != null) {
            template.setName(templateDetails.getName());
        }
        if (templateDetails.getDescription() != null) {
            template.setDescription(templateDetails.getDescription());
        }
        if (templateDetails.getCategory() != null) {
            template.setCategory(templateDetails.getCategory());
        }
        if (templateDetails.getCheckItems() != null) {
            template.setCheckItems(templateDetails.getCheckItems());
        }
        if (templateDetails.getStatus() != null) {
            template.setStatus(templateDetails.getStatus());
        }

        return templateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    public Long countActive() {
        return templateRepository.countActive();
    }
}
