package com.devopsplayground.taskmanager.controller;

import com.devopsplayground.taskmanager.dto.TaskCreateRequest;
import com.devopsplayground.taskmanager.dto.TaskUpdateRequest;
import com.devopsplayground.taskmanager.model.Task;
import com.devopsplayground.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_WithValidData_ShouldReturnCreated() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest("Test Task");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createTask_WithBlankTitle_ShouldReturnBadRequest() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest("");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTasks_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTaskById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest("Updated Task", true);

        mockMvc.perform(put("/tasks/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}