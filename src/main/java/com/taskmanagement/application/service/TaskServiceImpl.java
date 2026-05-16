package com.taskmanagement.application.service;

import com.taskmanagement.domain.model.Status;
import com.taskmanagement.domain.model.Task;
import com.taskmanagement.domain.repository.TaskRepository;
import com.taskmanagement.domain.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(String id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Override
    public Task updateTask(String id, Task task) {
        Task existingTask = getTaskById(id);

        if (task.getTitle() != null) {
            existingTask.setTitle(task.getTitle());
        }
        if (task.getDescription() != null) {
            existingTask.setDescription(task.getDescription());
        }
        if (task.getStatus() != null) {
            existingTask.setStatus(task.getStatus());
        }
        if (task.getDueDate() != null) {
            existingTask.setDueDate(task.getDueDate());
        }

        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(String id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> listAllTasks(Status status, String direction) {
        List<Task> tasks = status != null
            ? taskRepository.findByStatus(status)
            : taskRepository.findAll();

        Comparator<Task> comparator = Comparator.comparing(Task::getDueDate);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return tasks.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
}