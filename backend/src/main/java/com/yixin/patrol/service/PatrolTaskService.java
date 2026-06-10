package com.yixin.patrol.service;

import com.yixin.patrol.dto.TaskCreateRequest;
import com.yixin.patrol.dto.TaskDTO;
import com.yixin.patrol.entity.PatrolTask;
import com.yixin.patrol.repository.PatrolTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatrolTaskService {

    @Autowired
    private PatrolTaskRepository taskRepository;

    public TaskDTO getTaskById(Long id) {
        PatrolTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        return TaskDTO.fromEntity(task);
    }

    public Page<TaskDTO> getAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return taskRepository.findAllOrderByCreateTimeDesc(pageable)
                .map(TaskDTO::fromEntity);
    }

    public List<TaskDTO> getTasksByOrgId(Long orgId) {
        return taskRepository.findByOrgId(orgId).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByExecutorId(Long executorId) {
        return taskRepository.findByExecutorId(executorId).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByStatus(Integer status) {
        return taskRepository.findByStatus(status).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getPendingTasksForExecutor(Long executorId) {
        return taskRepository.findByExecutorIdAndStatuses(executorId, List.of(0, 1)).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO createTask(TaskCreateRequest request, Long assignerId) {
        PatrolTask task = new PatrolTask();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setTemplateId(request.getTemplateId());
        task.setOrgId(request.getOrgId());
        task.setExecutorId(request.getExecutorId());
        task.setAssignerId(assignerId);
        task.setPriority(request.getPriority());
        task.setStatus(0); // 待执行
        task.setDeadline(request.getDeadline());

        PatrolTask savedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(savedTask);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long id, Integer status) {
        PatrolTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        task.setStatus(status);
        
        if (status == 1) { // 执行中
            task.setStartTime(LocalDateTime.now());
        } else if (status == 3) { // 已完成
            task.setCompleteTime(LocalDateTime.now());
        }

        PatrolTask updatedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(updatedTask);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskCreateRequest request) {
        PatrolTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getTemplateId() != null) {
            task.setTemplateId(request.getTemplateId());
        }
        if (request.getOrgId() != null) {
            task.setOrgId(request.getOrgId());
        }
        if (request.getExecutorId() != null) {
            task.setExecutorId(request.getExecutorId());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDeadline() != null) {
            task.setDeadline(request.getDeadline());
        }

        PatrolTask updatedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("任务不存在");
        }
        taskRepository.deleteById(id);
    }

    public Long countByStatus(Integer status) {
        return taskRepository.countByStatus(status);
    }

    public Long countOverdueTasks() {
        return (long) taskRepository.findOverdueTasks(LocalDateTime.now()).size();
    }

    public Long countTotal() {
        return taskRepository.count();
    }
}
