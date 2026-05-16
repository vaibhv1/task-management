package com.taskmanagement;

import com.taskmanagement.domain.repository.TaskRepository;
import com.taskmanagement.infrastructure.repository.InMemoryTaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public TaskRepository taskRepository() {
        return new InMemoryTaskRepository();
    }
}