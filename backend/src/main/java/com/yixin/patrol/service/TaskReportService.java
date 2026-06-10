package com.yixin.patrol.service;

import com.yixin.patrol.dto.TaskReportCreateRequest;
import com.yixin.patrol.dto.TaskReportDTO;
import com.yixin.patrol.dto.TaskReviewRequest;
import com.yixin.patrol.entity.PatrolTask;
import com.yixin.patrol.entity.TaskReport;
import com.yixin.patrol.repository.PatrolTaskRepository;
import com.yixin.patrol.repository.TaskReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskReportService {

    @Autowired
    private TaskReportRepository taskReportRepository;

    @Autowired
    private PatrolTaskRepository patrolTaskRepository;

    public List<TaskReportDTO> getReportsByTaskId(Long taskId) {
        return taskReportRepository.findByTaskIdOrderByReportTimeDesc(taskId).stream()
                .map(TaskReportDTO::fromEntity)
                .toList();
    }

    public List<TaskReportDTO> getMyReports(Long userId) {
        return taskReportRepository.findByReportUserIdOrderByReportTimeDesc(userId).stream()
                .map(TaskReportDTO::fromEntity)
                .toList();
    }

    public List<TaskReportDTO> getPendingReviews() {
        return taskReportRepository.findPendingReviews().stream()
                .map(TaskReportDTO::fromEntity)
                .toList();
    }

    @Transactional
    public TaskReportDTO submitReport(TaskReportCreateRequest request, Long currentUserId) {
        PatrolTask task = patrolTaskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (!task.getExecutorId().equals(currentUserId)) {
            throw new RuntimeException("只能由任务执行人提交上报");
        }
        if (task.getStatus() != 1 && task.getStatus() != 4) {
            throw new RuntimeException("当前任务状态不允许提交上报");
        }

        TaskReport report = new TaskReport();
        report.setTaskId(request.getTaskId());
        report.setReportContent(request.getReportContent());
        report.setCheckResult(request.getCheckResult());
        report.setImages(request.getImages());
        report.setLocation(request.getLocation());
        report.setReportUserId(currentUserId);
        report.setReviewStatus(0);

        TaskReport savedReport = taskReportRepository.save(report);
        task.setStatus(2);
        patrolTaskRepository.save(task);

        savedReport.setTask(task);
        return TaskReportDTO.fromEntity(savedReport);
    }

    @Transactional
    public TaskReportDTO reviewReport(Long reportId, TaskReviewRequest request, Long currentUserId) {
        TaskReport report = taskReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("上报记录不存在"));
        if (report.getReviewStatus() != 0) {
            throw new RuntimeException("该上报已审核");
        }

        PatrolTask task = patrolTaskRepository.findById(report.getTaskId())
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        report.setReviewStatus(Boolean.TRUE.equals(request.getApproved()) ? 1 : 2);
        report.setReviewUserId(currentUserId);
        report.setReviewTime(LocalDateTime.now());
        report.setReviewComment(request.getReviewComment());

        if (Boolean.TRUE.equals(request.getApproved())) {
            task.setStatus(3);
            task.setCompleteTime(LocalDateTime.now());
        } else {
            task.setStatus(4);
        }

        taskReportRepository.save(report);
        patrolTaskRepository.save(task);
        report.setTask(task);
        return TaskReportDTO.fromEntity(report);
    }
}
