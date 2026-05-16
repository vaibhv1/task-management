package com.taskmanagement.web.controller;

import com.taskmanagement.application.dto.CreateTaskRequest;
import com.taskmanagement.application.dto.TaskResponse;
import com.taskmanagement.application.dto.UpdateTaskRequest;
import com.taskmanagement.domain.model.Status;
import com.taskmanagement.domain.model.Task;
import com.taskmanagement.domain.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = new Task(
            request.getTitle(),
            request.getDescription(),
            request.getStatus() != null ? request.getStatus() : Status.PENDING,
            request.getDueDate()
        );
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.fromEntity(createdTask));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String id,
            @Valid @RequestBody UpdateTaskRequest request) {
        Task task = new Task(
            request.getTitle(),
            request.getDescription(),
            request.getStatus(),
            request.getDueDate()
        );
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(TaskResponse.fromEntity(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> listTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String direction) {

        List<Task> allTasks = taskService.listAllTasks(status, direction);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allTasks.size());

        List<Task> pageContent = start < allTasks.size()
            ? allTasks.subList(start, end)
            : List.of();

        Page<TaskResponse> taskPage = new org.springframework.data.domain.PageImpl<>(
            pageContent.stream().map(TaskResponse::fromEntity).collect(Collectors.toList()),
            pageable,
            allTasks.size()
        );

        return ResponseEntity.ok(taskPage);
    }
}