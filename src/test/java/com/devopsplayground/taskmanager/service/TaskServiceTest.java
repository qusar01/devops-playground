package com.devopsplayground.taskmanager.service;

import com.devopsplayground.taskmanager.dto.TaskCreateRequest;
import com.devopsplayground.taskmanager.dto.TaskUpdateRequest;
import com.devopsplayground.taskmanager.model.Task;
import com.devopsplayground.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task("Test Task 1");
        task1.setId(1L);
        
        task2 = new Task("Test Task 2");
        task2.setId(2L);
        task2.setDone(true);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Given
        List<Task> expectedTasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(expectedTasks);

        // When
        List<Task> result = taskService.getAllTasks();

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedTasks, result);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        // When
        Optional<Task> result = taskService.getTaskById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(task1, result.get());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Task> result = taskService.getTaskById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() {
        // Given
        TaskCreateRequest request = new TaskCreateRequest("New Task");
        Task savedTask = new Task("New Task");
        savedTask.setId(3L);
        
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        Task result = taskService.createTask(request);

        // Then
        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        assertEquals(false, result.getDone());
        assertEquals(3L, result.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_WhenTaskExists_ShouldReturnUpdatedTask() {
        // Given
        TaskUpdateRequest request = new TaskUpdateRequest("Updated Title", true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        // When
        Optional<Task> result = taskService.updateTask(1L, request);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals(true, result.get().getDone());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task1);
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldReturnEmpty() {
        // Given
        TaskUpdateRequest request = new TaskUpdateRequest("Updated Title", true);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Task> result = taskService.updateTask(999L, request);

        // Then
        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldReturnTrue() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // When
        boolean result = taskService.deleteTask(1L);

        // Then
        assertTrue(result);
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldReturnFalse() {
        // Given
        when(taskRepository.existsById(anyLong())).thenReturn(false);

        // When
        boolean result = taskService.deleteTask(999L);

        // Then
        assertFalse(result);
        verify(taskRepository, times(1)).existsById(999L);
        verify(taskRepository, never()).deleteById(anyLong());
    }
}