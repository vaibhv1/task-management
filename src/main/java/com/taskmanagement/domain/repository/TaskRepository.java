package com.taskmanagement.domain.repository;

import com.taskmanagement.domain.model.Status;
import com.taskmanagement.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(String id);
    List<Task> findAll();
    List<Task> findByStatus(Status status);
    void deleteById(String id);
    boolean existsById(String id);
}