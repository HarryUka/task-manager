package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;

@SpringBootTest
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.PENDING);
    }

    @Test
    void createTask_ShouldReturnSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task savedTask = taskService.createTask(testTask);
        
        assertNotNull(savedTask);
        assertEquals("Test Task", savedTask.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        
        Optional<Task> found = taskService.getTaskById(1L);
        
        assertTrue(found.isPresent());
        assertEquals("Test Task", found.get().getTitle());
    }

    @Test
    void updateTask_WhenTaskExists_ShouldReturnUpdatedTask() {
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.updateTask(1L, updatedTask);

        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }

    @Test
    void updateTask_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(1L, new Task());
        });
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(testTask));
        
        List<Task> tasks = taskService.getAllTasks();
        
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void deleteTask_ShouldCallRepositoryDelete() {
        taskService.deleteTask(1L);
        verify(taskRepository).deleteById(1L);
    }
} 