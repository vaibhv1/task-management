package com.taskmanagement.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class Task {
    private final String id;
    private String title;
    private String description;
    private Status status;
    private LocalDate dueDate;

    public Task(String title, String description, Status status, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.status = status != null ? status : Status.PENDING;
        this.dueDate = dueDate;
    }

    public Task(String id, String title, String description, Status status, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}