package com.taskmanagement.domain.service;

import com.taskmanagement.domain.model.Status;
import com.taskmanagement.domain.model.Task;

import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    Task getTaskById(String id);
    Task updateTask(String id, Task task);
    void deleteTask(String id);
    List<Task> listAllTasks(Status status, String direction);
}