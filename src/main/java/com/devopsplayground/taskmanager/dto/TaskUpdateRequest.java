package com.devopsplayground.taskmanager.dto;

import jakarta.validation.constraints.Size;

public class TaskUpdateRequest {
    
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    
    private Boolean done;
    
    // Constructors
    public TaskUpdateRequest() {}
    
    public TaskUpdateRequest(String title, Boolean done) {
        this.title = title;
        this.done = done;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Boolean getDone() {
        return done;
    }
    
    public void setDone(Boolean done) {
        this.done = done;
    }
}