package com.devopsplayground.taskmanager.controller;

import com.devopsplayground.taskmanager.dto.TaskCreateRequest;
import com.devopsplayground.taskmanager.dto.TaskUpdateRequest;
import com.devopsplayground.taskmanager.model.Task;
import com.devopsplayground.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresql::getJdbcUrl);
        registry.add("spring.datasource.username", postgresql::getUsername);
        registry.add("spring.datasource.password", postgresql::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void getAllTasks_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllTasks_WhenTasksExist_ShouldReturnAllTasks() throws Exception {
        // Given
        Task task1 = taskRepository.save(new Task("Task 1"));
        Task task2 = taskRepository.save(new Task("Task 2", true));

        // When & Then
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(task1.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is("Task 1")))
                .andExpect(jsonPath("$[0].done", is(false)))
                .andExpect(jsonPath("$[1].id", is(task2.getId().intValue())))
                .andExpect(jsonPath("$[1].title", is("Task 2")))
                .andExpect(jsonPath("$[1].done", is(true)));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        // Given
        Task savedTask = taskRepository.save(new Task("Test Task"));

        // When & Then
        mockMvc.perform(get("/tasks/{id}", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedTask.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.done", is(false)));
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_WithValidData_ShouldCreateTask() throws Exception {
        // Given
        TaskCreateRequest request = new TaskCreateRequest("New Task");

        // When & Then
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.done", is(false)))
                .andExpect(jsonPath("$.id", notNullValue()));

        assertEquals(1, taskRepository.count());
    }

    @Test
    void createTask_WithBlankTitle_ShouldReturn400() throws Exception {
        // Given
        TaskCreateRequest request = new TaskCreateRequest("");

        // When & Then
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0, taskRepository.count());
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateTask() throws Exception {
        // Given
        Task savedTask = taskRepository.save(new Task("Original Task"));
        TaskUpdateRequest request = new TaskUpdateRequest("Updated Task", true);

        // When & Then
        mockMvc.perform(put("/tasks/{id}", savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedTask.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.done", is(true)));
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldReturn404() throws Exception {
        // Given
        TaskUpdateRequest request = new TaskUpdateRequest("Updated Task", true);

        // When & Then
        mockMvc.perform(put("/tasks/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldDeleteTask() throws Exception {
        // Given
        Task savedTask = taskRepository.save(new Task("Task to delete"));

        // When & Then
        mockMvc.perform(delete("/tasks/{id}", savedTask.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, taskRepository.count());
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}