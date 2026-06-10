package com.yixin.patrol.service;

import com.yixin.patrol.dto.TaskReportCreateRequest;
import com.yixin.patrol.dto.TaskReviewRequest;
import com.yixin.patrol.entity.PatrolTask;
import com.yixin.patrol.entity.TaskReport;
import com.yixin.patrol.repository.PatrolTaskRepository;
import com.yixin.patrol.repository.TaskReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskReportServiceTest {

    @Mock
    private TaskReportRepository taskReportRepository;

    @Mock
    private PatrolTaskRepository patrolTaskRepository;

    @InjectMocks
    private TaskReportService taskReportService;

    @Test
    void submitReportShouldCreateReportAndMoveTaskToPendingReview() {
        PatrolTask task = new PatrolTask();
        task.setId(10L);
        task.setExecutorId(7L);
        task.setStatus(1);

        TaskReportCreateRequest request = new TaskReportCreateRequest();
        request.setTaskId(10L);
        request.setReportContent("完成校园消防巡检");
        request.setCheckResult("[{\"item\":\"消防器材\",\"passed\":true}]");
        request.setLocation("实验楼A座");
        request.setImages("[\"img-1.png\"]");

        when(patrolTaskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskReportRepository.save(any(TaskReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        taskReportService.submitReport(request, 7L);

        ArgumentCaptor<TaskReport> reportCaptor = ArgumentCaptor.forClass(TaskReport.class);
        verify(taskReportRepository).save(reportCaptor.capture());
        assertEquals(10L, reportCaptor.getValue().getTaskId());
        assertEquals(7L, reportCaptor.getValue().getReportUserId());
        assertEquals(0, reportCaptor.getValue().getReviewStatus());
        assertEquals(2, task.getStatus());
        verify(patrolTaskRepository).save(task);
    }

    @Test
    void reviewReportShouldCompleteTaskWhenApproved() {
        PatrolTask task = new PatrolTask();
        task.setId(20L);
        task.setStatus(2);

        TaskReport report = new TaskReport();
        report.setId(5L);
        report.setTaskId(20L);
        report.setReviewStatus(0);
        report.setTask(task);

        TaskReviewRequest request = new TaskReviewRequest();
        request.setApproved(true);
        request.setReviewComment("检查通过");

        when(taskReportRepository.findById(5L)).thenReturn(Optional.of(report));
        when(patrolTaskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(taskReportRepository.save(any(TaskReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        taskReportService.reviewReport(5L, request, 99L);

        assertEquals(1, report.getReviewStatus());
        assertEquals("检查通过", report.getReviewComment());
        assertEquals(99L, report.getReviewUserId());
        assertEquals(3, task.getStatus());
        verify(taskReportRepository).save(report);
        verify(patrolTaskRepository).save(task);
    }

    @Test
    void reviewReportShouldRejectTaskWhenDenied() {
        PatrolTask task = new PatrolTask();
        task.setId(21L);
        task.setStatus(2);

        TaskReport report = new TaskReport();
        report.setId(6L);
        report.setTaskId(21L);
        report.setReviewStatus(0);
        report.setTask(task);

        TaskReviewRequest request = new TaskReviewRequest();
        request.setApproved(false);
        request.setReviewComment("请补充整改说明");

        when(taskReportRepository.findById(6L)).thenReturn(Optional.of(report));
        when(patrolTaskRepository.findById(21L)).thenReturn(Optional.of(task));
        when(taskReportRepository.save(any(TaskReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        taskReportService.reviewReport(6L, request, 100L);

        assertEquals(2, report.getReviewStatus());
        assertEquals(4, task.getStatus());
        verify(taskReportRepository).save(report);
        verify(patrolTaskRepository).save(task);
    }

    @Test
    void submitReportShouldRejectUnauthorizedExecutor() {
        PatrolTask task = new PatrolTask();
        task.setId(10L);
        task.setExecutorId(8L);
        task.setStatus(1);

        TaskReportCreateRequest request = new TaskReportCreateRequest();
        request.setTaskId(10L);
        request.setReportContent("content");

        when(patrolTaskRepository.findById(10L)).thenReturn(Optional.of(task));

        RuntimeException error = assertThrows(RuntimeException.class, () -> taskReportService.submitReport(request, 7L));
        assertEquals("只能由任务执行人提交上报", error.getMessage());
    }
}
