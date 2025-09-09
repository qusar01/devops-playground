package com.devopsplayground.taskmanager.service;

import com.devopsplayground.taskmanager.dto.TaskCreateRequest;
import com.devopsplayground.taskmanager.dto.TaskUpdateRequest;
import com.devopsplayground.taskmanager.model.Task;
import com.devopsplayground.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public Task createTask(TaskCreateRequest request) {
        Task task = new Task(request.getTitle());
        return taskRepository.save(task);
    }
    
    public Optional<Task> updateTask(Long id, TaskUpdateRequest request) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            
            if (request.getTitle() != null) {
                task.setTitle(request.getTitle());
            }
            
            if (request.getDone() != null) {
                task.setDone(request.getDone());
            }
            
            return Optional.of(taskRepository.save(task));
        }
        
        return Optional.empty();
    }
    
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}