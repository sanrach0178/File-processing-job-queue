package com.example.jobqueue.dto;

import com.example.jobqueue.model.JobStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobResponse {
    private Long id;
    private String payload;
    private JobStatus status;
    private String result;
    private int retriesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
