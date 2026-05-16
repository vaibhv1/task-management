package com.taskmanagement.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanagement.application.dto.CreateTaskRequest;
import com.taskmanagement.application.dto.UpdateTaskRequest;
import com.taskmanagement.domain.model.Status;
import com.taskmanagement.domain.repository.TaskRepository;
import com.taskmanagement.infrastructure.repository.InMemoryTaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        if (taskRepository instanceof InMemoryTaskRepository) {
            ((InMemoryTaskRepository) taskRepository).clear();
        }
    }

    @AfterEach
    void tearDown() {
        if (taskRepository instanceof InMemoryTaskRepository) {
            ((InMemoryTaskRepository) taskRepository).clear();
        }
    }

    @Test
    void createTask_shouldReturn201_withValidRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
            "Test Task",
            "Test Description",
            Status.PENDING,
            LocalDate.now().plusDays(7)
        );

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createTask_shouldReturn400_withMissingTitle() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
            null,
            "Test Description",
            Status.PENDING,
            LocalDate.now().plusDays(7)
        );

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void createTask_shouldReturn400_withPastDueDate() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
            "Test Task",
            "Test Description",
            Status.PENDING,
            LocalDate.now().minusDays(1)
        );

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.dueDate").exists());
    }

    @Test
    void getTaskById_shouldReturn200_withExistingTask() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
            "Test Task",
            "Test Description",
            Status.PENDING,
            LocalDate.now().plusDays(7)
        );

        String createResponse = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        mockMvc.perform(get("/tasks/" + taskId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(taskId))
            .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getTaskById_shouldReturn404_withNonExistingTask() throws Exception {
        mockMvc.perform(get("/tasks/non-existent-id"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Task not found with id: non-existent-id"));
    }

    @Test
    void updateTask_shouldReturn200_withValidRequest() throws Exception {
        CreateTaskRequest createRequest = new CreateTaskRequest(
            "Original Title",
            "Original Description",
            Status.PENDING,
            LocalDate.now().plusDays(7)
        );

        String createResponse = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        UpdateTaskRequest updateRequest = new UpdateTaskRequest(
            "Updated Title",
            "Updated Description",
            Status.IN_PROGRESS,
            LocalDate.now().plusDays(10)
        );

        mockMvc.perform(put("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateTask_shouldReturn404_withNonExistingTask() throws Exception {
        UpdateTaskRequest updateRequest = new UpdateTaskRequest(
            "Updated Title",
            null,
            null,
            null
        );

        mockMvc.perform(put("/tasks/non-existent-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Task not found with id: non-existent-id"));
    }

    @Test
    void deleteTask_shouldReturn204_withExistingTask() throws Exception {
        CreateTaskRequest createRequest = new CreateTaskRequest(
            "Task to Delete",
            "Description",
            Status.PENDING,
            LocalDate.now().plusDays(7)
        );

        String createResponse = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        mockMvc.perform(delete("/tasks/" + taskId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/" + taskId))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_shouldReturn404_withNonExistingTask() throws Exception {
        mockMvc.perform(delete("/tasks/non-existent-id"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Task not found with id: non-existent-id"));
    }

    @Test
    void listTasks_shouldReturn200_withAllTasksSortedByDueDate() throws Exception {
        CreateTaskRequest task1 = new CreateTaskRequest("Task 1", null, Status.PENDING, LocalDate.now().plusDays(3));
        CreateTaskRequest task2 = new CreateTaskRequest("Task 2", null, Status.PENDING, LocalDate.now().plusDays(1));
        CreateTaskRequest task3 = new CreateTaskRequest("Task 3", null, Status.PENDING, LocalDate.now().plusDays(5));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task1)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task2)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task3)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[0].title").value("Task 2"))
            .andExpect(jsonPath("$.content[1].title").value("Task 1"))
            .andExpect(jsonPath("$.content[2].title").value("Task 3"));
    }

    @Test
    void listTasks_shouldFilterByStatus() throws Exception {
        CreateTaskRequest pendingTask = new CreateTaskRequest("Pending Task", null, Status.PENDING, LocalDate.now().plusDays(1));
        CreateTaskRequest doneTask = new CreateTaskRequest("Done Task", null, Status.DONE, LocalDate.now().plusDays(2));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pendingTask)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doneTask)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks")
                .param("status", "PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].title").value("Pending Task"));
    }

    @Test
    void listTasks_shouldPaginate() throws Exception {
        for (int i = 1; i <= 15; i++) {
            CreateTaskRequest task = new CreateTaskRequest(
                "Task " + i,
                null,
                Status.PENDING,
                LocalDate.now().plusDays(i)
            );
            mockMvc.perform(post("/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/tasks")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)))
            .andExpect(jsonPath("$.totalElements").value(15))
            .andExpect(jsonPath("$.totalPages").value(3));
    }
}