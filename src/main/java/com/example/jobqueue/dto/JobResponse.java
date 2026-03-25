package com.example.jobqueue.dto;

import com.example.jobqueue.model.JobStatus;
import lombok.Data;
import java.time.LocalDateTime;

public class JobResponse {
    private Long id;
    private String payload;
    private JobStatus status;
    private String result;
    private int retriesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public int getRetriesCount() { return retriesCount; }
    public void setRetriesCount(int retriesCount) { this.retriesCount = retriesCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
