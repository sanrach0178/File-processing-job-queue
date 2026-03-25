package com.example.jobqueue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JobRequest {
    @NotBlank(message = "Payload cannot be empty")
    @Size(max = 255, message = "Payload is too long")
    private String payload;

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
