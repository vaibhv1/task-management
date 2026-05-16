package com.taskmanagement.infrastructure.repository;

import com.taskmanagement.domain.model.Status;
import com.taskmanagement.domain.model.Task;
import com.taskmanagement.domain.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryTaskRepository implements TaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    public void clear() {
        tasks.clear();
    }

    @Override
    public Task save(Task task) {
        tasks.removeIf(t -> t.getId().equals(task.getId()));
        tasks.add(task);
        return task;
    }

    @Override
    public Optional<Task> findById(String id) {
        return tasks.stream()
            .filter(task -> task.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public List<Task> findByStatus(Status status) {
        return tasks.stream()
            .filter(task -> task.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        tasks.removeIf(task -> task.getId().equals(id));
    }

    @Override
    public boolean existsById(String id) {
        return tasks.stream()
            .anyMatch(task -> task.getId().equals(id));
    }
}