package com.taskmanagement.application.service;

import com.taskmanagement.domain.model.Status;
import com.taskmanagement.domain.model.Task;
import com.taskmanagement.domain.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository);
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {
        Task task = new Task("Test Task", "Description", Status.PENDING, LocalDate.now().plusDays(7));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask(task);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        String id = "test-id-123";
        Task task = new Task("Test Task", "Description", Status.PENDING, LocalDate.now().plusDays(7));
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(id);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void getTaskById_shouldThrowException_whenNotExists() {
        String id = "non-existent-id";
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(id));
    }

    @Test
    void updateTask_shouldUpdateAndReturnTask() {
        String id = "test-id-123";
        Task existingTask = new Task("Old Title", "Old Description", Status.PENDING, LocalDate.now().plusDays(7));
        Task updatedTask = new Task("New Title", "New Description", Status.IN_PROGRESS, LocalDate.now().plusDays(10));

        when(taskRepository.findById(id)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(id, updatedTask);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void updateTask_shouldThrowException_whenNotExists() {
        String id = "non-existent-id";
        Task updatedTask = new Task("New Title", null, null, null);

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(id, updatedTask));
    }

    @Test
    void deleteTask_shouldDelete_whenExists() {
        String id = "test-id-123";
        when(taskRepository.existsById(id)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(id);

        assertDoesNotThrow(() -> taskService.deleteTask(id));
        verify(taskRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteTask_shouldThrowException_whenNotExists() {
        String id = "non-existent-id";
        when(taskRepository.existsById(id)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(id));
    }

    @Test
    void listAllTasks_shouldReturnAllTasks_sortedByDueDate() {
        Task task1 = new Task("Task 1", null, Status.PENDING, LocalDate.now().plusDays(3));
        Task task2 = new Task("Task 2", null, Status.PENDING, LocalDate.now().plusDays(1));
        Task task3 = new Task("Task 3", null, Status.PENDING, LocalDate.now().plusDays(5));

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2, task3));

        List<Task> result = taskService.listAllTasks(null, "asc");

        assertEquals(3, result.size());
        assertEquals("Task 2", result.get(0).getTitle());
        assertEquals("Task 1", result.get(1).getTitle());
        assertEquals("Task 3", result.get(2).getTitle());
    }

    @Test
    void listAllTasks_shouldFilterByStatus() {
        Task pendingTask = new Task("Pending Task", null, Status.PENDING, LocalDate.now().plusDays(1));
        Task doneTask = new Task("Done Task", null, Status.DONE, LocalDate.now().plusDays(2));

        when(taskRepository.findByStatus(Status.PENDING)).thenReturn(List.of(pendingTask));

        List<Task> result = taskService.listAllTasks(Status.PENDING, "asc");

        assertEquals(1, result.size());
        assertEquals("Pending Task", result.get(0).getTitle());
    }

    @Test
    void listAllTasks_shouldReturnTasksInDescendingOrder_whenDirectionIsDesc() {
        Task task1 = new Task("Task 1", null, Status.PENDING, LocalDate.now().plusDays(3));
        Task task2 = new Task("Task 2", null, Status.PENDING, LocalDate.now().plusDays(1));
        Task task3 = new Task("Task 3", null, Status.PENDING, LocalDate.now().plusDays(5));

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2, task3));

        List<Task> result = taskService.listAllTasks(null, "desc");

        assertEquals(3, result.size());
        assertEquals("Task 3", result.get(0).getTitle());
        assertEquals("Task 1", result.get(1).getTitle());
        assertEquals("Task 2", result.get(2).getTitle());
    }
}